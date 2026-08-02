package com.facilityflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "assets", indexes = {
        @Index(name = "idx_asset_room", columnList = "room_id"),
        @Index(name = "idx_asset_status", columnList = "status"),
        @Index(name = "idx_asset_tag", columnList = "asset_tag", unique = true)
})
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, of = {})
public class Asset extends BaseEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String assetTag;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private AssetStatus status = AssetStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    private LocalDate warrantyExpiryDate;

    @Column(length = 500)
    private String qrCodeUrl;

    @Column(length = 100)
    private String serialNumber;

    @Column(length = 100)
    private String vendor;

    private Double purchaseCost;
}
