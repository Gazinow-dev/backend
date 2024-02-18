package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.IssueResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import com.gazi.gazi_renew.repository.LikeRepository;
import com.gazi.gazi_renew.repository.MemberRepository;
import com.gazi.gazi_renew.repository.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final LikeRepository likeRepository;
    private final SubwayRepository subwayRepository;
    private final MemberRepository memberRepository;
    private final Response response;


    @Value("${issue.code}")
    private String secretCode;

    @Override
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto) {

        try {
            if (!dto.getSecretCode().equals(secretCode)) {
                return response.fail("인증코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            List<Station> stationList = getStationList(dto.getStations());

            Issue issue = Issue.builder()
                    .crawlingNo(dto.getCrawlingNo())
                    .startDate(dto.getStartDate().withSecond(0).withNano(0))
                    .expireDate(dto.getExpireDate().withSecond(0).withNano(0))
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .stations(stationList)
                    .keyword(dto.getKeyword())
                    .build();

            issueRepository.save(issue);
            // station에도 추가되어야한다.
            for (Station station : stationList) {
                List<Issue> issues = station.getIssues();
                issues.add(issue);
                station.setIssues(issues);
                subwayRepository.save(station);
            }
            return response.success();
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getIssue(Long id) {
        try {
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            Issue issue = issueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
            IssueResponse issueResponse = IssueResponse.builder()
                    .id(issue.getId())
                    .title(issue.getTitle())
                    .content(issue.getContent())
                    .isLike(likeRepository.existsByIssueAndMember(issue,member))
                    .keyword(issue.getKeyword())
                    .line(issue.getLine())
                    .stationDtos(IssueResponse.getStations(issue.getStations()))
                    .startDate(issue.getStartDate())
                    .expireDate(issue.getExpireDate())
                    .agoTime(getTime(issue.getStartDate()))
                    .build();
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
        Page<Issue> issuePage = issueRepository.findAll(pageable);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage, "이슈 전체 조회 성공", HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getLineByIssues(String line, Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findALlByLine(line, pageable);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage, "line" + "이슈 조회 성공", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> getPopularIssues(Pageable pageable) {
        int likeCount = 5;
        try {
            Page<Issue> issuePage = issueRepository.findTopIssuesByLikesCount(likeCount, pageable);
            Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);
            return response.success(issueResponsePage, "인기 이슈 조회 성공", HttpStatus.OK);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
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
                    .line(m.getLine())
                    .startDate(m.getStartDate())
                    .expireDate(m.getExpireDate())
                    .agoTime(getTime(m.getStartDate()));

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
        for (IssueRequest.Station station : stations) {
            List<Station> findStationList = subwayRepository.findByStationCodeBetween(station.getStartStationCode(), station.getEndStationCode());
            for (Station stationEntity : findStationList) {
                stationResponse.add(stationEntity);
            }
        }
        return stationResponse;
    }

    // 시간 구하기 로직
    public static String getTime(LocalDateTime startTime) {

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
    public  List<Issue> getActiveIssues(List<Issue> issues){
        List<Issue> issueList = issueRepository.findByExpireDateAfter(LocalDateTime.now());
        return issueList;
    }

}
