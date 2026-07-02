package com.shopdarcel.common.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

/**
 * Mapped superclass extended by every JPA entity in every ShopDarcel
 * service. Centralizes the surrogate primary key and created/updated
 * auditing timestamps so no service reimplements this boilerplate.
 *
 * <p>Uses {@link AuditingEntityListener}, so each service must enable JPA
 * auditing once via {@code @EnableJpaAuditing} on a {@code @Configuration}
 * class (typically in that service's {@code config/} package).
 *
 * <p>{@code @MappedSuperclass} means this class is NOT itself an entity and
 * has no table of its own — its fields are inlined into each subclass's
 * table via column inheritance.
 */
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}