package com.gazi.gazi_renew.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Line;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.domain.enums.SubwayDirection;
import com.gazi.gazi_renew.dto.IssueRedisDto;
import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.IssueResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final Response response;
    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${issue.code}")
    private String secretCode;
    private static final int minStationNo = 201;
    private static final int maxStationNo = 243;


    @Override
    @Transactional
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto) {

        try {
            if (!dto.getSecretCode().equals(secretCode)) {
                return response.fail("인증코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            if(issueRepository.existsByCrawlingNo(dto.getCrawlingNo())){
                return response.fail("이미 해당 데이터가 존재합니다.", HttpStatus.BAD_REQUEST);
            }
            List<Station> stationList = getStationList(dto.getStations());
            List<Line> lineList = getLineList(dto.getLines());
            Issue issue = Issue.builder()
                    .crawlingNo(dto.getCrawlingNo())
                    .startDate(dto.getStartDate().withSecond(0).withNano(0))
                    .expireDate(dto.getExpireDate().withSecond(0).withNano(0))
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .stations(stationList)
                    .keyword(dto.getKeyword())
                    .lines(lineList)
                    .latestNo(dto.getLatestNo())
                    .build();

            issueRepository.save(issue);
            // Redis에 이슈 추가
            addIssueToRedis(issue);

            // station에도 추가되어야한다.
            for (Station station : stationList) {
                List<Issue> issues = station.getIssues();
                issues.add(issue);
                station.setIssues(issues);
                subwayRepository.save(station);
            }

            // line에도 추가
            for (Line line : lineList) {
                List<Issue> issues = line.getIssues();
                issues.add(issue);
                line.setIssues(issues);
                lineRepository.save(line);
            }
            return response.success();
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    public void addIssueToRedis(Issue issue) throws JsonProcessingException {
        String issueId = issue.getId().toString();
        IssueRedisDto issueDto = new IssueRedisDto(issue.getStartDate().atZone(ZoneId.systemDefault()).toEpochSecond(),
                issue.getExpireDate().atZone(ZoneId.systemDefault()).toEpochSecond());
        redisTemplate.opsForHash().put("issues", issueId, issueDto);
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getIssue(Long id) {
        try {
            Optional<Member> member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail());

            Issue issue = issueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
            boolean isLike = false;
            if(member.isPresent()){
                isLike = likeRepository.existsByIssueAndMember(issue,member.get());
            }else{
                isLike = false;
            }
            IssueResponse issueResponse = IssueResponse.builder()
                    .id(issue.getId())
                    .title(issue.getTitle())
                    .content(issue.getContent())
                    .isLike(isLike)
                    .keyword(issue.getKeyword())
                    .lines(
                            issue.getLines().stream()
                                    .map(Line::getLineName)
                                    .collect(Collectors.toList())
                    )
                    .stationDtos(IssueResponse.getStations(issue.getStations()))
                    .startDate(issue.getStartDate())
                    .expireDate(issue.getExpireDate())
                    .agoTime(getTime(issue.getCreatedAt()))
                    .build();
            int likeCount = Optional.ofNullable(issue.getLikes())
                    .map(Set::size)
                    .orElse(0);
            issueResponse.setLikeCount(likeCount);
            return response.success(issueResponse, "이슈 조회 성공", HttpStatus.OK);
        }catch (EntityNotFoundException e){
            return response.fail(e.getMessage(),HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getIssues(Pageable pageable) {
        try{
            Page<Issue> issuePage = issueRepository.findAll(pageable);
            Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

            return response.success(issueResponsePage, "이슈 전체 조회 성공", HttpStatus.OK);
        } catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getLineByIssues(String line, Pageable pageable) {
        try {
            Line lineEntity = lineRepository.findByLineName(line).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 호선입니다.")
            );

            int start = (int)pageable.getOffset();
            int end = (start + pageable.getPageSize()) > lineEntity.getIssues().size()? lineEntity.getIssues().size() : (start + pageable.getPageSize());
            List<Issue> sortedList = lineEntity.getIssues().stream()
                    .sorted(Comparator.comparing(Issue::getStartDate).reversed()) // startDate를 기준으로 내림차순 정렬
                    .collect(Collectors.toList());
            sortedList = sortedList.subList(start, end);
            Page<Issue> sortedIssues = new PageImpl<>(sortedList, pageable, sortedList.size());
            Page<IssueResponse> issueResponsePage = getPostDtoPage(sortedIssues);

            return response.success(issueResponsePage, "line" + "이슈 조회 성공", HttpStatus.OK);

        } catch (EntityNotFoundException e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getPopularIssues() {
        int likeCount = 5;
        try{
            //            Page<Issue> issuePage = issueRepository.findTopIssuesByLikesCount(likeCount, pageable);
            List<Issue> issueList = issueRepository.findTopIssuesByLikesCount(likeCount, PageRequest.of(0, 4));
            List<IssueResponse> issueResponseList = issueList.stream().map(
                    m -> {
                        IssueResponse.IssueResponseBuilder builder = IssueResponse.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .content(m.getContent())
                                .keyword(m.getKeyword())
                                .stationDtos(IssueResponse.getStations(m.getStations()))
                                .lines(
                                        m.getLines().stream()
                                                .map(Line::getLineName)
                                                .collect(Collectors.toList())
                                )
                                .startDate(m.getStartDate())
                                .expireDate(m.getExpireDate())
                                .agoTime(getTime(m.getCreatedAt()));

                        int likeCountDto = Optional.ofNullable(m.getLikes())
                                .map(Set::size)
                                .orElse(0);

                        builder.likeCount(likeCountDto);

                        return builder.build();
                    }
            ).collect(Collectors.toList());
            return response.success(issueResponseList, "인기 이슈 조회 성공", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Response.Body> updateIssueContent(IssueRequest.updateContentDto dto){
        try {
            System.out.println(dto.getId());
            Issue issue = issueRepository.findById(dto.getId()).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 이슈입니다.")
            );

            issue.setContent(dto.getContent());
            issueRepository.save(issue);
            return response.success(" 내용 수정 성공");
        }catch (EntityNotFoundException e){
            return response.fail(e.getMessage(),HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }



    }
    public Page<IssueResponse> getPostDtoPage(Page<Issue> issuePage) {

        Page<IssueResponse> issueResponsePage = issuePage.map(m -> {
            IssueResponse.IssueResponseBuilder builder = IssueResponse.builder()
                    .id(m.getId())
                    .title(m.getTitle())
                    .content(m.getContent())
                    .keyword(m.getKeyword())
                    .stationDtos(IssueResponse.getStations(m.getStations()))
                    .lines(
                            m.getLines().stream()
                                    .map(Line::getLineName)
                                    .collect(Collectors.toList())
                    )
                    .startDate(m.getStartDate())
                    .expireDate(m.getExpireDate())
                    .agoTime(getTime(m.getCreatedAt()));

            int likeCount = Optional.ofNullable(m.getLikes())
                    .map(Set::size)
                    .orElse(0);

            builder.likeCount(likeCount);

            return builder.build();
        });

        return issueResponsePage;
    }


    // 역코드로 해당역에 이슈가 있는지 파악하는 함수
    public List<IssueResponse.IssueSummaryDto> getIssueByStationCode(int stationCode) {
        List<Issue> issues = issueRepository.findByStations_StationCode(stationCode);
        List<IssueResponse.IssueSummaryDto> issueResponses = (List<IssueResponse.IssueSummaryDto>) issues.stream().map(
                m -> {
                    IssueResponse.IssueSummaryDto.IssueSummaryDtoBuilder builder = IssueResponse.IssueSummaryDto.builder()
                            .id(m.getId())
                            .title(m.getTitle())
                            .keyword(m.getKeyword());

                    int likeCount = Optional.ofNullable(m.getLikes())
                            .map(Set::size)
                            .orElse(0);
                    builder.likeCount(likeCount);
                    return builder.build();
                }


        ).collect(Collectors.toList());
        return issueResponses;
    }
    public List<Station> getStationList(List<IssueRequest.Station> stations) {
        List<Station> stationResponse = new ArrayList<>();

        // 입력된 모든 역 정보를 순회하며 각 역을 처리
        for (IssueRequest.Station station : stations) {
            // 2호선인 경우와 아닌 경우 분기 처리
            if (station.getLine().equals("수도권 2호선")) {
                List<Station> stationList = handleLineTwo(station, station.getStartStationCode(), station.getEndStationCode());
                stationResponse.addAll(stationList);
            } else {
                int startStationCode = Math.min(station.getStartStationCode(), station.getEndStationCode());
                int endStationCode = Math.max(station.getStartStationCode(), station.getEndStationCode());

                List<Station> stationList = findStationsForOtherLines(startStationCode, endStationCode);
                stationResponse.addAll(stationList);
            }
        }
        return stationResponse;
    }

    public List<Line> getLineList(List<String> lines){
        List<Line> lineResponse = new ArrayList<>();
        for(String line : lines){
            Line lineEntity = lineRepository.findByLineName(line).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 호선입니다.")
            );
            lineResponse.add(lineEntity);
        }
        return lineResponse;
    }
    // 시간 구하기 로직
    public static String getTime(LocalDateTime startTime) {
        System.out.println(startTime);

        LocalDateTime nowDate = LocalDateTime.now();
        Duration duration = Duration.between(startTime, nowDate);
        Long time = duration.getSeconds();
        String formatTime;

        if (time > 60 && time <= 3600) {
            // 분
            time = time / 60;
            formatTime = time + "분 전";
        } else if (time > 3600 && time <= 86400) {
            time = time / (60 * 60);
            formatTime = time + "시간 전";
        } else if (time > 86400) {
            time = time / 86400;
            formatTime = time + "일 전";
        } else {
            formatTime = time + "초 전";
        }

        return formatTime;
    }

    // 만료된 이슈를 제외하고 가지고 오는 함수
    public  List<Issue> getActiveIssues(Line line){
        List<Issue> issueList = issueRepository.findActiveIssuesForLine(line);
        for(Issue issue : issueList){
            issue.getId();

            System.out.println(issue.getTitle());
        }
        return issueList;
    }

    public List<Issue> getIssuesByLine(String lineName){
        List<Issue> issueList = issueRepository.findALlByLine(lineName);
        return issueList;
    }
    /**
     * 2호선의 경우, 시계방향과 반시계방향에 따라 다르게 처리
     * @param station IssueRequest의 역 정보
     * @param startStationCode 출발역 코드
     * @param endStationCode 도착역 코드
     * @return 구간에 해당하는 Station 목록
     */
    private List<Station> handleLineTwo(IssueRequest.Station station, int startStationCode, int endStationCode) {
        if (station.getDirection() == SubwayDirection.Clockwise) {
            // 2호선 내선 처리
            return handleClockwiseDirection(startStationCode, endStationCode);
        } else if (station.getDirection() == SubwayDirection.Counterclockwise) {
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
    private List<Station> handleCounterClockwiseDirection(int startStationCode, int endStationCode) {
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
