package com.sp26se025.aura.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private String id;
    private String accountId;
    private String packageName;
    private int credits;
    private BigDecimal amount;
    private Instant paidAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Instant getPaidAt() { return paidAt; }
    public void setPaidAt(Instant paidAt) { this.paidAt = paidAt; }
}
