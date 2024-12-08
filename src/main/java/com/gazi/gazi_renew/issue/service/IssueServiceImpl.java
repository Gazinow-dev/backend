package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import com.gazi.gazi_renew.issue.service.kafka.NotificationSender;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.station.service.port.LineRepository;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final LikeRepository likeRepository;
    private final SubwayRepository subwayRepository;
    private final MemberRepository memberRepository;
    private final LineRepository lineRepository;
    private final RedisUtilService redisUtilService;
    private final SecurityUtilService securityUtilService;
    private final IssueLineRepository issueLineRepository;
    private final IssueStationRepository issueStationRepository;
    private final KafkaSender kafkaSender;

    @Value("${issue.code}")
    private String secretCode;
    private static final int minStationNo = 201;
    private static final int maxStationNo = 243;
    private static final int likeCount = 5;

    @Override
    @Transactional
    public boolean addIssue(IssueCreate issueCreate) throws JsonProcessingException {
        if (!issueCreate.getSecretCode().equals(secretCode)) {
            throw ErrorCode.throwInvalidVerificationCode();
        }

        if (issueRepository.existsByCrawlingNo(issueCreate.getCrawlingNo())) {
            throw ErrorCode.throwDuplicateIssueException();
        }
        Issue issue = Issue.from(issueCreate);
        issue = issueRepository.save(issue);
        issueRepository.flush();
        List<Station> stationList = getStationList(issueCreate.getStations());


        for (Station station : stationList) {
            IssueStation issueStation = IssueStation.from(issue, station);
            issueStationRepository.save(issueStation);

        }
        List<Line> lineList = issueCreate.getLines().stream()
                .map(lineName -> lineRepository.findByLineName(lineName)
                        .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: " + lineName))
                )
                .collect(Collectors.toList());

        for (Line line : lineList) {
            IssueLine issueLine = IssueLine.from(issue, line);
            issueLineRepository.save(issueLine);

        }
        // Redis에 이슈 추가
        addIssueToRedis(issue);
        kafkaSender.sendNotification(issue, lineList, stationList);
        return true;
    }
    /**
     * redis에 issue 저장
     * @param : Issue issue
     */
    private void addIssueToRedis(Issue issue) throws JsonProcessingException {
        IssueRedisDto issueRedisDto = new IssueRedisDto(issue.getStartDate().atZone(ZoneId.systemDefault()).toEpochSecond(),
                issue.getExpireDate().atZone(ZoneId.systemDefault()).toEpochSecond());

        redisUtilService.addIssueToRedis("issues", issue.getId().toString(), issueRedisDto);
    }
    /**
     * 이슈 세부사항 조회
     * 해당 메서드에서만 IssueDetail 사용(isLike포함)
     * @param id
     * @return IssueDetail
     */
    @Override
    @Transactional(readOnly = true)
    public IssueStationDetail getIssue(Long id) {
        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());

        Issue issue = issueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
        boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());

        return getIssueStationDetail(issue, isLike);
    }
    @Override
    @Transactional(readOnly = true)
    public IssueStationDetail getIssueStationDetail(Issue issue, boolean isLike) {
        List<IssueStation> issueStationList = issueStationRepository.findAllByIssue(issue);

        List<Station> stationList = issueStationList.stream()
                .map(IssueStation::getStation).collect(Collectors.toList());

        List<IssueLine> issueLineList = issueLineRepository.findAllByIssue(issue);

        List<Line> lineList = issueLineList.stream().map(IssueLine::getLine)
                .collect(Collectors.toList());

        return IssueStationDetail.from(issue, stationList, lineList, isLike);
    }

    /**
     * 이슈 전체 조회
     * @param  pageable
     * @return Page<IssueStationDetail>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IssueStationDetail> getIssues(Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findAll(pageable);

        // Page<Issue> -> Page<IssueStationDetail>로 변환
        Page<IssueStationDetail> issueStationDetailPage = issuePage.map(issue -> {
            // isLike 계산을 위해 현재 사용자의 좋아요 상태 확인
            Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());

            return getIssueStationDetail(issue, isLike);
        });

        return issueStationDetailPage;
    }

    /**
     * 호선별 이슈 조회
     *
     * @param : String line, Pageable pageable
     * @return Page<Issue>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IssueStationDetail> getLineByIssues(String lineName, Pageable pageable) {
        Line line = lineRepository.findByLineName(lineName).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 호선입니다.")
        );

        Page<Issue> sortedIssues = issueLineRepository.findByLineId(line.getId(), pageable);

        // Page<Issue> -> Page<IssueStationDetail>로 변환
        Page<IssueStationDetail> issueStationDetailPage = sortedIssues.map(issue -> {
            // isLike 계산을 위해 현재 사용자의 좋아요 상태 확인
            Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());

            return getIssueStationDetail(issue, isLike);
        });

        return issueStationDetailPage;
    }
    /**
     * 좋아요 숫자가 5개 이상인 이슈 조회
     *
     * @param
     * @return IssueResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueStationDetail> getPopularIssues() {
        List<Issue> issueList = issueRepository.findTopIssuesByLikesCount(likeCount, PageRequest.of(0, 4));

        List<IssueStationDetail> issueStationDetailList = issueList.stream().map(issue -> {
            Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());
            return getIssueStationDetail(issue, isLike);
        }).collect(Collectors.toList());

        return issueStationDetailList;
    }
    /**
     * issue 내용 update
     * @param : IssueUpdate issueUpdate
     * @return void
     */
    @Override
    @Transactional
    public void updateIssue(IssueUpdate issueUpdate){
        Issue issue = issueRepository.findById(issueUpdate.getId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 이슈입니다.")
        );
        issue = issue.update(issueUpdate);
        issueRepository.updateIssue(issue);
    }
    public List<Station> getStationList(List<IssueCreate.Station> issueStations) {
        List<Station> stationEntityResponse = new ArrayList<>();

        // 입력된 모든 역 정보를 순회하며 각 역을 처리
        for (IssueCreate.Station issueStation : issueStations) {
            // 2호선인 경우와 아닌 경우 분기 처리
            if (issueStation.getLine().equals("수도권 2호선")) {
                List<Station> stationEntityList = handleLineTwo(issueStation, issueStation.getStartStationCode(), issueStation.getEndStationCode());
                stationEntityResponse.addAll(stationEntityList);
            } else {
                int startStationCode = Math.min(issueStation.getStartStationCode(), issueStation.getEndStationCode());
                int endStationCode = Math.max(issueStation.getStartStationCode(), issueStation.getEndStationCode());

                List<Station> stationEntityList = findStationsForOtherLines(startStationCode, endStationCode);
                stationEntityResponse.addAll(stationEntityList);
            }
        }
        return stationEntityResponse;
    }

    /**
     * 2호선의 경우, 시계방향과 반시계방향에 따라 다르게 처리
     * @param issueStation IssueRequest의 역 정보
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> handleLineTwo(IssueCreate.Station issueStation, int startStationCode, int endStationCode) {
        if (issueStation.getDirection() == SubwayDirection.Clockwise) {
            // 2호선 내선 처리
            return handleClockwiseDirection(startStationCode, endStationCode);
        } else if (issueStation.getDirection() == SubwayDirection.Counterclockwise) {
            // 2호선 외선 처리
            return handleCounterClockwiseDirection(startStationCode, endStationCode);
        }
        //지선인 경우
        else{
            // 시작역이 끝역 보다 작게
            if (startStationCode > endStationCode) {
                int temp = startStationCode;
                startStationCode = endStationCode;
                endStationCode = temp;
            }
            return findStationsForOtherLines(startStationCode, endStationCode);
        }
    }

    /**
     * 시계방향(내선)일 때의 역 구간 처리
     * @param startIssueStationCode 출발역 코드
     * @param endIssueStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> handleClockwiseDirection(int startIssueStationCode, int endIssueStationCode) {
        if (startIssueStationCode < endIssueStationCode) {
            // 구간이 연속되는 경우
            return subwayRepository.findByIssueStationCodeBetween(startIssueStationCode, endIssueStationCode);
        } else {
            // 구간이 원형을 넘어가는 경우 (예: 역 번호가 큰 출발역에서 작은 도착역으로 이동)
            return getStationsForCircularRoute(startIssueStationCode, endIssueStationCode);
        }
    }

    /**
     * 반시계방향(외선)일 때의 역 구간 처리
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> handleCounterClockwiseDirection(int startStationCode, int endStationCode) {
        // 반시계일때, 구간이 시작역이 끝역코드보다 커야 연속
        if (startStationCode > endStationCode) {
            return subwayRepository.findByIssueStationCodeBetween(endStationCode, startStationCode);
        } else {
            return getStationsForCircularRoute(endStationCode, startStationCode);
        }
    }

    /**
     * 원형을 넘는 구간을 처리하는 메서드
     * 구간이 역 번호의 최대값에서 시작하여 다시 처음으로 돌아가는 경우 두 구간으로 나누어 처리
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> getStationsForCircularRoute(int startStationCode, int endStationCode) {
        // 구간을 두 부분으로 나누어 처리
        List<Station> leftList = subwayRepository.findByIssueStationCodeBetween(startStationCode, maxStationNo); // 최대역 번호까지의 구간
        List<Station> rightList = subwayRepository.findByIssueStationCodeBetween(minStationNo, endStationCode); // 최소역 번호부터 구간

        // 두 구간의 결과를 합침
        leftList.addAll(rightList);
        return leftList;
    }

    /**
     * 2호선 이외의 다른 노선에 대한 역 구간 처리
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> findStationsForOtherLines(int startStationCode, int endStationCode) {
        return subwayRepository.findByIssueStationCodeBetween(startStationCode, endStationCode);
    }

    @Override
    @Transactional
    public void deleteIssue(Long issueId) {
        issueStationRepository.deleteIssueStationByIssueId(issueId);
        issueLineRepository.deleteIssueLineByIssueId(issueId);
        issueRepository.deleteIssue(issueId);

    }


}
