// UserCSVManager.java
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserCSVManager {
    private static final String CSV_FILE = "csv/users.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "userId,userName,studentId,faculty,email,userType,phoneNumber,registeredDate";
    
    /**
     * Save users to CSV file
     */
    public static void saveUsersToCSV(List<UserData> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println(CSV_HEADER);
            
            // Write user data
            for (UserData user : users) {
                writer.println(userToCSVLine(user));
            }
            
            System.out.println("Users saved successfully to " + CSV_FILE + " (" + users.size() + " users)");
        } catch (IOException e) {
            System.err.println("Error saving users to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load users from CSV file
     */
    public static List<UserData> loadUsersFromCSV() {
        List<UserData> users = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            System.out.println("Users CSV file not found. Creating new empty file: " + CSV_FILE);
            // Create empty CSV file with header
            saveUsersToCSV(users);
            return users;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        UserData user = csvLineToUser(line);
                        if (user != null) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing user CSV line: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + users.size() + " users from " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Error loading users from CSV: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Add a single user to CSV
     */
    public static void addUserToCSV(UserData user) {
        List<UserData> users = loadUsersFromCSV();
        users.add(user);
        saveUsersToCSV(users);
        System.out.println("Added user to CSV: " + user.getUserName());
        
        // Automatically create platform vouchers for new user
        try {
            VoucherCSVManager.createNewUserVouchers(user.getUserId());
            VoucherCSVManager.createGroupOrderVouchers(user.getUserId());
            System.out.println("Created platform vouchers for new user: " + user.getUserName());
        } catch (Exception e) {
            System.err.println("Error creating platform vouchers for user " + user.getUserName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Update a user in CSV
     */
    public static void updateUserInCSV(UserData oldUser, UserData newUser) {
        List<UserData> users = loadUsersFromCSV();
        
        // Find and replace the old user
        for (int i = 0; i < users.size(); i++) {
            UserData existing = users.get(i);
            if (existing.getUserId().equals(oldUser.getUserId())) {
                users.set(i, newUser);
                saveUsersToCSV(users);
                System.out.println("Updated user in CSV: " + newUser.getUserName());
                return;
            }
        }
        
        System.err.println("User not found for update: " + oldUser.getUserName());
    }
    
    /**
     * Delete a user from CSV
     */
    public static void deleteUserFromCSV(UserData userToDelete) {
        List<UserData> users = loadUsersFromCSV();
        
        // Find and remove the user
        boolean removed = users.removeIf(user -> user.getUserId().equals(userToDelete.getUserId()));
        
        if (removed) {
            saveUsersToCSV(users);
            System.out.println("Deleted user from CSV: " + userToDelete.getUserName());
        } else {
            System.err.println("User not found for deletion: " + userToDelete.getUserName());
        }
    }
    
    /**
     * Find user by ID
     */
    public static UserData findUserById(String userId) {
        List<UserData> users = loadUsersFromCSV();
        return users.stream()
                   .filter(user -> user.getUserId().equals(userId))
                   .findFirst()
                   .orElse(null);
    }
    
    /**
     * Find user by email
     */
    public static UserData findUserByEmail(String email) {
        List<UserData> users = loadUsersFromCSV();
        return users.stream()
                   .filter(user -> user.getEmail().equalsIgnoreCase(email))
                   .findFirst()
                   .orElse(null);
    }
    
    /**
     * Generate next user ID
     */
    public static String generateNextUserId() {
        List<UserData> users = loadUsersFromCSV();
        int maxId = 0;
        
        for (UserData user : users) {
            String userId = user.getUserId();
            if (userId.startsWith("U")) {
                try {
                    int id = Integer.parseInt(userId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("U%04d", maxId + 1);
    }
    
    /**
     * Convert UserData to CSV line
     */
    private static String userToCSVLine(UserData user) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeCSV(user.getUserId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getUserName())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getStudentId() != null ? user.getStudentId() : "")).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getFaculty() != null ? user.getFaculty() : "")).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getEmail())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getUserType())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(user.getPhoneNumber())).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(user.getRegisteredDate()));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to UserData
     */
    private static UserData csvLineToUser(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 8) {
            throw new IllegalArgumentException("Invalid user CSV line format - expected 8 fields, got " + fields.length);
        }
        
        try {
            String userId = fields[0];
            String userName = fields[1];
            String studentId = fields[2].trim().isEmpty() ? null : fields[2];
            String faculty = fields[3].trim().isEmpty() ? null : fields[3];
            String email = fields[4];
            String userType = fields[5];
            String phoneNumber = fields[6];
            Date registeredDate = DATE_FORMAT.parse(fields[7]);
            
            return new UserData(userId, userName, studentId, faculty, email, userType, phoneNumber, registeredDate);
            
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing user CSV fields: " + e.getMessage());
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
            return "Users CSV: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "Users CSV file does not exist yet";
        }
    }
} 