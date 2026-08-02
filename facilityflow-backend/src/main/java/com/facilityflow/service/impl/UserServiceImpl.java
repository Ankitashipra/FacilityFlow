package com.facilityflow.service.impl;

import com.facilityflow.audit.AuditService;
import com.facilityflow.dto.request.ChangePasswordRequest;
import com.facilityflow.dto.request.UpdateUserRequest;
import com.facilityflow.dto.response.UserResponse;
import com.facilityflow.entity.AuditAction;
import com.facilityflow.entity.Role;
import com.facilityflow.entity.User;
import com.facilityflow.exception.InvalidCredentialsException;
import com.facilityflow.exception.ResourceNotFoundException;
import com.facilityflow.mapper.UserMapper;
import com.facilityflow.repository.UserRepository;
import com.facilityflow.security.SecurityUtils;
import com.facilityflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String term, Pageable pageable) {
        return userRepository.search(term, pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUserOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userMapper.toResponse(findUserOrThrow(userId));
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDesignation() != null) user.setDesignation(request.getDesignation());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
            auditService.record(AuditAction.ROLE_CHANGE, "User", user.getId(),
                    "Role changed to " + request.getRole());
        }
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        user = userRepository.save(user);
        auditService.record(AuditAction.UPDATE, "User", user.getId(), "User profile updated");

        return userMapper.toResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = findUserOrThrow(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        auditService.record(AuditAction.PASSWORD_CHANGE, "User", user.getId(), "Password changed");
    }

    @Override
    public void softDeleteUser(Long id) {
        User user = findUserOrThrow(id);
        user.markDeleted();
        user.setEnabled(false);
        userRepository.save(user);
        auditService.record(AuditAction.DELETE, "User", user.getId(), "User soft-deleted");
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }
}
