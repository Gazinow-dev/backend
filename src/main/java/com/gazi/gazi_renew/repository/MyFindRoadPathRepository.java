package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyFindRoadPathRepository extends JpaRepository<MyFindRoadPath,Long> {
    List<MyFindRoadPath> findAllByMember(Member member);
}
