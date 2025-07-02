// PaymentData.java
import java.util.Date;

public class PaymentData {
    private String paymentId;
    private String registrationId;
    private String userId;
    private String eventId;
    private String userName;  // For easier display
    private String eventName; // For easier display
    private double paymentAmount;
    private String paymentMethod; // Cash, Credit Card, Bank Transfer, Online Banking
    private Date paymentDate;
    private String paymentStatus; // Pending, Completed, Failed, Refunded
    private String transactionId;
    private String paymentReference;
    private String notes;
    
    // Constructor
    public PaymentData(String paymentId, String registrationId, String userId, String eventId,
                      String userName, String eventName, double paymentAmount, 
                      String paymentMethod, Date paymentDate, String paymentStatus,
                      String transactionId, String paymentReference, String notes) {
        this.paymentId = paymentId;
        this.registrationId = registrationId;
        this.userId = userId;
        this.eventId = eventId;
        this.userName = userName;
        this.eventName = eventName;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.paymentReference = paymentReference;
        this.notes = notes;
    }
    
    // Simplified constructor
    public PaymentData(String paymentId, String registrationId, String userId, String eventId,
                      String userName, String eventName, double paymentAmount, 
                      String paymentMethod, String paymentStatus) {
        this(paymentId, registrationId, userId, eventId, userName, eventName, 
             paymentAmount, paymentMethod, new Date(), paymentStatus, "", "", "");
    }
    
    // Getters
    public String getPaymentId() { return paymentId; }
    public String getRegistrationId() { return registrationId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getUserName() { return userName; }
    public String getEventName() { return eventName; }
    public double getPaymentAmount() { return paymentAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public Date getPaymentDate() { return paymentDate; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getTransactionId() { return transactionId; }
    public String getPaymentReference() { return paymentReference; }
    public String getNotes() { return notes; }
    
    // Setters
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    @Override
    public String toString() {
        return String.format("Payment[%s]: %s - %s (RM%.2f via %s, Status: %s)", 
                           paymentId, userName, eventName, paymentAmount, paymentMethod, paymentStatus);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PaymentData that = (PaymentData) obj;
        return paymentId.equals(that.paymentId);
    }
    
    @Override
    public int hashCode() {
        return paymentId.hashCode();
    }
} 