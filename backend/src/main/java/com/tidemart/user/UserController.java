package com.tidemart.user;

import com.tidemart.common.ApiResponse;
import com.tidemart.user.dto.UpdateProfileRequest;
import com.tidemart.user.dto.UserProfileResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserController {
    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @GetMapping
    public ApiResponse<UserProfileResponse> me(@AuthenticationPrincipal Long id) { return ApiResponse.ok(service.me(id)); }

    @PutMapping
    public ApiResponse<UserProfileResponse> update(@AuthenticationPrincipal Long id, @RequestBody UpdateProfileRequest r) { return ApiResponse.ok(service.update(id, r)); }

    @DeleteMapping
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long id) { service.delete(id); return ApiResponse.ok(null); }
}
