package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.notification.infrastructure.jpa.NotificationHistoryJpaRepository;
import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationHistoryRepositoryImpl implements NotificationHistoryRepository {
    private final NotificationHistoryJpaRepository notificationHistoryJpaRepository;
    @Override
    public NotificationHistory save(NotificationHistory notificationHistory) {
        return notificationHistoryJpaRepository.save(NotificationHistoryEntity.from(notificationHistory)).toModel();
    }

    @Override
    public Page<NotificationHistory> findAllByMemberId(Long memberId, Pageable pageable) {
        List<NotificationHistory> notificationHistoryList = notificationHistoryJpaRepository.findAllByMemberId(memberId)
                .stream()
                .map(NotificationHistoryEntity::toModel)
                .sorted(Comparator.comparing(NotificationHistory::getStartDate).reversed())
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), notificationHistoryList.size());
        List<NotificationHistory> pagedList = notificationHistoryList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, notificationHistoryList.size());
    }

    @Override
    public void updateNotificationIsRead(Long id) {
        notificationHistoryJpaRepository.updateNotificationIsRead(id);
    }

    @Override
    public Long countByMemberIdAndReadFalse(Long memberId) {
        return notificationHistoryJpaRepository.countByMemberIdAndReadFalse(memberId);
    }

    @Override
    public void saveAll(List<NotificationHistory> histories) {
        List<NotificationHistoryEntity> historyEntityList = histories.stream()
                .map(NotificationHistoryEntity::from).toList();

        notificationHistoryJpaRepository.saveAll(historyEntityList);
    }
}
