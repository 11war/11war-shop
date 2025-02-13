package com.war11.domain.user.controller;

import com.war11.domain.user.dto.request.UserRequest;
import com.war11.domain.user.dto.response.UserResponse;
import com.war11.domain.user.service.UserService;
import com.war11.global.common.ApiResponse;
import com.war11.global.config.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "관리자 쿠폰 API", description = "관리자 쿠폰 API 목록임!")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable Long id) {
        UserResponse userResponse = userService.findById(id);
        return ApiResponse.success(userResponse);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<UserResponse>>> findAllUsers(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> pageUser = userService.findAllUsers(userDetails, page, size);
        return ApiResponse.success(pageUser);
    }

    @PatchMapping()
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@AuthenticationPrincipal CustomUserDetails userDetails , @RequestBody
        UserRequest request) {
        UserResponse updateResponse = userService.updateUsers(userDetails, request);
        return ApiResponse.success(updateResponse);
    }

}
