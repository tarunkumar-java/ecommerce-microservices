package com.ecommerce.user_service.entity;

import com.ecommerce.user_service.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "emailId"),
                @UniqueConstraint(columnNames = "phoneNo")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(name = "emailId", nullable = false)
    private String emailId;

    @Column(name = "phoneNo")
    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}

