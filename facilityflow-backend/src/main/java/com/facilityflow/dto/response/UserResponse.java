package com.facilityflow.dto.response;

import com.facilityflow.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String designation;
    private String department;
    private Role role;
    private boolean enabled;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
