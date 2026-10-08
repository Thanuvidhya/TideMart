package com.tidemart.wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {
    public record View(double balance, List<WalletTransaction> transactions) {}
    private final WalletRepository wallets;
    private final WalletTransactionRepository txs;

    public WalletService(WalletRepository wallets, WalletTransactionRepository txs) { this.wallets = wallets; this.txs = txs; }

    public Wallet of(Long uid) { return wallets.findByUserId(uid).orElseGet(() -> { Wallet w = new Wallet(); w.userId = uid; return wallets.save(w); }); }

    @Transactional
    public void credit(Long uid, double amount, String reason) {
        Wallet w = of(uid);
        w.balance += amount; wallets.save(w);
        WalletTransaction t = new WalletTransaction();
        t.walletId = w.id; t.type = "CREDIT"; t.amount = amount; t.reason = reason;
        txs.save(t);
    }

    @Transactional
    public void debit(Long uid, double amount, String reason) {
        Wallet w = of(uid);
        if (w.balance < amount) throw new com.tidemart.common.exception.BadRequestException("Your wallet balance is too low");
        w.balance -= amount; wallets.save(w);
        WalletTransaction t = new WalletTransaction();
        t.walletId = w.id; t.type = "DEBIT"; t.amount = amount; t.reason = reason;
        txs.save(t);
    }

    public View view(Long uid) { Wallet w = of(uid); return new View(w.balance, txs.findByWalletIdOrderByIdDesc(w.id)); }
}
