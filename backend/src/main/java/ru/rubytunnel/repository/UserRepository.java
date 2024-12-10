package ru.rubytunnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.rubytunnel.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // get_user_by_chat_id
    Optional<User> findByChatId(Long chatId);

    // get_users_referal_people
    Optional<User> findByReferalCode(Long referalCode);

    // count_active_referrals
    @Query("SELECT COUNT(u) FROM User u WHERE u.referalCode = :chatId AND u.active = true AND u.demo = false")
    Long countActiveReferrals(@Param("chatId") Long chatId);

    // get_all
    @Override
    List<User> findAll();

    // Дополнительные полезные методы
    List<User> findByActive(Boolean active);
    
    List<User> findByDemo(Boolean demo);
    
    @Query("SELECT u FROM User u WHERE u.subscriptionEndDate < CURRENT_TIMESTAMP AND u.active = true")
    List<User> findExpiredSubscriptions();
    
    @Query("SELECT u FROM User u WHERE u.referalCode = :code")
    List<User> findAllByReferalCode(@Param("code") Long code);
    
    boolean existsByChatId(Long chatId);
}
