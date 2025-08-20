package com.blueisfresh.bootguard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
@EntityListeners(AuditingEntityListener.class) // Enables Spring Data JPA Auditing for @CreatedDate/@LastModifiedDate

public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    @Column(name = "username", nullable = false, length = 64, unique = true)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format") // Email well Formated
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank(message = "Display name cannot be blank")
    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    @Column(name = "display_name", nullable = false, length = 100, unique = true)
    private String displayName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    // TODO: Recommended Profile Picture Url - if null default profile picture

    @CreatedDate // Spring Data JPA Auditing: Automatically sets creation timestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate // Spring Data JPA Auditing: Automatically updates timestamp on entity modification
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;// Update the timestamp on every update

    // Relation to RefreshToken
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    // Relation to Roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
