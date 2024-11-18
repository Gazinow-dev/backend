package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeNotificationRepository implements NotificationRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Notification> data = new ArrayList<>();
    @Override
    public List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId) {
        return data.stream()
                .filter(notification -> notification.getMyFindRoadPathId().equals(myFindRoadPathId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByMyFindRoad(MyFindRoad myFindRoad) {
        data.removeIf(notification -> notification.getMyFindRoadPathId().equals(myFindRoad));
    }

    @Override
    public List<Notification> saveAll(List<Notification> notificationList) {
        for (Notification notification : notificationList) {
            // ID가 없는 경우 새로운 ID를 부여하여 새로운 Notification 객체를 생성
            Notification notificationWithId = notification.getId() == null
                    ? Notification.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .dayOfWeek(notification.getDayOfWeek())
                    .fromTime(notification.getFromTime())
                    .toTime(notification.getToTime())
                    .myFindRoadPathId(notification.getMyFindRoadPathId())
                    .build()
                    : notification;  // ID가 있는 경우 그대로 사용

            data.add(notificationWithId);
        }
        return notificationList;
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        return data.stream()
                .filter(notification -> notification.getId().equals(notificationId))
                .findFirst();
    }
}
