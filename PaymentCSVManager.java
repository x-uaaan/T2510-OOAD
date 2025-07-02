// PaymentCSVManager.java
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentCSVManager {
    private static final String CSV_FILE = "csv/payments.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "paymentId,registrationId,userId,eventId,userName,eventName,paymentAmount,paymentMethod,paymentDate,paymentStatus,transactionId,paymentReference,notes";
    
    /**
     * Save payments to CSV file
     */
    public static void savePaymentsToCSV(List<PaymentData> payments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println(CSV_HEADER);
            
            // Write payment data
            for (PaymentData payment : payments) {
                writer.println(paymentToCSVLine(payment));
            }
            
            System.out.println("Payments saved successfully to " + CSV_FILE + " (" + payments.size() + " payments)");
        } catch (IOException e) {
            System.err.println("Error saving payments to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load payments from CSV file
     */
    public static List<PaymentData> loadPaymentsFromCSV() {
        List<PaymentData> payments = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            System.out.println("Payments CSV file not found. Creating new empty file: " + CSV_FILE);
            // Create empty CSV file with header
            savePaymentsToCSV(payments);
            return payments;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        PaymentData payment = csvLineToPayment(line);
                        if (payment != null) {
                            payments.add(payment);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing payment CSV line: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + payments.size() + " payments from " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Error loading payments from CSV: " + e.getMessage());
        }
        
        return payments;
    }
    
    /**
     * Add a single payment to CSV
     */
    public static void addPaymentToCSV(PaymentData payment) {
        List<PaymentData> payments = loadPaymentsFromCSV();
        payments.add(payment);
        savePaymentsToCSV(payments);
        System.out.println("Added payment to CSV: " + payment.getPaymentId());
    }
    
    /**
     * Update a payment in CSV
     */
    public static void updatePaymentInCSV(PaymentData oldPayment, PaymentData newPayment) {
        List<PaymentData> payments = loadPaymentsFromCSV();
        
        // Find and replace the old payment
        for (int i = 0; i < payments.size(); i++) {
            PaymentData existing = payments.get(i);
            if (existing.getPaymentId().equals(oldPayment.getPaymentId())) {
                payments.set(i, newPayment);
                savePaymentsToCSV(payments);
                System.out.println("Updated payment in CSV: " + newPayment.getPaymentId());
                return;
            }
        }
        
        System.err.println("Payment not found for update: " + oldPayment.getPaymentId());
    }
    
    /**
     * Delete a payment from CSV
     */
    public static void deletePaymentFromCSV(PaymentData paymentToDelete) {
        List<PaymentData> payments = loadPaymentsFromCSV();
        
        // Find and remove the payment
        boolean removed = payments.removeIf(payment -> payment.getPaymentId().equals(paymentToDelete.getPaymentId()));
        
        if (removed) {
            savePaymentsToCSV(payments);
            System.out.println("Deleted payment from CSV: " + paymentToDelete.getPaymentId());
        } else {
            System.err.println("Payment not found for deletion: " + paymentToDelete.getPaymentId());
        }
    }
    
    /**
     * Get payments for a specific registration
     */
    public static List<PaymentData> getPaymentsByRegistrationId(String registrationId) {
        List<PaymentData> allPayments = loadPaymentsFromCSV();
        List<PaymentData> registrationPayments = new ArrayList<>();
        
        for (PaymentData payment : allPayments) {
            if (payment.getRegistrationId().equals(registrationId)) {
                registrationPayments.add(payment);
            }
        }
        
        return registrationPayments;
    }
    
    /**
     * Get payments for a specific user
     */
    public static List<PaymentData> getPaymentsByUserId(String userId) {
        List<PaymentData> allPayments = loadPaymentsFromCSV();
        List<PaymentData> userPayments = new ArrayList<>();
        
        for (PaymentData payment : allPayments) {
            if (payment.getUserId().equals(userId)) {
                userPayments.add(payment);
            }
        }
        
        return userPayments;
    }
    
    /**
     * Get payments for a specific event
     */
    public static List<PaymentData> getPaymentsByEventId(String eventId) {
        List<PaymentData> allPayments = loadPaymentsFromCSV();
        List<PaymentData> eventPayments = new ArrayList<>();
        
        for (PaymentData payment : allPayments) {
            if (payment.getEventId().equals(eventId)) {
                eventPayments.add(payment);
            }
        }
        
        return eventPayments;
    }
    
    /**
     * Get total amount collected for an event
     */
    public static double getTotalCollectedAmountForEvent(String eventId) {
        List<PaymentData> eventPayments = getPaymentsByEventId(eventId);
        double totalAmount = 0.0;
        
        for (PaymentData payment : eventPayments) {
            if ("Completed".equals(payment.getPaymentStatus())) {
                totalAmount += payment.getPaymentAmount();
            }
        }
        
        return totalAmount;
    }
    
    /**
     * Generate next payment ID
     */
    public static String generateNextPaymentId() {
        List<PaymentData> payments = loadPaymentsFromCSV();
        int maxId = 0;
        
        for (PaymentData payment : payments) {
            String paymentId = payment.getPaymentId();
            if (paymentId.startsWith("P")) {
                try {
                    int id = Integer.parseInt(paymentId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("P%04d", maxId + 1);
    }
    
    /**
     * Convert PaymentData to CSV line
     */
    private static String paymentToCSVLine(PaymentData payment) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(escapeCSV(payment.getPaymentId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getRegistrationId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getUserId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getEventId())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getUserName())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getEventName())).append(CSV_SEPARATOR);
        sb.append(payment.getPaymentAmount()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getPaymentMethod())).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(payment.getPaymentDate())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getPaymentStatus())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getTransactionId() != null ? payment.getTransactionId() : "")).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getPaymentReference() != null ? payment.getPaymentReference() : "")).append(CSV_SEPARATOR);
        sb.append(escapeCSV(payment.getNotes() != null ? payment.getNotes() : ""));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to PaymentData
     */
    private static PaymentData csvLineToPayment(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 13) {
            throw new IllegalArgumentException("Invalid payment CSV line format - expected 13 fields, got " + fields.length);
        }
        
        try {
            String paymentId = fields[0];
            String registrationId = fields[1];
            String userId = fields[2];
            String eventId = fields[3];
            String userName = fields[4];
            String eventName = fields[5];
            double paymentAmount = Double.parseDouble(fields[6]);
            String paymentMethod = fields[7];
            Date paymentDate = DATE_FORMAT.parse(fields[8]);
            String paymentStatus = fields[9];
            String transactionId = fields[10].trim().isEmpty() ? null : fields[10];
            String paymentReference = fields[11].trim().isEmpty() ? null : fields[11];
            String notes = fields[12].trim().isEmpty() ? null : fields[12];
            
            return new PaymentData(paymentId, registrationId, userId, eventId, userName, eventName,
                                 paymentAmount, paymentMethod, paymentDate, paymentStatus,
                                 transactionId, paymentReference, notes);
            
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing payment CSV fields: " + e.getMessage());
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
            return "Payments CSV: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "Payments CSV file does not exist yet";
        }
    }
} 