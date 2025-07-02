import java.util.Date;

public class VoucherData {
    private String voucherId;
    private String voucherCode;
    private String voucherType; // PROMO_CODE, EARLY_BIRD, GROUP, MEMBER
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private double discountValue; // Percentage (0-100) or Fixed Amount
    private String description;
    private Date validFrom;
    private Date validUntil;
    private int usageLimit; // -1 for unlimited
    private int usedCount;
    private double minimumAmount; // Minimum purchase amount to qualify
    private int minimumPax; // Minimum participants for group discounts
    private String applicableEvents; // "ALL" or comma-separated event IDs
    private String userTypeEligible; // "ALL", "STUDENT", "STAFF", "EXTERNAL"
    private boolean isActive;
    private String createdBy;
    private Date createdDate;
    
    // Constructor
    public VoucherData(String voucherId, String voucherCode, String voucherType, 
                      String discountType, double discountValue, String description,
                      Date validFrom, Date validUntil, int usageLimit, int usedCount,
                      double minimumAmount, int minimumPax, String applicableEvents,
                      String userTypeEligible, boolean isActive, String createdBy, Date createdDate) {
        this.voucherId = voucherId;
        this.voucherCode = voucherCode;
        this.voucherType = voucherType;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.description = description;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.minimumAmount = minimumAmount;
        this.minimumPax = minimumPax;
        this.applicableEvents = applicableEvents;
        this.userTypeEligible = userTypeEligible;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }
    
    // Simplified constructor for creating new vouchers
    public VoucherData(String voucherId, String voucherCode, String voucherType, 
                      String discountType, double discountValue, String description,
                      Date validFrom, Date validUntil, String createdBy) {
        this(voucherId, voucherCode, voucherType, discountType, discountValue, description,
             validFrom, validUntil, -1, 0, 0.0, 1, "ALL", "ALL", true, createdBy, new Date());
    }
    
    // Getters
    public String getVoucherId() { return voucherId; }
    public String getVoucherCode() { return voucherCode; }
    public String getVoucherType() { return voucherType; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public String getDescription() { return description; }
    public Date getValidFrom() { return validFrom; }
    public Date getValidUntil() { return validUntil; }
    public int getUsageLimit() { return usageLimit; }
    public int getUsedCount() { return usedCount; }
    public double getMinimumAmount() { return minimumAmount; }
    public int getMinimumPax() { return minimumPax; }
    public String getApplicableEvents() { return applicableEvents; }
    public String getUserTypeEligible() { return userTypeEligible; }
    public boolean isActive() { return isActive; }
    public String getCreatedBy() { return createdBy; }
    public Date getCreatedDate() { return createdDate; }
    
    // Setters
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public void setValidUntil(Date validUntil) { this.validUntil = validUntil; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = usageLimit; }
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Check if voucher is currently valid
     */
    public boolean isCurrentlyValid() {
        if (!isActive) return false;
        
        Date now = new Date();
        if (validFrom != null && now.before(validFrom)) return false;
        if (validUntil != null && now.after(validUntil)) return false;
        
        // Check usage limit
        if (usageLimit > 0 && usedCount >= usageLimit) return false;
        
        return true;
    }
    
    /**
     * Check if voucher is applicable for given amount and pax
     */
    public boolean isApplicableFor(double amount, int pax, String eventId, String userType) {
        if (!isCurrentlyValid()) return false;
        
        // Check minimum amount
        if (amount < minimumAmount) return false;
        
        // Check minimum pax for group discounts
        if (("GROUP".equals(voucherType) || "GROUP_ORDER".equals(voucherType)) && pax < minimumPax) return false;
        
        // Check event eligibility
        if (!"ALL".equals(applicableEvents)) {
            String[] eventIds = applicableEvents.split(",");
            boolean eventFound = false;
            for (String id : eventIds) {
                if (id.trim().equals(eventId)) {
                    eventFound = true;
                    break;
                }
            }
            if (!eventFound) return false;
        }
        
        // Check user type eligibility
        if (!"ALL".equals(userTypeEligible) && !userTypeEligible.equals(userType)) return false;
        
        return true;
    }
    
    /**
     * Calculate discount amount based on original amount
     */
    public double calculateDiscountAmount(double originalAmount) {
        if (!isCurrentlyValid()) return 0.0;
        
        if ("PERCENTAGE".equals(discountType)) {
            return (originalAmount * discountValue) / 100.0;
        } else if ("FIXED_AMOUNT".equals(discountType)) {
            return discountValue; // Allow discount to exceed original amount
        }
        
        return 0.0;
    }
    
    /**
     * Increment usage count
     */
    public void incrementUsage() {
        this.usedCount++;
    }
    
    /**
     * Get formatted discount display
     */
    public String getDiscountDisplay() {
        if ("PERCENTAGE".equals(discountType)) {
            return String.format("%.0f%% OFF", discountValue);
        } else if ("FIXED_AMOUNT".equals(discountType)) {
            return String.format("RM%.2f OFF", discountValue);
        }
        return "DISCOUNT";
    }
    
    /**
     * Get voucher status for display
     */
    public String getStatusDisplay() {
        if (!isActive) return "INACTIVE";
        
        Date now = new Date();
        if (validFrom != null && now.before(validFrom)) return "NOT YET ACTIVE";
        if (validUntil != null && now.after(validUntil)) return "EXPIRED";
        if (usageLimit > 0 && usedCount >= usageLimit) return "USAGE LIMIT REACHED";
        
        return "ACTIVE";
    }
    
    @Override
    public String toString() {
        return String.format("Voucher[%s]: %s - %s (%s, Status: %s)", 
                           voucherId, voucherCode, getDiscountDisplay(), voucherType, getStatusDisplay());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VoucherData that = (VoucherData) obj;
        return voucherId.equals(that.voucherId);
    }
    
    @Override
    public int hashCode() {
        return voucherId.hashCode();
    }
} 