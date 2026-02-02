package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.infrastructure.entity.AdminNoticeEntity;
import com.gazi.gazi_renew.admin.infrastructure.jpa.AdminNoticeJpaRepositoryImpl;
import com.gazi.gazi_renew.admin.service.port.AdminNoticeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AdminNoticeRepositoryImpl implements AdminNoticeRepository {
    private final AdminNoticeJpaRepositoryImpl adminNoticeJpaRepositoryImpl;
    @Override
    public Page<AdminNotice> getNotifications(Pageable pageable) {
        return adminNoticeJpaRepositoryImpl.findAll(pageable)
                .map(AdminNoticeEntity::toModel);
    }

    @Override
    public AdminNotice getNotificationByNoticeId(Long noticeId) {
        return adminNoticeJpaRepositoryImpl.findById(noticeId).map(AdminNoticeEntity::toModel)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public AdminNotice saveNotification(AdminNotice adminNotice) {
        return adminNoticeJpaRepositoryImpl.save(AdminNoticeEntity.from(adminNotice)).toModel();
    }

    @Override
    public void deleteNotificationByNoticeId(Long noticeId) {
        adminNoticeJpaRepositoryImpl.deleteById(noticeId);
    }

    @Override
    public void updateNotification(AdminNotice adminNotice) {
        adminNoticeJpaRepositoryImpl.updateNotification(adminNotice.getNoticeId(), adminNotice.getNoticeTitle(),
                adminNotice.getNoticeContent(), adminNotice.getModifiedAt());
    }
}
