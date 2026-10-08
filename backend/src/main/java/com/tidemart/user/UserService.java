package com.tidemart.user;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.user.dto.UpdateProfileRequest;
import com.tidemart.user.dto.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository users;

    public UserService(UserRepository users) { this.users = users; }

    public UserProfileResponse me(Long id) { return toDto(find(id)); }

    public UserProfileResponse update(Long id, UpdateProfileRequest r) {
        User u = find(id);
        if (r.name() != null) u.name = r.name();
        if (r.email() != null && !r.email().isBlank()) {
            users.findByEmail(r.email()).filter(x -> !x.id.equals(id)).ifPresent(x -> { throw new BadRequestException("Email already in use"); });
            u.email = r.email();
        }
        if (r.photoUrl() != null) u.photoUrl = r.photoUrl();
        if (r.language() != null) u.language = r.language();
        return toDto(users.save(u));
    }

    public void delete(Long id) { User u = find(id); u.status = "DELETED"; users.save(u); }

    private User find(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")); }

    private UserProfileResponse toDto(User u) {
        return new UserProfileResponse(u.id, u.name, u.phone, u.email, u.photoUrl, u.language, u.role.name(), u.referralCode);
    }
}
