package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.admin.service.port.MemberPenaltyRepository;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.dto.*;
import com.gazi.gazi_renew.issue.service.port.*;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import com.gazi.gazi_renew.station.domain.Station;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.station.service.port.LineRepository;
import com.gazi.gazi_renew.station.service.port.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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
    private final MemberPenaltyRepository memberPenaltyRepository;
    private final KafkaSender kafkaSender;
    private final ClockHolder clockHolder;

    private static final int likeCount = 5;

    /**
     * 이슈 세부사항 조회
     * 해당 메서드에서만 IssueDetail 사용(isLike포함)
     * @param id
     * @return IssueDetail
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueStationDetail> getIssue(Long id) {
        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
        List<IssueStationDetail> issueStationDetailList = issueRepository.getIssueById(id);
        if (issueStationDetailList.isEmpty()) {
            throw new EntityNotFoundException("존재하지 않는 이슈입니다.");
        }

        boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(id, member.get().getId());
        boolean memberRestricted = member.isPresent() && memberPenaltyRepository.isMemberRestricted(member.get().getId());
        List<IssueStationDetail> result = issueStationDetailList.stream()
                .map(issueStationDetail ->
                {
                    issueStationDetail = issueStationDetail.fromLike(isLike);
                    return issueStationDetail.restrictedWriteComment(memberRestricted);
                }).collect(Collectors.toList());
        return result;
    }
    /**
     * 이슈 전체 조회
     * @param pageable
     * @return Page<IssueStationDetail>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IssueStationDetail> getIssues(Pageable pageable) {
        Page<IssueStationDetail> issuePage = issueRepository.findAll(pageable);
        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
        // Page<Issue> -> Page<IssueStationDetail>로 변환
        Page<IssueStationDetail> issueStationDetailPage = issuePage.map(issue -> {
            // isLike 계산을 위해 현재 사용자의 좋아요 상태 확인
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());
            issue = issue.fromLike(isLike);
            return issue;
        });

        return issueStationDetailPage;
    }

    /**
     * 호선별 이슈 조회
     * @param : String line, Pageable pageable
     * @return Page<Issue>
     */
    @Override
    @Transactional(readOnly = true)
    public Page<IssueStationDetail> getLineByIssues(String lineName, Pageable pageable) {
        Page<IssueStationDetail> issueList = issueRepository.getIssueByLineName(lineName, pageable);

        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());
        // Page<Issue> -> Page<IssueStationDetail>로 변환
        Page<IssueStationDetail> issueStationDetailPage = issueList.map(issue -> {
            // isLike 계산을 위해 현재 사용자의 좋아요 상태 확인
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());
            issue = issue.fromLike(isLike);
            return issue;
        });
        return issueStationDetailPage;
    }

    /**
     * 좋아요 숫자가 5개 이상인 이슈 조회
     * @param
     * @return IssueResponse
     */
    @Override
    @Transactional(readOnly = true)
    public List<IssueStationDetail> getPopularIssues() {
         List<IssueStationDetail> issueStationDetails = issueRepository.findTopIssuesByLikesCount(likeCount, PageRequest.of(0, 4));
        Optional<Member> member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail());

        List<IssueStationDetail> issueStationDetailList = issueStationDetails.stream().map(issue -> {
            boolean isLike = member.isPresent() && likeRepository.existsByIssueAndMember(issue.getId(), member.get().getId());
            issue = issue.fromLike(isLike);
            return issue;
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
    public void updateIssue(IssueUpdate issueUpdate) {
        Issue issue = issueRepository.findById(issueUpdate.getId()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 이슈입니다.")
        );
        issue = issue.update(issueUpdate);
        issueRepository.updateIssue(issue);
    }

    @Override
    public void deleteIssue(Long issueId) {
        issueStationRepository.deleteIssueStationByIssueId(issueId);
        issueLineRepository.deleteIssueLineByIssueId(issueId);
        issueRepository.deleteIssue(issueId);

    }

    @Override
    public Issue autoRegisterInternalIssue(InternalIssueCreate internalIssueCreate) throws JsonProcessingException {
        validateDuplicateIssue(internalIssueCreate.getCrawlingNo());
        Issue issue = Issue.fromInternalIssue(internalIssueCreate);
        Optional<Issue> issueOptional = issueRepository.findByIssueKey(internalIssueCreate.getIssueKey());

        if (!issueOptional.isEmpty()) {
            return updateExistingIssueDates(issue, issueOptional);
        }
        issue = issueRepository.save(issue);
        //구간 처리 필요하면 (지하철역)
        if (internalIssueCreate.getProcessRange()) {
            processIssueByRange(internalIssueCreate, issue);
        }
        // 전체 호선 처리 필요
        else if (!internalIssueCreate.getLines().isEmpty() && internalIssueCreate.getLocations().isEmpty()) {
            processIssueByLines(internalIssueCreate, issue);
        }
        // 단일 처리
        else {
            processIssueByStations(internalIssueCreate, issue);
        }
        return issue;
    }

    @Override
    public Issue autoRegisterExternalIssue(ExternalIssueCreate externalIssueCreate) throws JsonProcessingException {
        validateDuplicateIssue(externalIssueCreate.getCrawlingNo());
        // issueKey로 저장된 이슈 있는지 확인
        Issue issue = Issue.fromExternalIssue(externalIssueCreate);

        Optional<Issue> issueOptional = issueRepository.findByIssueKey(externalIssueCreate.getIssueKey());
        if (!issueOptional.isEmpty()) {
            return updateExistingIssueDates(issue, issueOptional);
        }
        issue = issueRepository.save(issue);
        return processStationsAndLinesForExternalIssue(externalIssueCreate, issue);
    }

    /**
     * 2호선 이외의 다른 노선에 대한 역 구간 처리
     *
     * @param startStationCode 출발역 코드
     * @param endStationCode   도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    public List<Station> findStationsForOtherLines(int startStationCode, int endStationCode) {
        return subwayRepository.findByIssueStationCodeBetween(startStationCode, endStationCode);
    }

    private Issue processStationsAndLinesForExternalIssue(ExternalIssueCreate externalIssueCreate, Issue issue) throws JsonProcessingException {
        List<ExternalIssueCreate.Stations> stations = externalIssueCreate.getStations();
        List<Station> stationList = new ArrayList<>();
        List<Line> lineList = new ArrayList<>();
        for (ExternalIssueCreate.Stations station : stations) {
            List<Station> stationSubList = subwayRepository.findByNameContainingAndLine(station.getName(), station.getLine());

            Station firstStation = Station.toFirstStation(station.getName(), stationSubList);
            stationList.add(firstStation);

            saveIssueStation(issue, firstStation);

            Line line = saveIssueLine(issue, station.getLine());
            lineList.add(line);
        }
        // Redis에 이슈 추가
        addIssueToRedis(issue);
        kafkaSender.sendNotification(issue, lineList, stationList);
        return issue;
    }

    private void saveIssueStation(Issue issue, Station station) {
        IssueStation issueStation = IssueStation.from(issue, station);
        issueStationRepository.save(issueStation);
    }

    private Line saveIssueLine(Issue issue, String lineName) {
        Line line = lineRepository.findByLineName(lineName)
                .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: "));
        IssueLine issueLine = IssueLine.from(issue, line);
        issueLineRepository.save(issueLine);
        return line;
    }

    private Issue updateExistingIssueDates(Issue issue, Optional<Issue> issueOptional) {
        Issue existIssue = issueOptional.get();
        existIssue = existIssue.updateDate(clockHolder, issue.getStartDate(), issue.getExpireDate());
        issueRepository.updateStartDateAndExpireDate(existIssue.getId(), existIssue.getStartDate(), existIssue.getExpireDate());

        return existIssue;
    }

    private List<Station> findByStartStationAndEndStationBetween(Station startStation, Station endStation) {
        int startStationCode = Math.min(startStation.getIssueStationCode(), endStation.getIssueStationCode());
        int endStationCode = Math.max(startStation.getIssueStationCode(), endStation.getIssueStationCode());

        return findStationsForOtherLines(startStationCode, endStationCode);
    }

    private void validateDuplicateIssue(String crawlingNo) {
        if (issueRepository.existsByCrawlingNo(crawlingNo)) {
            throw ErrorCode.throwDuplicateIssueException();
        }
    }

    private List<Station> getStationRangeList(List<String> stations, String line) {
        List<Station> stationList = new ArrayList<>();
        for (String station : stations) {
            List<Station> stationSubList = subwayRepository.findByNameContainingAndLine(station, line);
            Station result = Station.toFirstStation(station, stationSubList);

            stationList.add(result);
        }
        return stationList;

    }

    private void processIssueByStations(InternalIssueCreate internalIssueCreate, Issue issue) throws JsonProcessingException {
        String lineName = internalIssueCreate.getLines().get(0);

        Line line = saveIssueLine(issue, lineName);
        List<Station> stationList = new ArrayList<>();
        for (String stationName : internalIssueCreate.getLocations()) {
            List<Station> stationSubList = subwayRepository.findByNameContainingAndLine(stationName, lineName);
            Station station = Station.toFirstStation(stationName, stationSubList);

            saveIssueStation(issue, station);

            stationList.add(station);
        }
        addIssueToRedis(issue);
        kafkaSender.sendNotification(issue, List.of(line), stationList);
    }

    private void processIssueByLines(InternalIssueCreate internalIssueCreate, Issue issue) throws JsonProcessingException {
        List<Line> lineList = new ArrayList<>();
        for (String lineName : internalIssueCreate.getLines()) {
            Line line = saveIssueLine(issue, lineName);
            lineList.add(line);

            List<Station> stationList = subwayRepository.findByLine(lineName);
            for (Station station : stationList) {
                saveIssueStation(issue, station);
            }
            addIssueToRedis(issue);
            kafkaSender.sendNotification(issue, lineList, stationList);
        }
    }

    private void processIssueByRange(InternalIssueCreate internalIssueCreate, Issue issue) throws JsonProcessingException {
        String lineName = internalIssueCreate.getLines().get(0);
        List<Station> stationRangeList = getStationRangeList(internalIssueCreate.getLocations(), lineName);
        Station firstStation = stationRangeList.get(0);
        Station endStation = stationRangeList.get(1);

        List<Station> stationList = findByStartStationAndEndStationBetween(firstStation, endStation);
        Line line = saveIssueLine(issue, lineName);

        for (Station station : stationList) {
            saveIssueStation(issue, station);
        }
        addIssueToRedis(issue);
        kafkaSender.sendNotification(issue, List.of(line), stationList);
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
}
