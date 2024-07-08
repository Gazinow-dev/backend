package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteByMyFindRoadPath(MyFindRoadPath myFindRoadPath);
}
