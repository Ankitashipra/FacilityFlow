package com.facilityflow.dto.request;

import com.facilityflow.entity.Role;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateUserRequest {
    @Size(min = 2, max = 100)
    private String fullName;
    private String phoneNumber;
    private String designation;
    private String department;
    private Role role;
    private Boolean enabled;
}
