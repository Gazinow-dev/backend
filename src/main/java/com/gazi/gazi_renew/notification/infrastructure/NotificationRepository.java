package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteByMyFindRoadPath(MyFindRoadPath myFindRoadPath);
}
