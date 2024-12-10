package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId) {
        return notificationJpaRepository.findByMyFindRoadPathId(myFindRoadPathId).stream()
                .map(NotificationEntity::toModel).collect(Collectors.toList());
    }

    public void deleteByMyFindRoadId(Long myFindRoadId) {
        notificationJpaRepository.deleteByMyFindRoadPathId(myFindRoadId);
        notificationJpaRepository.flush();
    }

    @Override
    public List<Notification> saveAll(List<Notification> notificationList) {
        List<NotificationEntity> notificationEntityList = notificationList.stream().map(NotificationEntity::from)
                .collect(Collectors.toList());

        return notificationJpaRepository.saveAll(notificationEntityList).stream()
                .map(NotificationEntity::toModel).collect(Collectors.toList());
    }
    @Override
    public Optional<Notification> findById(Long notificationId) {
        return notificationJpaRepository.findById(notificationId).map(NotificationEntity::toModel);
    }
}
