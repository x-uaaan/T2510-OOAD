import java.util.Date;

public class AttendanceData {
    private String attendanceId;
    private String userId;
    private String userName;
    private String eventId;
    private String eventName;
    private double paidAmount;
    private Date attendanceTimestamp;
    private String paymentId;
    private String registrationId;
    private String attendanceStatus; // Registered, Attended, No-Show
    private String notes;
    
    // Constructor
    public AttendanceData(String attendanceId, String userId, String userName, 
                         String eventId, String eventName, double paidAmount,
                         Date attendanceTimestamp, String paymentId, String registrationId,
                         String attendanceStatus, String notes) {
        this.attendanceId = attendanceId;
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.paidAmount = paidAmount;
        this.attendanceTimestamp = attendanceTimestamp;
        this.paymentId = paymentId;
        this.registrationId = registrationId;
        this.attendanceStatus = attendanceStatus;
        this.notes = notes;
    }
    
    // Simplified constructor for when payment is completed
    public AttendanceData(String attendanceId, String userId, String userName, 
                         String eventId, String eventName, double paidAmount,
                         String paymentId, String registrationId) {
        this(attendanceId, userId, userName, eventId, eventName, paidAmount,
             new Date(), paymentId, registrationId, "Registered", "");
    }
    
    // Getters
    public String getAttendanceId() { return attendanceId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public double getPaidAmount() { return paidAmount; }
    public Date getAttendanceTimestamp() { return attendanceTimestamp; }
    public String getPaymentId() { return paymentId; }
    public String getRegistrationId() { return registrationId; }
    public String getAttendanceStatus() { return attendanceStatus; }
    public String getNotes() { return notes; }
    
    // Setters
    public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
    public void setAttendanceTimestamp(Date attendanceTimestamp) { this.attendanceTimestamp = attendanceTimestamp; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    
    @Override
    public String toString() {
        return String.format("Attendance[%s]: %s -> %s (RM%.2f, Status: %s)", 
                           attendanceId, userName, eventName, paidAmount, attendanceStatus);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AttendanceData that = (AttendanceData) obj;
        return attendanceId.equals(that.attendanceId);
    }
    
    @Override
    public int hashCode() {
        return attendanceId.hashCode();
    }
} 