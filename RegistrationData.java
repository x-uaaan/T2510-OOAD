// RegistrationData.java
import java.util.Date;

public class RegistrationData {
    private String registrationId;
    private String eventId;
    private String userId;
    private String eventName; // For easier display
    private String userName;  // For easier display
    private Date registrationDate;
    private int pax;
    private boolean catering;
    private boolean transportation;
    private String promoCodeUsed;
    private double baseAmount;
    private double discountAmount;
    private double totalAmount;
    private String status; // Pending, Confirmed, Cancelled
    
    // Constructor
    public RegistrationData(String registrationId, String eventId, String userId, 
                           String eventName, String userName, Date registrationDate,
                           int pax, boolean catering, boolean transportation, 
                           String promoCodeUsed, double baseAmount, double discountAmount, 
                           double totalAmount, String status) {
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.userId = userId;
        this.eventName = eventName;
        this.userName = userName;
        this.registrationDate = registrationDate;
        this.pax = pax;
        this.catering = catering;
        this.transportation = transportation;
        this.promoCodeUsed = promoCodeUsed;
        this.baseAmount = baseAmount;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    // Simplified constructor
    public RegistrationData(String registrationId, String eventId, String userId, 
                           String eventName, String userName, int pax, 
                           boolean catering, boolean transportation, 
                           String promoCodeUsed, double totalAmount) {
        this(registrationId, eventId, userId, eventName, userName, new Date(),
             pax, catering, transportation, promoCodeUsed, totalAmount, 0.0, totalAmount, "Pending");
    }
    
    // Getters
    public String getRegistrationId() { return registrationId; }
    public String getEventId() { return eventId; }
    public String getUserId() { return userId; }
    public String getEventName() { return eventName; }
    public String getUserName() { return userName; }
    public Date getRegistrationDate() { return registrationDate; }
    public int getPax() { return pax; }
    public boolean isCatering() { return catering; }
    public boolean isTransportation() { return transportation; }
    public String getPromoCodeUsed() { return promoCodeUsed; }
    public double getBaseAmount() { return baseAmount; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    
    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setBaseAmount(double baseAmount) { this.baseAmount = baseAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    @Override
    public String toString() {
        return String.format("Registration[%s]: %s -> %s (Pax: %d, Amount: RM%.2f, Status: %s)", 
                           registrationId, userName, eventName, pax, totalAmount, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RegistrationData that = (RegistrationData) obj;
        return registrationId.equals(that.registrationId);
    }
    
    @Override
    public int hashCode() {
        return registrationId.hashCode();
    }
} 