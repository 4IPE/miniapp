package ru.rubytunnel.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @Column(name = "wg_id")
    private String wgId;

    @Column(name = "active")
    private Boolean active = false;

    @Column(name = "name_tg", nullable = false)
    private String nameTg;

    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;

    @Column(name = "referal_code")
    private Long referalCode;

    @Column(name = "count_referal_users")
    private Long countReferalUsers = 0L;

    @Column(name = "demo")
    private Boolean demo = false;

    @Column(name = "price")
    private Integer price = 100;

    @Column(name = "inform_status")
    private Boolean informStatus = false;

    @Column(name="pay_label")
    private String payLabel;

}
