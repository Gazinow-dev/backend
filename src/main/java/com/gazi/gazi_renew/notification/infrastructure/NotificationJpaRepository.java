package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByMyFindRoadPathEntityId(Long myFindRoadPathEntityId);

    void deleteByMyFindRoadPathEntityId(Long myFindRoadPathEntityId);
}
