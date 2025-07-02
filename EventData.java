// EventData.java
import java.util.Date;

// EventData class to hold event info
class EventData {
    private String eventId;
    private String name, organiser, eventType, venue;
    private int capacity;
    private Date date;
    private double fee;
    // New fields for discounts
    private boolean earlyBirdEnabled;
    private Date earlyBirdEnd;
    private String earlyBirdDiscountType; // "PERCENTAGE" or "FIXED_AMOUNT"
    private double earlyBirdDiscountValue; // The discount amount or percentage
    private boolean promoEnabled;
    private String promoCode;
    private String promoDiscountType; // "PERCENTAGE" or "FIXED_AMOUNT"
    private double promoDiscount;
    private String status; // Ongoing, Completed, Cancelled
    private String description;
    private double fixedCost;
    private double variableCost;
    
    // Full constructor with eventId
    public EventData(String eventId, String name, String organiser, String eventType, String venue, int capacity, Date date, double fee, String description, double fixedCost, double variableCost, boolean earlyBirdEnabled, Date earlyBirdEnd, String earlyBirdDiscountType, double earlyBirdDiscountValue, boolean promoEnabled, String promoCode, String promoDiscountType, double promoDiscount, String status) {
        this.eventId = eventId;
        this.name = name;
        this.organiser = organiser;
        this.eventType = eventType;
        this.venue = venue;
        this.capacity = capacity;
        this.date = date;
        this.fee = fee;
        this.description = description == null ? "" : description;
        this.fixedCost = fixedCost;
        this.variableCost = variableCost;
        this.earlyBirdEnabled = earlyBirdEnabled;
        this.earlyBirdEnd = earlyBirdEnd;
        this.earlyBirdDiscountType = earlyBirdDiscountType;
        this.earlyBirdDiscountValue = earlyBirdDiscountValue;
        this.promoEnabled = promoEnabled;
        this.promoCode = promoCode;
        this.promoDiscountType = promoDiscountType;
        this.promoDiscount = promoDiscount;
        this.status = status == null ? "Active" : status;
    }
    
    // Constructor without eventId (auto-generate)
    public EventData(String name, String organiser, String eventType, String venue, int capacity, Date date, double fee, String description, double fixedCost, double variableCost, boolean earlyBirdEnabled, Date earlyBirdEnd, String earlyBirdDiscountType, double earlyBirdDiscountValue, boolean promoEnabled, String promoCode, String promoDiscountType, double promoDiscount, String status) {
        this(EventCSVManager.generateNextEventId(), name, organiser, eventType, venue, capacity, date, fee, description, fixedCost, variableCost, earlyBirdEnabled, earlyBirdEnd, earlyBirdDiscountType, earlyBirdDiscountValue, promoEnabled, promoCode, promoDiscountType, promoDiscount, status);
    }
    // Backward compatible constructor
    public EventData(String name, String organiser, String eventType, String venue, int capacity, Date date, double fee) {
        this(name, organiser, eventType, venue, capacity, date, fee, "", 0, 0, false, null, null, 0.0, false, null, null, 0, "Active");
    }
    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getOrganiser() { return organiser; }
    public String getEventType() { return eventType; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public Date getDate() { return date; }
    public double getFee() { return fee; }
    // New getters
    public boolean isEarlyBirdEnabled() { return earlyBirdEnabled; }
    public Date getEarlyBirdEnd() { return earlyBirdEnd; }
    public String getEarlyBirdDiscountType() { return earlyBirdDiscountType; }
    public double getEarlyBirdDiscountValue() { return earlyBirdDiscountValue; }
    public boolean isPromoEnabled() { return promoEnabled; }
    public String getPromoCode() { return promoCode; }
    public String getPromoDiscountType() { return promoDiscountType; }
    public double getPromoDiscount() { return promoDiscount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getFixedCost() { return fixedCost; }
    public void setFixedCost(double fixedCost) { this.fixedCost = fixedCost; }
    public double getVariableCost() { return variableCost; }
    public void setVariableCost(double variableCost) { this.variableCost = variableCost; }
    
    // Helper method to calculate promo discount amount
    public double calculatePromoDiscount(double amount) {
        if (!promoEnabled || promoCode == null) {
            return 0.0;
        }
        
        if ("PERCENTAGE".equals(promoDiscountType)) {
            return amount * (promoDiscount / 100.0);
        } else if ("FIXED_AMOUNT".equals(promoDiscountType)) {
            return promoDiscount;
        }
        
        return 0.0;
    }
    
    // Helper method to get promo discount display text
    public String getPromoDiscountDisplay() {
        if ("PERCENTAGE".equals(promoDiscountType)) {
            return String.format("%.0f%% OFF", promoDiscount);
        } else if ("FIXED_AMOUNT".equals(promoDiscountType)) {
            return String.format("RM%.2f OFF", promoDiscount);
        }
        return "No discount";
    }
    
    // Helper method to calculate early bird discount amount
    public double calculateEarlyBirdDiscount(double amount) {
        if (!earlyBirdEnabled || earlyBirdEnd == null || System.currentTimeMillis() > earlyBirdEnd.getTime()) {
            return 0.0;
        }
        
        if ("PERCENTAGE".equals(earlyBirdDiscountType)) {
            return amount * (earlyBirdDiscountValue / 100.0);
        } else if ("FIXED_AMOUNT".equals(earlyBirdDiscountType)) {
            return earlyBirdDiscountValue;
        }
        
        return 0.0;
    }
    
    // Helper method to get early bird discount display text
    public String getEarlyBirdDiscountDisplay() {
        if ("PERCENTAGE".equals(earlyBirdDiscountType)) {
            return String.format("%.0f%% OFF", earlyBirdDiscountValue);
        } else if ("FIXED_AMOUNT".equals(earlyBirdDiscountType)) {
            return String.format("RM%.2f OFF", earlyBirdDiscountValue);
        }
        return "No discount";
    }
}