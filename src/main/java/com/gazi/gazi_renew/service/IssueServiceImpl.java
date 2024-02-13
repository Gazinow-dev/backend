package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.IssueResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import com.gazi.gazi_renew.repository.SubwayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService{

    private final IssueRepository issueRepository;
    private final SubwayRepository subwayRepository;
    private final Response response;


    @Value("${issue.code}")
    private String secretCode;

    @Override
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto) {

        if(!dto.getSecretCode().equals(secretCode) ){
            return response.fail("인증코드가 일치하지 않습니다.",HttpStatus.UNAUTHORIZED);
        }

        Issue issue = Issue.builder()
                .crawlingNo(dto.getCrawlingNo())
                .startDate(dto.getStartDate().withSecond(0).withNano(0))
                .expireDate(dto.getExpireDate().withSecond(0).withNano(0))
                .title(dto.getTitle())
                .content(dto.getContent())
                .stations(getStationList(dto.getStations()))
                .build();

        issueRepository.save(issue);
        return response.success();
    }

    @Override
    public ResponseEntity<Response.Body> getIssue(Long id) {
        Issue issue = issueRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
        IssueResponse issueResponse = IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
//                .date(issue.getDate().toString())
                .line(issue.getLine())
                .stationDtos(IssueResponse.getStations(issue.getStations()))
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .build();
        return response.success(issueResponse,"이슈 조회 성공", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> getIssues(Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findAll(pageable);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage,"이슈 전체 조회 성공", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> getLineByIssues(String line, Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findALlByLine(line,pageable);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage,"line"+"이슈 조회 성공", HttpStatus.OK);
    }
    public Page<IssueResponse> getPostDtoPage( Page<Issue> issuePage) {

        Page<IssueResponse> issueResponsePage = issuePage.map(m -> {
            IssueResponse.IssueResponseBuilder builder = IssueResponse.builder()
                    .id(m.getId())
                    .title(m.getTitle())
                    .content(m.getContent())
                    .stationDtos(IssueResponse.getStations(m.getStations()))
                    .line(m.getLine())
                    .startDate(m.getStartDate())
                    .expireDate(m.getExpireDate());

            int likeCount = Optional.ofNullable(m.getLikes())
                    .map(Set::size)
                    .orElse(0);

            builder.likeCount(likeCount);

            return builder.build();
        });

        return issueResponsePage;
    }


    // 역코드로 해당역에 이슈가 있는지 파악하는 함수
    public  List<IssueResponse.IssueSummaryDto> getIssueByStationCode(int stationCode){
        List<Issue> issues = issueRepository.findByStations_StationCode(stationCode);
        List<IssueResponse.IssueSummaryDto> issueResponses = (List<IssueResponse.IssueSummaryDto>) issues.stream().map(
                m ->{
                    IssueResponse.IssueSummaryDto.IssueSummaryDtoBuilder builder = IssueResponse.IssueSummaryDto.builder()
                            .id(m.getId())
                            .title(m.getTitle());

                    int likeCount = Optional.ofNullable(m.getLikes())
                            .map(Set::size)
                            .orElse(0);
                    builder.likeCount(likeCount);
                    return builder.build();
                }


        ).collect(Collectors.toList());
        return issueResponses;
    }

    public List<Station> getStationList(List<IssueRequest.Station> stations){
        List<Station> stationResponse = new ArrayList<>();
        for(IssueRequest.Station station : stations){
            List<Station> findStationList = subwayRepository.findByStationCodeBetween(station.getStartStationCode(),station.getEndStationCode());
            for(Station stationEntity: findStationList){
                stationResponse.add(stationEntity);
            }
        }
        return stationResponse;
    }


}
