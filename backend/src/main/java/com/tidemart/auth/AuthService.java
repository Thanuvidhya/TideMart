package com.tidemart.auth;

import com.tidemart.auth.dto.*;
import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.UnauthorizedException;
import com.tidemart.security.JwtUtil;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository users;
    private final OtpService otp;
    private final JwtUtil jwt;
    private final PasswordEncoder enc;
    private final com.tidemart.seller.SellerRepository sellers;
    private final com.tidemart.reseller.ResellerRepository resellers;
    private final com.tidemart.delivery.DeliveryPartnerRepository deliveryPartners;

    public AuthService(UserRepository users, OtpService otp, JwtUtil jwt, PasswordEncoder enc, com.tidemart.seller.SellerRepository sellers, com.tidemart.reseller.ResellerRepository resellers, com.tidemart.delivery.DeliveryPartnerRepository deliveryPartners) {
        this.users = users; this.otp = otp; this.jwt = jwt; this.enc = enc; this.sellers = sellers; this.resellers = resellers; this.deliveryPartners = deliveryPartners;
    }

    public String sendOtp(String phone) { return otp.send(phone); }

    public AuthResponse verifyOtp(String phone, String code) {
        otp.verify(phone, code);
        User u = users.findByPhone(phone).orElseGet(() -> users.save(newUser(phone, null, null)));
        return respond(u);
    }

    public AuthResponse register(RegisterRequest r) {
        if (users.findByEmail(r.email()).isPresent()) throw new BadRequestException("Email already registered");
        User u = newUser(null, r.email(), r.name());
        u.passwordHash = enc.encode(r.password());
        return respond(users.save(u));
    }

    public AuthResponse login(EmailLoginRequest r) {
        User u = authenticate(r);
        if (u.role == com.tidemart.common.Role.ADMIN || u.role == com.tidemart.common.Role.DELIVERY) {
            throw new UnauthorizedException("Use the Admin or Delivery login page for this account");
        }
        return respond(u);
    }

    public AuthResponse adminLogin(EmailLoginRequest r) {
        User u = authenticate(r);
        if (u.role != com.tidemart.common.Role.ADMIN) {
            throw new UnauthorizedException("This account is not an Admin account");
        }
        return respond(u);
    }

    public AuthResponse deliveryLogin(EmailLoginRequest r) {
        User u = authenticate(r);
        if (u.role != com.tidemart.common.Role.DELIVERY) {
            throw new UnauthorizedException("This account is not a Delivery Partner account");
        }
        if (u.phone == null || deliveryPartners.findByPhone(u.phone).isEmpty()) {
            throw new UnauthorizedException("Delivery partner profile is not active");
        }
        return respond(u);
    }

    private User authenticate(EmailLoginRequest r) {
        User u = users.findByEmail(r.email()).orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (u.passwordHash == null || !enc.matches(r.password(), u.passwordHash)) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return u;
    }

    private User newUser(String phone, String email, String name) {
        User u = new User();
        u.phone = phone; u.email = email; u.name = name;
        u.referralCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return u;
    }

    private AuthResponse respond(User u) {
        if (!"ACTIVE".equals(u.status)) throw new UnauthorizedException("Account is not active");
        var roles = new java.util.ArrayList<String>(); roles.add(u.role.name()); if (sellers.findByUserId(u.id).isPresent()) roles.add("SELLER"); if (resellers.findByUserId(u.id).isPresent()) roles.add("RESELLER"); if (u.phone != null && deliveryPartners.findByPhone(u.phone).isPresent()) roles.add("DELIVERY"); if (!roles.contains("CUSTOMER") && !roles.contains("ADMIN") && !roles.contains("DELIVERY")) roles.add("CUSTOMER"); return new AuthResponse(jwt.create(u.id, roles), u.id, u.role.name(), u.name, roles);
    }
}
