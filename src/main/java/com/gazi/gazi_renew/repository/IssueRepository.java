package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Transactional(readOnly=true)
    Page<Issue> findALlByLine(String line, Pageable pageable);
}
