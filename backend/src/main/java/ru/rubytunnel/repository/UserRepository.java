package ru.rubytunnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.rubytunnel.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByChatId(Long chatId);

    Optional<User> findByReferalCode(Long referalCode);

    @Query("SELECT COUNT(u) FROM User u WHERE u.referalCode = :chatId AND u.active = true AND u.demo = false")
    Long countActiveReferrals(@Param("chatId") String chatId);

    Optional<User> findByPayLabel(String payLabel);

    @Override
    List<User> findAll();

    List<User> findByActive(Boolean active);

    List<User> findByDemo(Boolean demo);

    @Query("SELECT u FROM User u WHERE u.subscriptionEndDate < CURRENT_TIMESTAMP AND u.active = true")
    List<User> findExpiredSubscriptions();

    @Query("SELECT u FROM User u WHERE u.referalCode = :code")
    List<User> findAllByReferalCode(@Param("code") Long code);

    boolean existsByChatId(Long chatId);

    // 1) Подписка заканчивается завтра,
    //    т.е. subscription_end_date попадает в интервал [сейчас, сейчас + 1 день],
    //    при этом active = true и informStatus = false
    @Query("""
           SELECT u
           FROM User u
           WHERE u.subscriptionEndDate IS NOT NULL
             AND u.active = true
             AND u.informStatus = false
             AND u.subscriptionEndDate > CURRENT_TIMESTAMP
             AND u.subscriptionEndDate <= :tomorrow
           """)
    List<User> findUsersExpiringTomorrow(@Param("tomorrow") LocalDateTime tomorrow);

    // 2) Подписка уже истекла: subscription_end_date <= сейчас
    //    И пользователь до сих пор active = true
    @Query("""
           SELECT u
           FROM User u
           WHERE u.subscriptionEndDate IS NOT NULL
             AND u.active = true
             AND u.subscriptionEndDate <= CURRENT_TIMESTAMP
           """)
    List<User> findUsersExpired();
}
