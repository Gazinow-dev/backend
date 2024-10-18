package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteByMyFindRoadPath(MyFindRoadPath myFindRoadPath);

    @Query("SELECT n FROM Notification n WHERE n.myFindRoadPath.id IN :myFindRoadPathIds AND n.dayOfWeek = :dayOfWeek AND n.fromTime <= :currentTime AND n.toTime >= :currentTime")
    List<Notification> findNotificationsForCurrentTime(@Param("myFindRoadPathIds") List<Long> myFindRoadPathIds, @Param("dayOfWeek") String dayOfWeek, @Param("currentTime") LocalTime currentTime);
}
