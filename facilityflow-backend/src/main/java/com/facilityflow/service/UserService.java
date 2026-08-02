package com.facilityflow.service;

import com.facilityflow.dto.request.ChangePasswordRequest;
import com.facilityflow.dto.request.UpdateUserRequest;
import com.facilityflow.dto.response.UserResponse;
import com.facilityflow.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    Page<UserResponse> getUsersByRole(Role role, Pageable pageable);
    Page<UserResponse> searchUsers(String term, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUserProfile();
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void changePassword(ChangePasswordRequest request);
    void softDeleteUser(Long id);
}
