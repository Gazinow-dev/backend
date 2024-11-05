package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.common.exception.custom.DuplicateIssueException;
import com.gazi.gazi_renew.common.exception.custom.UnauthorizedException;
import com.gazi.gazi_renew.issue.domain.IssueCreate;
import com.gazi.gazi_renew.issue.domain.IssueDetail;
import com.gazi.gazi_renew.issue.domain.IssueUpdate;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.station.infrastructure.LineRepository;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.station.infrastructure.SubwayRepository;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.station.domain.enums.SubwayDirection;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final MemberJpaRepository memberJpaRepository;
    private final LineRepository lineRepository;
    private final RedisTemplate redisTemplate;

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

        List<Station> stationList = getStationList(issueCreate.getStations());
        Issue issue = Issue.from(issueCreate, stationList);
        List<Line> lineList = getLineList(issue.getLines());
        issueRepository.save(issue);

        // Redis에 이슈 추가
        addIssueToRedis(issue);

        // station에 추가
        for (Station station : stationList) {
            List<Issue> issueList = station.getIssueList();
            issueList.add(issue);
            station.addIssue(issueList);
            subwayRepository.save(station);
        }

        // line에 추가
        for (Line line : lineList) {
            List<Issue> issueList = line.getIssueList();
            issueList.add(issue);
            line.addIssue(issueList);
            lineRepository.save(line);
        }
        return true;
    }
    /**
     * redis에 issue 저장
     * @param : Issue issue
     */
    public void addIssueToRedis(Issue issue) throws JsonProcessingException {
        IssueRedisDto issueRedisDto = new IssueRedisDto(issue.getStartDate().atZone(ZoneId.systemDefault()).toEpochSecond(),
                issue.getExpireDate().atZone(ZoneId.systemDefault()).toEpochSecond());

        redisTemplate.opsForHash().put("issues", issue.getId().toString() , issueRedisDto);
    }
    /**
     * 이슈 세부사항 조회
     * 해당 메서드에서만 IssueDetail 사용(isLike포함)
     * @param id
     * @return IssueDetail
     */
    @Override
    @Transactional(readOnly = true)
    public IssueDetail getIssue(Long id) {
        //TOdo member 도메인으로 변경
        Optional<MemberEntity> member = memberJpaRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail());

        Issue issue = issueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
        //TOdo : like 도메인으로 변경
        boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue, member.get());
        return new IssueDetail(issue, isLike);
    }
    /**
     * 이슈 전체 조회
     * @param  pageable
     * @return Page<Issue>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Issue> getIssues(Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findAll(pageable);
        return issuePage;
    }
    /**
     * 호선별 이슈 조회
     * @param : String line, Pageable pageable
     * @return Page<Issue>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Issue> getLineByIssues(String line, Pageable pageable) {
        //TOdo : line 도메인으로 변경
        Line line = lineRepository.findByLineName(line).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 호선입니다.")
        );

        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > line.getIssueList().size()? line.getIssueList().size() : (start + pageable.getPageSize());
        List<Issue> sortedList = line.getIssueList().stream()
                .sorted(Comparator.comparing(Issue::getStartDate).reversed()) // startDate를 기준으로 내림차순 정렬
                .collect(Collectors.toList());
        sortedList = sortedList.subList(start, end);

        Page<Issue> sortedIssues = new PageImpl<>(sortedList, pageable, sortedList.size());
        return sortedIssues;
    }
    /**
     * 좋아요 숫자가 5개 이상인 이슈 조회
     * @param
     * @return IssueResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<Issue> getPopularIssues() {
        List<Issue> issueList = issueRepository.findTopIssuesByLikesCount(likeCount, PageRequest.of(0, 4));
        return issueList;
    }
    /**
     * issue 내용 update
     * @param : IssueUpdate issueUpdate
     * @return void
     */
    @Override
    @Transactional
    public void updateIssueContent(IssueUpdate issueUpdate){
        Issue issue = issueRepository.findById(issueUpdate.getId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 이슈입니다.")
        );
        issue.update(issueUpdate);
        issueRepository.save(issue);
    }
    private List<Station> getStationList(List<IssueCreate.Station> issueStations) {
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

    public List<Line> getLineList(List<String> lines){
        List<Line> lineEntityResponse = new ArrayList<>();
        for(String lineName : lines){
            Line line = lineRepository.findByLineName(lineName).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 호선입니다.")
            );
            lineEntityResponse.add(line);
        }
        return lineEntityResponse;
    }

    /**
     * 2호선의 경우, 시계방향과 반시계방향에 따라 다르게 처리
     * @param issueStation IssueRequest의 역 정보
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    private List<Station> handleLineTwo(Issue.IssueStation issueStation, int startStationCode, int endStationCode) {
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
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    private List<Station> handleClockwiseDirection(int startStationCode, int endStationCode) {
        if (startStationCode < endStationCode) {
            // 구간이 연속되는 경우
            return subwayRepository.findByIssueStationCodeBetween(startStationCode, endStationCode);
        } else {
            // 구간이 원형을 넘어가는 경우 (예: 역 번호가 큰 출발역에서 작은 도착역으로 이동)
            return getStationsForCircularRoute(startStationCode, endStationCode);
        }
    }

    /**
     * 반시계방향(외선)일 때의 역 구간 처리
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    private List<StationEntity> handleCounterClockwiseDirection(int startStationCode, int endStationCode) {
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
    private List<Station> getStationsForCircularRoute(int startStationCode, int endStationCode) {
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
    private List<Station> findStationsForOtherLines(int startStationCode, int endStationCode) {
        return subwayRepository.findByIssueStationCodeBetween(startStationCode, endStationCode);
    }


}
