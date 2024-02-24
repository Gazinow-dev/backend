package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Transactional(readOnly=true)
    Page<Issue> findALlByLine(String line, Pageable pageable);
    @Transactional(readOnly=true)
    List<Issue> findALlByLine(String line);
    boolean existsByCrawlingNo(String crawlingNo);
    List<Issue> findByStations_StationCode(int stationCode);

    // Like의 수가 N개 이상인 Issue를 Like의 수에 따라 내림차순으로 정렬하여 반환
    @Transactional(readOnly=true)
    @Query("SELECT i FROM Issue i WHERE SIZE(i.likes) >= :likesCount ORDER BY SIZE(i.likes) DESC")
    List<Issue> findTopIssuesByLikesCount(@Param("likesCount") int likesCount, Pageable pageable);

    // 현재시간보다 expireDate가 지나지 않았으면 가져오기
    List<Issue> findByExpireDateAfter(LocalDateTime currentTime);

    boolean existsByLatestNo(int latestNo);
    @Query("SELECT i FROM Issue i JOIN i.lines l " +
            "WHERE i.expireDate > CURRENT_TIMESTAMP " +
            "AND l = :line")
    List<Issue> findActiveIssuesForLine(@Param("line") Line line);
}
