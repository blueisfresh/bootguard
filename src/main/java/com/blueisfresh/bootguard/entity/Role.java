package com.blueisfresh.bootguard.entity;

import com.blueisfresh.bootguard.user.RoleName;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a user role.
 * <p>
 * Maps to the {@link com.blueisfresh.bootguard.user.RoleName} enum
 * and is associated with users via a many-to-many relationship.
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_role")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "role_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private RoleName name;

    @JsonBackReference
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude // Prevent ConcurrentModificationException
    @ToString.Exclude // Prevent ConcurrentModificationException
    private List<User> users = new ArrayList<>();
}
