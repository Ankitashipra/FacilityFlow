package com.facilityflow.dto.response;

import com.facilityflow.entity.AssetStatus;
import com.facilityflow.entity.AssetType;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetResponse {
    private Long id;
    private String assetTag;
    private String name;
    private AssetType type;
    private AssetStatus status;
    private Long roomId;
    private String roomName;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpiryDate;
    private String qrCodeUrl;
    private String serialNumber;
    private String vendor;
    private Double purchaseCost;
}
