package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

}
