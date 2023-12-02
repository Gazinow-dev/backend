package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFindLoadRepository extends JpaRepository<MyFindLoad, Long> {
}
