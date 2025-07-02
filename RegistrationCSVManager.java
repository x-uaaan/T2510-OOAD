// RegistrationCSVManager.java
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistrationCSVManager {
    private static final String CSV_FILE = "csv/registrations.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "registrationId,eventId,userId,eventName,userName,registrationDate,pax,catering,transportation,promoCodeUsed,baseAmount,discountAmount,totalAmount,status";
    
    /**
     * Save registrations to CSV file
     */
    public static void saveRegistrationsToCSV(List<RegistrationData> registrations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println(CSV_HEADER);
            
            // Write registration data
            for (RegistrationData registration : registrations) {
                writer.println(registrationToCSVLine(registration));
            }
            
            System.out.println("Registrations saved successfully to " + CSV_FILE + " (" + registrations.size() + " registrations)");
        } catch (IOException e) {
            System.err.println("Error saving registrations to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load registrations from CSV file
     */
    public static List<RegistrationData> loadRegistrationsFromCSV() {
        List<RegistrationData> registrations = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            System.out.println("Registrations CSV file not found. Creating new empty file: " + CSV_FILE);
            // Create empty CSV file with header
            saveRegistrationsToCSV(registrations);
            return registrations;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        RegistrationData registration = csvLineToRegistration(line);
                        if (registration != null) {
                            registrations.add(registration);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing registration CSV line: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + registrations.size() + " registrations from " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Error loading registrations from CSV: " + e.getMessage());
        }
        
        return registrations;
    }
    
    /**
     * Add a single registration to CSV
     */
    public static void addRegistrationToCSV(RegistrationData registration) {
        List<RegistrationData> registrations = loadRegistrationsFromCSV();
        registrations.add(registration);
        saveRegistrationsToCSV(registrations);
        System.out.println("Added registration to CSV: " + registration.getRegistrationId());
    }
    
    /**
     * Update a registration in CSV
     */
    public static void updateRegistrationInCSV(RegistrationData oldRegistration, RegistrationData newRegistration) {
        List<RegistrationData> registrations = loadRegistrationsFromCSV();
        
        // Find and replace the old registration
        for (int i = 0; i < registrations.size(); i++) {
            RegistrationData existing = registrations.get(i);
            if (existing.getRegistrationId().equals(oldRegistration.getRegistrationId())) {
                registrations.set(i, newRegistration);
                saveRegistrationsToCSV(registrations);
                System.out.println("Updated registration in CSV: " + newRegistration.getRegistrationId());
                return;
            }
        }
        
        System.err.println("Registration not found for update: " + oldRegistration.getRegistrationId());
    }
    
    /**
     * Delete a registration from CSV
     */
    public static void deleteRegistrationFromCSV(RegistrationData registrationToDelete) {
        List<RegistrationData> registrations = loadRegistrationsFromCSV();
        
        // Find and remove the registration
        boolean removed = registrations.removeIf(reg -> reg.getRegistrationId().equals(registrationToDelete.getRegistrationId()));
        
        if (removed) {
            saveRegistrationsToCSV(registrations);
            System.out.println("Deleted registration from CSV: " + registrationToDelete.getRegistrationId());
        } else {
            System.err.println("Registration not found for deletion: " + registrationToDelete.getRegistrationId());
        }
    }
    
    /**
     * Get registrations for a specific event
     */
    public static List<RegistrationData> getRegistrationsByEventId(String eventId) {
        List<RegistrationData> allRegistrations = loadRegistrationsFromCSV();
        List<RegistrationData> eventRegistrations = new ArrayList<>();
        
        for (RegistrationData registration : allRegistrations) {
            if (registration.getEventId().equals(eventId)) {
                eventRegistrations.add(registration);
            }
        }
        
        return eventRegistrations;
    }
    
    /**
     * Get registrations for a specific user
     */
    public static List<RegistrationData> getRegistrationsByUserId(String userId) {
        List<RegistrationData> allRegistrations = loadRegistrationsFromCSV();
        List<RegistrationData> userRegistrations = new ArrayList<>();
        
        for (RegistrationData registration : allRegistrations) {
            if (registration.getUserId().equals(userId)) {
                userRegistrations.add(registration);
            }
        }
        
        return userRegistrations;
    }
    
    /**
     * Get total registered pax for an event
     */
    public static int getTotalRegisteredPaxForEvent(String eventId) {
        List<RegistrationData> eventRegistrations = getRegistrationsByEventId(eventId);
        int totalPax = 0;
        
        for (RegistrationData registration : eventRegistrations) {
            if (!"Cancelled".equals(registration.getStatus())) {
                totalPax += registration.getPax();
            }
        }
        
        return totalPax;
    }
    
    /**
     * Generate next registration ID
     */
    public static String generateNextRegistrationId() {
        List<RegistrationData> registrations = loadRegistrationsFromCSV();
        int maxId = 0;
        
        for (RegistrationData registration : registrations) {
            String regId = registration.getRegistrationId();
            if (regId.startsWith("R")) {
                try {
                    int id = Integer.parseInt(regId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("R%04d", maxId + 1);
    }
    
    /**
     * Convert RegistrationData to CSV line
     */
    private static String registrationToCSVLine(RegistrationData registration) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeCSV(registration.getRegistrationId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getEventId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getUserId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getEventName())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getUserName())).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(registration.getRegistrationDate())).append(CSV_SEPARATOR);
        sb.append(registration.getPax()).append(CSV_SEPARATOR);
        sb.append(registration.isCatering()).append(CSV_SEPARATOR);
        sb.append(registration.isTransportation()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getPromoCodeUsed() != null ? registration.getPromoCodeUsed() : "")).append(CSV_SEPARATOR);
        sb.append(registration.getBaseAmount()).append(CSV_SEPARATOR);
        sb.append(registration.getDiscountAmount()).append(CSV_SEPARATOR);
        sb.append(registration.getTotalAmount()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(registration.getStatus()));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to RegistrationData
     */
    private static RegistrationData csvLineToRegistration(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 14) {
            throw new IllegalArgumentException("Invalid registration CSV line format - expected 14 fields, got " + fields.length);
        }
        
        try {
            String registrationId = fields[0];
            String eventId = fields[1];
            String userId = fields[2];
            String eventName = fields[3];
            String userName = fields[4];
            Date registrationDate = DATE_FORMAT.parse(fields[5]);
            int pax = Integer.parseInt(fields[6]);
            boolean catering = Boolean.parseBoolean(fields[7]);
            boolean transportation = Boolean.parseBoolean(fields[8]);
            String promoCodeUsed = fields[9].trim().isEmpty() ? null : fields[9];
            double baseAmount = Double.parseDouble(fields[10]);
            double discountAmount = Double.parseDouble(fields[11]);
            double totalAmount = Double.parseDouble(fields[12]);
            String status = fields[13];
            
            return new RegistrationData(registrationId, eventId, userId, eventName, userName,
                                      registrationDate, pax, catering, transportation,
                                      promoCodeUsed, baseAmount, discountAmount, totalAmount, status);
            
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing registration CSV fields: " + e.getMessage());
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
            return "Registrations CSV: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "Registrations CSV file does not exist yet";
        }
    }
} 