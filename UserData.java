// UserData.java
import java.util.Date;

public class UserData {
    private String userId;
    private String userName;
    private String studentId;
    private String faculty;
    private String email;
    private String userType; // Student, Staff, External
    private String phoneNumber;
    private Date registeredDate;
    
    // Full Constructor
    public UserData(String userId, String userName, String studentId, String faculty, String email, String userType, String phoneNumber, Date registeredDate) {
        this.userId = userId;
        this.userName = userName;
        this.studentId = studentId;
        this.faculty = faculty;
        this.email = email;
        this.userType = userType;
        this.phoneNumber = phoneNumber;
        this.registeredDate = registeredDate;
    }
    
    // Simplified constructor
    public UserData(String userId, String userName, String studentId, String faculty, String email, String userType, String phoneNumber) {
        this(userId, userName, studentId, faculty, email, userType, phoneNumber, new Date());
    }
    
    // Backward compatible constructor (without studentId and faculty)
    public UserData(String userId, String userName, String email, String userType, String phoneNumber, Date registeredDate) {
        this(userId, userName, null, null, email, userType, phoneNumber, registeredDate);
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getStudentId() { return studentId; }
    public String getFaculty() { return faculty; }
    public String getEmail() { return email; }
    public String getUserType() { return userType; }
    public String getPhoneNumber() { return phoneNumber; }
    public Date getRegisteredDate() { return registeredDate; }
    
    // Setters
    public void setUserName(String userName) { this.userName = userName; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setFaculty(String faculty) { this.faculty = faculty; }
    public void setEmail(String email) { this.email = email; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    @Override
    public String toString() {
        String studentInfo = studentId != null ? " [" + studentId + "]" : "";
        String facultyInfo = faculty != null ? " - " + faculty : "";
        return String.format("User[%s]: %s%s (%s)%s - %s", userId, userName, studentInfo, email, facultyInfo, userType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserData userData = (UserData) obj;
        return userId.equals(userData.userId);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode();
    }
} 