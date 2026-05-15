package com.sp26se025.aura.model;

import java.time.Instant;

public class Clinic {
    private String id;
    private String name;
    private String taxCode;
    private String address;
    private boolean verified;
    private int packageCredits = 100;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public int getPackageCredits() { return packageCredits; }
    public void setPackageCredits(int packageCredits) { this.packageCredits = packageCredits; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
