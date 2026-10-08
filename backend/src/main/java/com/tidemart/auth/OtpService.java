package com.tidemart.auth;

import com.tidemart.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {
    private final OtpTokenRepository repo;
    private final SecureRandom rnd = new SecureRandom();
    @Value("${tidemart.demo-otp:true}")
    private boolean demo;

    public OtpService(OtpTokenRepository repo) { this.repo = repo; }

    /** Demo mode: prints the OTP in the console and returns it so the app can show it. No SMS provider. */
    public String send(String phone) {
        String code = String.format("%06d", rnd.nextInt(1_000_000));
        OtpToken t = new OtpToken();
        t.phone = phone; t.code = code; t.expiresAt = LocalDateTime.now().plusMinutes(5);
        repo.save(t);
        System.out.println("[Tidemart demo OTP] " + phone + " -> " + code);
        return demo ? code : null;
    }

    public void verify(String phone, String code) {
        OtpToken t = repo.findTopByPhoneAndUsedFalseOrderByIdDesc(phone).orElseThrow(() -> new BadRequestException("Request an OTP first"));
        if (t.expiresAt.isBefore(LocalDateTime.now())) throw new BadRequestException("OTP expired");
        if (!t.code.equals(code)) throw new BadRequestException("Wrong OTP");
        t.used = true;
        repo.save(t);
    }
}
