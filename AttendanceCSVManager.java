import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AttendanceCSVManager {
    private static final String CSV_FILE = "csv/attendance.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "attendanceId,userId,userName,eventId,eventName,paidAmount,attendanceTimestamp,paymentId,registrationId,attendanceStatus,notes";
    
    /**
     * Save all attendance records to CSV
     */
    public static void saveAttendanceToCSV(List<AttendanceData> attendanceList) {
        try {
            // Create directory if it doesn't exist
            File csvFile = new File(CSV_FILE);
            csvFile.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
                // Write header
                writer.println(CSV_HEADER);
                
                // Write data
                for (AttendanceData attendance : attendanceList) {
                    writer.println(attendanceToCSVLine(attendance));
                }
            }
            
            System.out.println("Attendance saved successfully to " + CSV_FILE + " (" + attendanceList.size() + " records)");
            
        } catch (IOException e) {
            System.err.println("Error saving attendance to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load all attendance records from CSV
     */
    public static List<AttendanceData> loadAttendanceFromCSV() {
        List<AttendanceData> attendanceList = new ArrayList<>();
        File csvFile = new File(CSV_FILE);
        
        if (!csvFile.exists()) {
            System.out.println("Attendance CSV file does not exist yet: " + CSV_FILE);
            return attendanceList;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        AttendanceData attendance = csvLineToAttendance(line);
                        attendanceList.add(attendance);
                    } catch (Exception e) {
                        System.err.println("Error parsing attendance line: " + line + " - " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + attendanceList.size() + " attendance records from " + CSV_FILE);
            
        } catch (IOException e) {
            System.err.println("Error loading attendance from CSV: " + e.getMessage());
        }
        
        return attendanceList;
    }
    
    /**
     * Add new attendance record to CSV
     */
    public static void addAttendanceToCSV(AttendanceData attendance) {
        List<AttendanceData> attendanceList = loadAttendanceFromCSV();
        attendanceList.add(attendance);
        saveAttendanceToCSV(attendanceList);
        System.out.println("Added attendance record: " + attendance);
    }
    
    /**
     * Update attendance record in CSV
     */
    public static void updateAttendanceInCSV(AttendanceData oldAttendance, AttendanceData newAttendance) {
        List<AttendanceData> attendanceList = loadAttendanceFromCSV();
        
        for (int i = 0; i < attendanceList.size(); i++) {
            if (attendanceList.get(i).equals(oldAttendance)) {
                attendanceList.set(i, newAttendance);
                saveAttendanceToCSV(attendanceList);
                System.out.println("Updated attendance record: " + newAttendance);
                return;
            }
        }
        
        System.err.println("Attendance record not found for update: " + oldAttendance);
    }
    
    /**
     * Delete attendance record from CSV
     */
    public static void deleteAttendanceFromCSV(AttendanceData attendanceToDelete) {
        List<AttendanceData> attendanceList = loadAttendanceFromCSV();
        
        if (attendanceList.remove(attendanceToDelete)) {
            saveAttendanceToCSV(attendanceList);
            System.out.println("Deleted attendance record: " + attendanceToDelete);
        } else {
            System.err.println("Attendance record not found for deletion: " + attendanceToDelete);
        }
    }
    
    /**
     * Get attendance records for a specific user
     */
    public static List<AttendanceData> getAttendanceByUserId(String userId) {
        List<AttendanceData> allAttendance = loadAttendanceFromCSV();
        List<AttendanceData> userAttendance = new ArrayList<>();
        
        for (AttendanceData attendance : allAttendance) {
            if (attendance.getUserId().equals(userId)) {
                userAttendance.add(attendance);
            }
        }
        
        return userAttendance;
    }
    
    /**
     * Get attendance records for a specific event
     */
    public static List<AttendanceData> getAttendanceByEventId(String eventId) {
        List<AttendanceData> allAttendance = loadAttendanceFromCSV();
        List<AttendanceData> eventAttendance = new ArrayList<>();
        
        for (AttendanceData attendance : allAttendance) {
            if (attendance.getEventId().equals(eventId)) {
                eventAttendance.add(attendance);
            }
        }
        
        return eventAttendance;
    }
    
    /**
     * Get attendance by payment ID
     */
    public static AttendanceData getAttendanceByPaymentId(String paymentId) {
        List<AttendanceData> allAttendance = loadAttendanceFromCSV();
        
        for (AttendanceData attendance : allAttendance) {
            if (attendance.getPaymentId().equals(paymentId)) {
                return attendance;
            }
        }
        
        return null;
    }
    
    /**
     * Mark user as attended for an event
     */
    public static void markUserAttended(String attendanceId) {
        List<AttendanceData> attendanceList = loadAttendanceFromCSV();
        
        for (int i = 0; i < attendanceList.size(); i++) {
            AttendanceData attendance = attendanceList.get(i);
            if (attendance.getAttendanceId().equals(attendanceId)) {
                attendance.setAttendanceStatus("Attended");
                attendance.setAttendanceTimestamp(new Date());
                attendanceList.set(i, attendance);
                saveAttendanceToCSV(attendanceList);
                System.out.println("Marked as attended: " + attendance);
                return;
            }
        }
        
        System.err.println("Attendance record not found: " + attendanceId);
    }
    
    /**
     * Generate next attendance ID
     */
    public static String generateNextAttendanceId() {
        List<AttendanceData> attendance = loadAttendanceFromCSV();
        int maxId = 0;
        
        for (AttendanceData record : attendance) {
            String attendanceId = record.getAttendanceId();
            if (attendanceId.startsWith("A")) {
                try {
                    int id = Integer.parseInt(attendanceId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("A%04d", maxId + 1);
    }
    
    /**
     * Get event attendance statistics
     */
    public static Map<String, Integer> getEventAttendanceStats(String eventId) {
        List<AttendanceData> eventAttendance = getAttendanceByEventId(eventId);
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("Registered", 0);
        stats.put("Attended", 0);
        stats.put("No-Show", 0);
        
        for (AttendanceData attendance : eventAttendance) {
            String status = attendance.getAttendanceStatus();
            stats.put(status, stats.get(status) + 1);
        }
        
        return stats;
    }
    
    /**
     * Convert AttendanceData to CSV line
     */
    private static String attendanceToCSVLine(AttendanceData attendance) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeCSV(attendance.getAttendanceId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getUserId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getUserName())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getEventId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getEventName())).append(CSV_SEPARATOR);
        sb.append(attendance.getPaidAmount()).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(attendance.getAttendanceTimestamp())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getPaymentId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getRegistrationId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getAttendanceStatus())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(attendance.getNotes() != null ? attendance.getNotes() : ""));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to AttendanceData
     */
    private static AttendanceData csvLineToAttendance(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 11) {
            throw new IllegalArgumentException("Invalid attendance CSV line format - expected 11 fields, got " + fields.length);
        }
        
        try {
            String attendanceId = fields[0];
            String userId = fields[1];
            String userName = fields[2];
            String eventId = fields[3];
            String eventName = fields[4];
            double paidAmount = Double.parseDouble(fields[5]);
            Date attendanceTimestamp = DATE_FORMAT.parse(fields[6]);
            String paymentId = fields[7];
            String registrationId = fields[8];
            String attendanceStatus = fields[9];
            String notes = fields[10].trim().isEmpty() ? null : fields[10];
            
            return new AttendanceData(attendanceId, userId, userName, eventId, eventName,
                                    paidAmount, attendanceTimestamp, paymentId, registrationId,
                                    attendanceStatus, notes);
            
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing attendance CSV fields: " + e.getMessage());
        }
    }
    
    /**
     * Escape CSV special characters
     */
    private static String escapeCSV(String value) {
        if (value == null) return "";
        
        // If value contains comma, quote, or newline, wrap in quotes and escape quotes
        if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * Parse CSV line considering quoted fields
     */
    private static String[] parseCSVLine(String csvLine) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < csvLine.length(); i++) {
            char c = csvLine.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < csvLine.length() && csvLine.charAt(i + 1) == '"') {
                    // Escaped quote
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add last field
        fields.add(current.toString());
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Get CSV file info
     */
    public static String getCSVInfo() {
        File file = new File(CSV_FILE);
        if (file.exists()) {
            return "Attendance CSV: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "Attendance CSV file does not exist yet";
        }
    }
} 