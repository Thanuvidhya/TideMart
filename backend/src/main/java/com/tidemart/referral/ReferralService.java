package com.tidemart.referral;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.user.User;
import com.tidemart.user.UserRepository;
import com.tidemart.wallet.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReferralService {
    public record View(String code, double earned, List<Referral> referrals) {}
    private static final double REWARD = 50;
    private final ReferralRepository repo;
    private final UserRepository users;
    private final WalletService wallet;

    public ReferralService(ReferralRepository repo, UserRepository users, WalletService wallet) { this.repo = repo; this.users = users; this.wallet = wallet; }

    public View view(Long uid) {
        List<Referral> l = repo.findByReferrerIdOrderByIdDesc(uid);
        return new View(users.findById(uid).orElseThrow().referralCode, l.stream().mapToDouble(r -> r.reward).sum(), l);
    }

    /** Demo rule: both people get Rs 50 in their wallet when a code is applied. */
    @Transactional
    public View apply(Long uid, String code) {
        if (repo.findByReferredId(uid).isPresent()) throw new BadRequestException("You have already used a referral code");
        User ref = users.findByReferralCode(code == null ? "" : code.trim().toUpperCase()).orElseThrow(() -> new BadRequestException("That referral code is not valid"));
        if (ref.id.equals(uid)) throw new BadRequestException("You cannot use your own code");
        Referral r = new Referral();
        r.referrerId = ref.id; r.referredId = uid; r.reward = REWARD; r.status = "REWARDED";
        repo.save(r);
        wallet.credit(ref.id, REWARD, "Referral reward");
        wallet.credit(uid, REWARD, "Welcome reward");
        return view(uid);
    }
}
