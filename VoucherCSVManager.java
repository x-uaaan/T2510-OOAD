import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VoucherCSVManager {
    private static final String CSV_FILE = "csv/vouchers.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "voucherId,voucherCode,voucherType,discountType,discountValue,description,validFrom,validUntil,usageLimit,usedCount,minimumAmount,minimumPax,applicableEvents,userTypeEligible,isActive,createdBy,createdDate";
    
    /**
     * Save all vouchers to CSV
     */
    public static void saveVouchersToCSV(List<VoucherData> vouchers) {
        try {
            // Create directory if it doesn't exist
            File csvFile = new File(CSV_FILE);
            csvFile.getParentFile().mkdirs();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile))) {
                // Write header
                writer.println(CSV_HEADER);
                
                // Write data
                for (VoucherData voucher : vouchers) {
                    writer.println(voucherToCSVLine(voucher));
                }
            }
            
            System.out.println("Vouchers saved successfully to " + CSV_FILE + " (" + vouchers.size() + " vouchers)");
            
        } catch (IOException e) {
            System.err.println("Error saving vouchers to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load all vouchers from CSV
     */
    public static List<VoucherData> loadVouchersFromCSV() {
        List<VoucherData> vouchers = new ArrayList<>();
        File csvFile = new File(CSV_FILE);
        
        if (!csvFile.exists()) {
            System.out.println("Vouchers CSV file does not exist yet: " + CSV_FILE);
            // Create default vouchers
            createDefaultVouchers();
            return loadVouchersFromCSV(); // Reload after creating defaults
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        VoucherData voucher = csvLineToVoucher(line);
                        vouchers.add(voucher);
                    } catch (Exception e) {
                        System.err.println("Error parsing voucher line: " + line + " - " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + vouchers.size() + " vouchers from " + CSV_FILE);
            
        } catch (IOException e) {
            System.err.println("Error loading vouchers from CSV: " + e.getMessage());
        }
        
        return vouchers;
    }
    
    /**
     * Create default vouchers for the system
     */
    private static void createDefaultVouchers() {
        List<VoucherData> defaultVouchers = new ArrayList<>();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        
        // Early Bird Discount - 15% off
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 30);
        Date earlyBirdEnd = cal.getTime();
        defaultVouchers.add(new VoucherData(
            "V0001", "EARLYBIRD15", "EARLY_BIRD", "PERCENTAGE", 15.0,
            "Early Bird Special - 15% discount for advance bookings",
            now, earlyBirdEnd, "SYSTEM"
        ));
        
        // Group Discount - RM20 off for 5+ people
        cal.setTime(now);
        cal.add(Calendar.MONTH, 6);
        Date groupEnd = cal.getTime();
        VoucherData groupDiscount = new VoucherData(
            "V0002", "GROUP20", "GROUP", "FIXED_AMOUNT", 20.0,
            "Group Discount - RM20 off for 5 or more participants",
            now, groupEnd, "SYSTEM"
        );
        groupDiscount.setUsageLimit(-1); // Unlimited
        // Set minimum pax to 5 for group discount
        defaultVouchers.add(groupDiscount);
        
        // Student Member Discount - 10% off for students
        cal.setTime(now);
        cal.add(Calendar.YEAR, 1);
        Date memberEnd = cal.getTime();
        VoucherData studentDiscount = new VoucherData(
            "V0003", "STUDENT10", "MEMBER", "PERCENTAGE", 10.0,
            "Student Member Discount - 10% off for students",
            now, memberEnd, "SYSTEM"
        );
        // This would need to be set up properly in the full constructor
        defaultVouchers.add(studentDiscount);
        
        // Welcome Promo Code - RM10 off
        cal.setTime(now);
        cal.add(Calendar.MONTH, 3);
        Date promoEnd = cal.getTime();
        defaultVouchers.add(new VoucherData(
            "V0004", "WELCOME10", "PROMO_CODE", "FIXED_AMOUNT", 10.0,
            "Welcome Promo - RM10 off your first event registration",
            now, promoEnd, "SYSTEM"
        ));
        
        saveVouchersToCSV(defaultVouchers);
        System.out.println("Created default vouchers");
    }
    
    /**
     * Add new voucher to CSV
     */
    public static void addVoucherToCSV(VoucherData voucher) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        vouchers.add(voucher);
        saveVouchersToCSV(vouchers);
        System.out.println("Added voucher: " + voucher);
    }
    
    /**
     * Update voucher in CSV
     */
    public static void updateVoucherInCSV(VoucherData oldVoucher, VoucherData newVoucher) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        
        for (int i = 0; i < vouchers.size(); i++) {
            if (vouchers.get(i).equals(oldVoucher)) {
                vouchers.set(i, newVoucher);
                saveVouchersToCSV(vouchers);
                System.out.println("Updated voucher: " + newVoucher);
                return;
            }
        }
        
        System.err.println("Voucher not found for update: " + oldVoucher);
    }
    
    /**
     * Delete voucher from CSV
     */
    public static void deleteVoucherFromCSV(VoucherData voucherToDelete) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        
        if (vouchers.remove(voucherToDelete)) {
            saveVouchersToCSV(vouchers);
            System.out.println("Deleted voucher: " + voucherToDelete);
        } else {
            System.err.println("Voucher not found for deletion: " + voucherToDelete);
        }
    }
    
    /**
     * Find voucher by code
     */
    public static VoucherData findVoucherByCode(String voucherCode) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        
        for (VoucherData voucher : vouchers) {
            if (voucher.getVoucherCode().equalsIgnoreCase(voucherCode)) {
                return voucher;
            }
        }
        
        return null;
    }
    
    /**
     * Create platform vouchers for new user automatically
     */
    public static void createNewUserVouchers(String userId) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        
        // Welcome RM10 Voucher (available 1 month)
        cal.setTime(now);
        cal.add(Calendar.MONTH, 1); // Valid for 1 month
        Date welcomeValidUntil = cal.getTime();
        
        String welcomeVoucherId = generateNextVoucherId();
        String welcomeVoucherCode = "WELCOME10_" + userId; // Unique code per user
        
        VoucherData welcomeVoucher = new VoucherData(
            welcomeVoucherId, welcomeVoucherCode, "WELCOME", "FIXED_AMOUNT", 10.0,
            "Welcome Bonus - RM10 off your first registration",
            now, welcomeValidUntil, 1, 0, 0.0, 1, "ALL", "ALL", true, "SYSTEM", now
        );
        
        addVoucherToCSV(welcomeVoucher);
        
        // Student Voucher (available 1 year)
        cal.setTime(now);
        cal.add(Calendar.YEAR, 1); // Valid for 1 year
        Date studentValidUntil = cal.getTime();
        
        String studentVoucherId = generateNextVoucherId();
        String studentVoucherCode = "STUDENT10_" + userId; // Unique code per user
        
        VoucherData studentVoucher = new VoucherData(
            studentVoucherId, studentVoucherCode, "STUDENT", "PERCENTAGE", 10.0,
            "Student Discount - 10% off for students",
            now, studentValidUntil, 1, 0, 0.0, 1, "ALL", "STUDENT", true, "SYSTEM", now
        );
        
        addVoucherToCSV(studentVoucher);
        
        System.out.println("Created welcome and student vouchers for user: " + userId);
    }
    
    /**
     * Create group order vouchers for user automatically
     */
    public static void createGroupOrderVouchers(String userId) {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, 1); // Valid for 1 year
        Date validUntil = cal.getTime();
        
        // Create 5 group order vouchers for the user
        for (int i = 1; i <= 5; i++) {
            String voucherId = generateNextVoucherId();
            String voucherCode = "GROUP" + i + "_" + userId; // Unique code per voucher
            
            VoucherData groupVoucher = new VoucherData(
                voucherId, voucherCode, "GROUP_ORDER", "FIXED_AMOUNT", 30.0,
                "Group Order Discount #" + i + " - RM30 off for 5+ participants",
                now, validUntil, 1, 0, 0.0, 5, "ALL", "ALL", true, "SYSTEM", now
            );
            
            addVoucherToCSV(groupVoucher);
        }
        
        System.out.println("Created 5 group order vouchers for user: " + userId);
    }
    
    /**
     * Check if user has welcome voucher available
     */
    public static VoucherData getUserWelcomeVoucher(String userId) {
        // Check if user has ever registered for an event before
        if (hasUserRegisteredBefore(userId)) {
            // User has registered before, hide welcome voucher
            return null;
        }
        
        List<VoucherData> vouchers = loadVouchersFromCSV();
        String expectedCode = "WELCOME10_" + userId;
        
        for (VoucherData voucher : vouchers) {
            if ("WELCOME".equals(voucher.getVoucherType()) && 
                expectedCode.equals(voucher.getVoucherCode()) &&
                voucher.isCurrentlyValid() &&
                voucher.getUsedCount() < voucher.getUsageLimit()) {
                return voucher;
            }
        }
        
        return null;
    }
    
    /**
     * Check if user has any previous registrations
     */
    public static boolean hasUserRegisteredBefore(String userId) {
        List<RegistrationData> userRegistrations = RegistrationCSVManager.getRegistrationsByUserId(userId);
        return !userRegistrations.isEmpty();
    }
    
    /**
     * Deactivate welcome voucher for user after their first registration
     */
    public static void deactivateWelcomeVoucherForUser(String userId) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        String expectedCode = "WELCOME10_" + userId;
        boolean found = false;
        
        for (VoucherData voucher : vouchers) {
            if ("WELCOME".equals(voucher.getVoucherType()) && 
                expectedCode.equals(voucher.getVoucherCode()) &&
                voucher.isActive()) {
                
                // Create updated voucher with isActive = false
                VoucherData updatedVoucher = new VoucherData(
                    voucher.getVoucherId(),
                    voucher.getVoucherCode(),
                    voucher.getVoucherType(),
                    voucher.getDiscountType(),
                    voucher.getDiscountValue(),
                    voucher.getDescription(),
                    voucher.getValidFrom(),
                    voucher.getValidUntil(),
                    voucher.getUsageLimit(),
                    voucher.getUsedCount(),
                    voucher.getMinimumAmount(),
                    voucher.getMinimumPax(),
                    voucher.getApplicableEvents(),
                    voucher.getUserTypeEligible(),
                    false, // Set isActive to false
                    voucher.getCreatedBy(),
                    voucher.getCreatedDate()
                );
                
                updateVoucherInCSV(voucher, updatedVoucher);
                found = true;
                System.out.println("Deactivated welcome voucher for user: " + userId);
                break;
            }
        }
        
        if (!found) {
            System.out.println("No active welcome voucher found for user: " + userId);
        }
    }
    
    /**
     * Check if user has student voucher available
     */
    public static VoucherData getUserStudentVoucher(String userId) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        String expectedCode = "STUDENT10_" + userId;
        
        for (VoucherData voucher : vouchers) {
            if ("STUDENT".equals(voucher.getVoucherType()) && 
                expectedCode.equals(voucher.getVoucherCode()) &&
                voucher.isCurrentlyValid() &&
                voucher.getUsedCount() < voucher.getUsageLimit()) {
                return voucher;
            }
        }
        
        return null;
    }
    
    /**
     * Check if user has group order voucher available
     */
    public static VoucherData getUserGroupOrderVoucher(String userId) {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        
        // Look for any available group voucher for this user (GROUP1_, GROUP2_, etc.)
        for (VoucherData voucher : vouchers) {
            if ("GROUP_ORDER".equals(voucher.getVoucherType()) && 
                voucher.getVoucherCode().contains("_" + userId) &&
                voucher.getVoucherCode().startsWith("GROUP") &&
                voucher.isCurrentlyValid() &&
                voucher.getUsedCount() < voucher.getUsageLimit()) {
                return voucher; // Return the first available group voucher
            }
        }
        
        return null;
    }
    
    /**
     * Auto-apply platform vouchers based on conditions
     */
    public static List<VoucherData> getAutoApplicablePlatformVouchers(String userId, int pax) {
        List<VoucherData> autoVouchers = new ArrayList<>();
        
        // Check for welcome voucher
        VoucherData welcomeVoucher = getUserWelcomeVoucher(userId);
        if (welcomeVoucher != null) {
            autoVouchers.add(welcomeVoucher);
        }
        
        // Check for student voucher  
        VoucherData studentVoucher = getUserStudentVoucher(userId);
        if (studentVoucher != null) {
            autoVouchers.add(studentVoucher);
        }
        
        // Check for group order voucher (ONLY if pax >= 5)
        if (pax >= 5) {
            VoucherData groupVoucher = getUserGroupOrderVoucher(userId);
            if (groupVoucher != null) {
                autoVouchers.add(groupVoucher);
            }
        }
        
        return autoVouchers;
    }
    
    /**
     * Sort vouchers by valid date (closest expiry first) - for available voucher window
     */
    public static List<VoucherData> sortVouchersByValidDate(List<VoucherData> vouchers) {
        List<VoucherData> sortedVouchers = new ArrayList<>(vouchers);
        sortedVouchers.sort((v1, v2) -> {
            Date date1 = v1.getValidUntil();
            Date date2 = v2.getValidUntil();
            
            // Handle null dates (put them at the end)
            if (date1 == null && date2 == null) return 0;
            if (date1 == null) return 1;
            if (date2 == null) return -1;
            
            // Sort by closest expiry date first
            return date1.compareTo(date2);
        });
        return sortedVouchers;
    }
    
    /**
     * Sort vouchers for selection dialog by save amount (highest first)
     */
    public static List<VoucherData> sortVouchersForSelection(List<VoucherData> vouchers, double baseAmount) {
        List<VoucherData> sortedVouchers = new ArrayList<>(vouchers);
        
        sortedVouchers.sort((v1, v2) -> {
            // Sort by save amount (highest first)
            double save1 = v1.calculateDiscountAmount(baseAmount);
            double save2 = v2.calculateDiscountAmount(baseAmount);
            
            return Double.compare(save2, save1); // Descending order (highest savings first)
        });
        
        return sortedVouchers;
    }
    
    /**
     * Check if voucher is event-based (promo code or early bird from specific event)
     */
    private static boolean isEventVoucher(VoucherData voucher) {
        String type = voucher.getVoucherType();
        String code = voucher.getVoucherCode();
        
        // Event promo codes or early bird vouchers are event-based
        return "PROMO_CODE".equals(type) || 
               "EARLY_BIRD".equals(type) ||
               code.startsWith("EVENT_PROMO:");
    }

    /**
     * Get all active vouchers
     */
    public static List<VoucherData> getActiveVouchers() {
        List<VoucherData> allVouchers = loadVouchersFromCSV();
        List<VoucherData> activeVouchers = new ArrayList<>();
        
        for (VoucherData voucher : allVouchers) {
            if (voucher.isCurrentlyValid()) {
                activeVouchers.add(voucher);
            }
        }
        
        return activeVouchers;
    }
    
    /**
     * Get applicable vouchers for specific criteria
     */
    public static List<VoucherData> getApplicableVouchers(double amount, int pax, String eventId, String userType) {
        List<VoucherData> allVouchers = loadVouchersFromCSV();
        List<VoucherData> applicableVouchers = new ArrayList<>();
        
        for (VoucherData voucher : allVouchers) {
            if (voucher.isApplicableFor(amount, pax, eventId, userType)) {
                applicableVouchers.add(voucher);
            }
        }
        
        return applicableVouchers;
    }
    
    /**
     * Get vouchers by type
     */
    public static List<VoucherData> getVouchersByType(String voucherType) {
        List<VoucherData> allVouchers = loadVouchersFromCSV();
        List<VoucherData> typeVouchers = new ArrayList<>();
        
        for (VoucherData voucher : allVouchers) {
            if (voucher.getVoucherType().equals(voucherType)) {
                typeVouchers.add(voucher);
            }
        }
        
        return typeVouchers;
    }
    
    /**
     * Update voucher usage count
     */
    public static void incrementVoucherUsage(String voucherCode) {
        VoucherData voucher = findVoucherByCode(voucherCode);
        if (voucher != null) {
            VoucherData oldVoucher = voucher;
            voucher.incrementUsage();
            updateVoucherInCSV(oldVoucher, voucher);
            System.out.println("Incremented usage for voucher: " + voucherCode);
        }
    }
    
    /**
     * Generate next voucher ID
     */
    public static String generateNextVoucherId() {
        List<VoucherData> vouchers = loadVouchersFromCSV();
        int maxId = 0;
        
        for (VoucherData voucher : vouchers) {
            String voucherId = voucher.getVoucherId();
            if (voucherId.startsWith("V")) {
                try {
                    int id = Integer.parseInt(voucherId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("V%04d", maxId + 1);
    }
    
    /**
     * Check if a user has already used a specific voucher
     */
    public static boolean hasUserUsedVoucher(String userId, String voucherCode) {
        try {
            List<RegistrationData> registrations = RegistrationCSVManager.loadRegistrationsFromCSV();
            
            for (RegistrationData registration : registrations) {
                if (registration.getUserId().equals(userId) && 
                    registration.getPromoCodeUsed() != null) {
                    
                    // Check if any of the used vouchers match
                    String[] usedVouchers = registration.getPromoCodeUsed().split(",");
                    for (String usedVoucher : usedVouchers) {
                        if (usedVoucher.trim().equals(voucherCode)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking voucher usage for user " + userId + ": " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Validate and apply voucher to calculate discount
     */
    public static double applyVoucher(String voucherCode, double originalAmount, int pax, String eventId, String userType) {
        VoucherData voucher = findVoucherByCode(voucherCode);
        
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher code not found: " + voucherCode);
        }
        
        if (!voucher.isApplicableFor(originalAmount, pax, eventId, userType)) {
            throw new IllegalArgumentException("Voucher is not applicable for this registration");
        }
        
        double discountAmount = voucher.calculateDiscountAmount(originalAmount);
        
        // Increment usage (this will be done when payment is confirmed)
        // incrementVoucherUsage(voucherCode);
        
        return discountAmount;
    }
    
    /**
     * Convert VoucherData to CSV line
     */
    private static String voucherToCSVLine(VoucherData voucher) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeCSV(voucher.getVoucherId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getVoucherCode())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getVoucherType())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getDiscountType())).append(CSV_SEPARATOR);
        sb.append(voucher.getDiscountValue()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getDescription())).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(voucher.getValidFrom())).append(CSV_SEPARATOR);
        sb.append(voucher.getValidUntil() != null ? DATE_FORMAT.format(voucher.getValidUntil()) : "").append(CSV_SEPARATOR);
        sb.append(voucher.getUsageLimit()).append(CSV_SEPARATOR);
        sb.append(voucher.getUsedCount()).append(CSV_SEPARATOR);
        sb.append(voucher.getMinimumAmount()).append(CSV_SEPARATOR);
        sb.append(voucher.getMinimumPax()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getApplicableEvents())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getUserTypeEligible())).append(CSV_SEPARATOR);
        sb.append(voucher.isActive()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(voucher.getCreatedBy())).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(voucher.getCreatedDate()));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to VoucherData
     */
    private static VoucherData csvLineToVoucher(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 17) {
            throw new IllegalArgumentException("Invalid voucher CSV line format - expected 17 fields, got " + fields.length);
        }
        
        try {
            String voucherId = fields[0];
            String voucherCode = fields[1];
            String voucherType = fields[2];
            String discountType = fields[3];
            double discountValue = Double.parseDouble(fields[4]);
            String description = fields[5];
            Date validFrom = DATE_FORMAT.parse(fields[6]);
            Date validUntil = fields[7].trim().isEmpty() ? null : DATE_FORMAT.parse(fields[7]);
            int usageLimit = Integer.parseInt(fields[8]);
            int usedCount = Integer.parseInt(fields[9]);
            double minimumAmount = Double.parseDouble(fields[10]);
            int minimumPax = Integer.parseInt(fields[11]);
            String applicableEvents = fields[12];
            String userTypeEligible = fields[13];
            boolean isActive = Boolean.parseBoolean(fields[14]);
            String createdBy = fields[15];
            Date createdDate = DATE_FORMAT.parse(fields[16]);
            
            return new VoucherData(voucherId, voucherCode, voucherType, discountType, discountValue,
                                 description, validFrom, validUntil, usageLimit, usedCount,
                                 minimumAmount, minimumPax, applicableEvents, userTypeEligible,
                                 isActive, createdBy, createdDate);
            
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing voucher CSV fields: " + e.getMessage());
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
            return "Vouchers CSV: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "Vouchers CSV file does not exist yet";
        }
    }
} 