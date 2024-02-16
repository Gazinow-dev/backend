package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Transactional(readOnly=true)
    Page<Issue> findALlByLine(String line, Pageable pageable);

    boolean existsByCrawlingNo(String crawlingNo);
    List<Issue> findByStations_StationCode(int stationCode);

    // Like의 수가 N개 이상인 Issue를 Like의 수에 따라 내림차순으로 정렬하여 반환
    @Transactional(readOnly=true)
    @Query("SELECT i FROM Issue i WHERE SIZE(i.likes) >= :likesCount ORDER BY SIZE(i.likes) DESC")
    Page<Issue> findTopIssuesByLikesCount(@Param("likesCount") int likesCount, Pageable pageable);
}
