package com.facilityflow.dto.request;

import com.facilityflow.entity.AssetStatus;
import com.facilityflow.entity.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssetRequest {
    @NotBlank private String assetTag;
    @NotBlank private String name;
    @NotNull private AssetType type;
    private AssetStatus status;
    private Long roomId;
    @NotNull private LocalDate purchaseDate;
    private LocalDate warrantyExpiryDate;
    private String serialNumber;
    private String vendor;
    private Double purchaseCost;
}
