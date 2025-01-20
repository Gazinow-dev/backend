package com.gazi.gazi_renew.issue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.domain.dto.*;
import com.gazi.gazi_renew.issue.service.port.IssueLineRepository;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
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

    private static final int likeCount = 5;
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
    @Override
    @Transactional
    public void deleteIssue(Long issueId) {
        issueStationRepository.deleteIssueStationByIssueId(issueId);
        issueLineRepository.deleteIssueLineByIssueId(issueId);
        issueRepository.deleteIssue(issueId);

    }
    private List<Station> getStationRangeList(List<String> stations, String line) {
        List<Station> stationList = new ArrayList<>();
        for (String station : stations) {
            Station result = subwayRepository.findCoordinateByNameAndLine(station, line);
            stationList.add(result);
        }
        return stationList;

    }
    @Override
    public Issue autoRegisterInternalIssue(InternalIssueCreate internalIssueCreate) throws JsonProcessingException {
        if (issueRepository.existsByCrawlingNo(internalIssueCreate.getCrawlingNo())) {
            throw ErrorCode.throwDuplicateIssueException();
        }
        Issue issue = Issue.fromInternalIssue(internalIssueCreate);
        Optional<Issue> issueOptional=issueRepository.findByIssueKey(internalIssueCreate.getIssueKey());

        if (!issueOptional.isEmpty()) {
            Issue existIssue = issueOptional.get();
            existIssue = existIssue.updateDate(issue.getStartDate(), issue.getExpireDate());
            issueRepository.updateStartDateAndExpireDate(existIssue.getId(), existIssue.getStartDate(), existIssue.getExpireDate());
        }else {
            //구간 처리 필요하면 (지하철역)
            if (internalIssueCreate.getProcessRange()) {
                String lineName = internalIssueCreate.getLines().get(0);
                List<Station> stationRangeList = getStationRangeList(internalIssueCreate.getLocations(), lineName);

                Line line = lineRepository.findByLineName(lineName)
                        .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: "));

                IssueLine issueLine = IssueLine.from(issue, line);
                issueLineRepository.save(issueLine);
                Station firstStation = stationRangeList.get(0);
                Station endStation = stationRangeList.get(1);

                List<Station> stationList = findByStartStationAndEndStationBetween(firstStation, endStation);

                for (Station station : stationList) {
                    IssueStation issueStation = IssueStation.from(issue, station);
                    issueStationRepository.save(issueStation);
                }
                addIssueToRedis(issue);

                kafkaSender.sendNotification(issue, List.of(line), stationList);
            }
            // 전체 호선 처리 필요
            else if (!internalIssueCreate.getLines().isEmpty() && internalIssueCreate.getLocations().isEmpty()) {
                List<Line> lineList = new ArrayList<>();
                for (String lineName : internalIssueCreate.getLines()) {
                    Line line = lineRepository.findByLineName(lineName)
                            .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: "));
                    lineList.add(line);
                    IssueLine issueLine = IssueLine.from(issue, line);
                    issueLineRepository.save(issueLine);

                    List<Station> stationList = subwayRepository.findByLine(lineName);
                    for (Station station : stationList) {
                        IssueStation issueStation = IssueStation.from(issue, station);
                        issueStationRepository.save(issueStation);
                    }
                    addIssueToRedis(issue);
                    kafkaSender.sendNotification(issue, lineList, stationList);
                }
            }
            // 단일 처리
            else{
                String lineName = internalIssueCreate.getLines().get(0);

                Line line = lineRepository.findByLineName(lineName)
                        .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: "));
                IssueLine issueLine = IssueLine.from(issue, line);
                issueLineRepository.save(issueLine);
                List<Station> stationList = new ArrayList<>();
                for (String stationName : internalIssueCreate.getLocations()) {
                    List<Station> stationSubList = subwayRepository.findByNameContainingAndLine(stationName, lineName);
                    Station station = Station.toFirstStation(stationName, stationSubList);

                    IssueStation issueStation = IssueStation.from(issue, station);
                    issueStationRepository.save(issueStation);

                    stationList.add(station);
                }
                addIssueToRedis(issue);
                kafkaSender.sendNotification(issue, List.of(line), stationList);
            }
        }

    }

    @Override
    public Issue autoRegisterExternalIssue(ExternalIssueCreate externalIssueCreate) throws JsonProcessingException {
        if (issueRepository.existsByCrawlingNo(externalIssueCreate.getCrawlingNo())) {
            throw ErrorCode.throwDuplicateIssueException();
        }
        //TODO issueKey로 저장된 이슈 있는지 확인
        Issue issue = Issue.fromExternalIssue(externalIssueCreate);
        issue = issueRepository.save(issue);

        List<ExternalIssueCreate.Stations> stations = externalIssueCreate.getStations();
        List<Station> stationList = new ArrayList<>();
        List<Line> lineList = new ArrayList<>();

        //TODO 지하철 역 정보 조호ㅚ 필요
        Optional<Issue> issueOptional=issueRepository.findByIssueKey(externalIssueCreate.getIssueKey());
        if (!issueOptional.isEmpty()) {
            Issue existIssue = issueOptional.get();
            existIssue = existIssue.updateDate(issue.getStartDate(), issue.getExpireDate());
            issueRepository.updateStartDateAndExpireDate(existIssue.getId(), existIssue.getStartDate(), existIssue.getExpireDate());
        }
        else{
            for (ExternalIssueCreate.Stations station : stations) {
                List<Station> stationSubList = subwayRepository.findByNameContainingAndLine(station.getName(), station.getLine());

                Station firstStation = Station.toFirstStation(station.getName(), stationSubList);
                stationList.add(firstStation);

                IssueStation issueStation = IssueStation.from(issue, firstStation);
                issueStationRepository.save(issueStation);

                Line line = lineRepository.findByLineName(station.getLine())
                        .orElseThrow(() -> new EntityNotFoundException("호선이 존재하지 않습니다: "));
                lineList.add(line);
                IssueLine issueLine = IssueLine.from(issue, line);
                issueLineRepository.save(issueLine);
            }
            // Redis에 이슈 추가
            addIssueToRedis(issue);
            kafkaSender.sendNotification(issue, lineList, stationList);
        }
        return issue;
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
    private List<Station> findByStartStationAndEndStationBetween(Station startStation, Station endStation) {
        int startStationCode = Math.min(startStation.getIssueStationCode(), endStation.getIssueStationCode());
        int endStationCode = Math.max(startStation.getIssueStationCode(), endStation.getIssueStationCode());

        return findStationsForOtherLines(startStationCode, endStationCode);
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
