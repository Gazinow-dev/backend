package com.gazi.gazi_renew.notification.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteByMyFindRoadPathId(Long myFindRoadPathId);
}
