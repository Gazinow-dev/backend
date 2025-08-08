package com.gazi.gazi_renew.member.infrastructure.jpa;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<MemberEntity> getReferenceByEmail(String email);

    Optional<MemberEntity> findByEmailAndNickName(String email, String nickname);
    @Modifying
    @Query("UPDATE MemberEntity m SET m.firebaseToken = :firebaseToken WHERE m.email = :email")
    void updateFireBaseToken(@Param("firebaseToken") String firebaseToken, @Param("email") String email);
    @Modifying
    @Query("UPDATE MemberEntity m SET m.nickName = :nickName WHERE m.id = :id")
    void updateNickname(@Param("id") Long id, @Param("nickName") String nickName);
    @Modifying
    @Query("UPDATE MemberEntity m SET m.password = :password WHERE m.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
    @Modifying
    @Query("UPDATE MemberEntity m " +
            "SET m.pushNotificationEnabled = :pushNotificationEnabled, " +
            "m.mySavedRouteNotificationEnabled = :mySavedRouteNotificationEnabled, " +
            "m.nextDayNotificationEnabled = :nextDayNotificationEnabled, " +
            "m.routeDetailNotificationEnabled = :routeDetailNotificationEnabled " +
            "WHERE m.email = :email")
    void updateAlertAgree(@Param("pushNotificationEnabled") Boolean pushNotificationEnabled,
                         @Param("mySavedRouteNotificationEnabled") Boolean mySavedRouteNotificationEnabled,
                          @Param("nextDayNotificationEnabled") Boolean nextDayNotificationEnabled,
                          @Param("routeDetailNotificationEnabled") Boolean routeDetailNotificationEnabled,
                         @Param("email") String email);
}
