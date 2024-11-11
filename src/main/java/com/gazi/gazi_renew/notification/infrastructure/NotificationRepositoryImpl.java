package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
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

    public void deleteByMyFindRoad(MyFindRoad myFindRoad) {
        notificationJpaRepository.deleteByMyFindRoadPathEntity(MyFindRoadPathEntity.from(myFindRoad));
        // TODO : REMOVE 먼저 되는지 확인
        notificationJpaRepository.flush();
    }

    @Override
    public void saveAll(List<Notification> notificationList) {
        List<NotificationEntity> notificationEntityList = notificationList.stream().map(NotificationEntity::from)
                .collect(Collectors.toList());

        notificationJpaRepository.saveAll(notificationEntityList);
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return notificationJpaRepository.findById(notificationId).map(NotificationEntity::toModel);
    }
}
