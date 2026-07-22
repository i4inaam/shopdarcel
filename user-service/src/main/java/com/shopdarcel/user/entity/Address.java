package com.shopdarcel.user.entity;

import com.shopdarcel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * A user's shipping address. Kept as a separate entity from {@link User}
 * per Chat 1's locked decision — order-service snapshots this onto the
 * order at purchase time rather than referencing it directly, so order
 * history remains intact even if the address is later edited.
 * <p>
 * One-to-one with {@link User}: each user has at most one shipping
 * address on file.
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
public class Address extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "line1", nullable = false)
    private String line1;

    @Column(name = "line2")
    private String line2;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "country", nullable = false)
    private String country;
}