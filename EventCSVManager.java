import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventCSVManager {
    private static final String CSV_FILE = "csv/events.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // CSV Header
    private static final String CSV_HEADER = "eventId,name,organiser,eventType,venue,capacity,date,fee,description,fixedCost,variableCost,earlyBirdEnabled,earlyBirdEnd,earlyBirdDiscountType,earlyBirdDiscountValue,promoEnabled,promoCode,promoDiscountType,promoDiscount,status,authorId,authorName";
    
    /**
     * Save events to CSV file
     */
    public static void saveEventsToCSV(List<EventData> events) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            writer.println(CSV_HEADER);
            
            // Write event data
            for (EventData event : events) {
                writer.println(eventToCSVLine(event));
            }
            
            System.out.println("Events saved successfully to " + CSV_FILE + " (" + events.size() + " events)");
        } catch (IOException e) {
            System.err.println("Error saving events to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Load events from CSV file
     */
    public static List<EventData> loadEventsFromCSV() {
        List<EventData> events = new ArrayList<>();
        File file = new File(CSV_FILE);
        
        if (!file.exists()) {
            System.out.println("CSV file not found. Creating new empty file: " + CSV_FILE);
            // Create empty CSV file with header
            saveEventsToCSV(events);
            return events;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        EventData event = csvLineToEvent(line);
                        if (event != null) {
                            events.add(event);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing CSV line: " + line);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Loaded " + events.size() + " events from " + CSV_FILE);
        } catch (IOException e) {
            System.err.println("Error loading events from CSV: " + e.getMessage());
        }
        
        return events;
    }
    
    /**
     * Add a single event to CSV
     */
    public static void addEventToCSV(EventData event) {
        List<EventData> events = loadEventsFromCSV();
        events.add(event);
        saveEventsToCSV(events);
        System.out.println("Added event to CSV: " + event.getName());
    }
    
    /**
     * Update an event in CSV
     */
    public static void updateEventInCSV(EventData oldEvent, EventData newEvent) {
        List<EventData> events = loadEventsFromCSV();
        
        // Find and replace the old event
        for (int i = 0; i < events.size(); i++) {
            EventData existing = events.get(i);
            if (eventsMatch(existing, oldEvent)) {
                events.set(i, newEvent);
                saveEventsToCSV(events);
                System.out.println("Updated event in CSV: " + newEvent.getName());
                return;
            }
        }
        
        System.err.println("Event not found for update: " + oldEvent.getName());
    }
    
    /**
     * Delete an event from CSV
     */
    public static void deleteEventFromCSV(EventData eventToDelete) {
        List<EventData> events = loadEventsFromCSV();
        
        // Find and remove the event
        boolean removed = events.removeIf(event -> eventsMatch(event, eventToDelete));
        
        if (removed) {
            saveEventsToCSV(events);
            System.out.println("Deleted event from CSV: " + eventToDelete.getName());
        } else {
            System.err.println("Event not found for deletion: " + eventToDelete.getName());
        }
    }
    
    /**
     * Check if two events match (for update/delete operations)
     */
    private static boolean eventsMatch(EventData event1, EventData event2) {
        // If both have eventIds, compare by eventId
        if (event1.getEventId() != null && event2.getEventId() != null) {
            return event1.getEventId().equals(event2.getEventId());
        }
        
        // Fallback to field comparison for backward compatibility
        return event1.getName().equals(event2.getName()) &&
               event1.getOrganiser().equals(event2.getOrganiser()) &&
               event1.getDate().equals(event2.getDate()) &&
               event1.getVenue().equals(event2.getVenue());
    }
    
    /**
     * Convert EventData to CSV line
     */
    private static String eventToCSVLine(EventData event) {
        StringBuilder sb = new StringBuilder();
        
        // Event ID field
        sb.append(escapeCSV(event.getEventId())).append(CSV_SEPARATOR);
        
        // Basic fields
        sb.append(escapeCSV(event.getName())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getOrganiser())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getEventType())).append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getVenue())).append(CSV_SEPARATOR);
        sb.append(event.getCapacity()).append(CSV_SEPARATOR);
        sb.append(DATE_FORMAT.format(event.getDate())).append(CSV_SEPARATOR);
        sb.append(event.getFee()).append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getDescription())).append(CSV_SEPARATOR);
        sb.append(event.getFixedCost()).append(CSV_SEPARATOR);
        sb.append(event.getVariableCost()).append(CSV_SEPARATOR);
        
        // Early bird fields
        sb.append(event.isEarlyBirdEnabled()).append(CSV_SEPARATOR);
        if (event.getEarlyBirdEnd() != null) {
            sb.append(DATE_FORMAT.format(event.getEarlyBirdEnd()));
        }
        sb.append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getEarlyBirdDiscountType())).append(CSV_SEPARATOR);
        sb.append(event.getEarlyBirdDiscountValue()).append(CSV_SEPARATOR);
        
        // Promo fields
        sb.append(event.isPromoEnabled()).append(CSV_SEPARATOR);
        if (event.getPromoCode() != null) {
            sb.append(escapeCSV(event.getPromoCode()));
        }
        sb.append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getPromoDiscountType())).append(CSV_SEPARATOR);
        sb.append(event.getPromoDiscount());
        sb.append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getStatus()));
        sb.append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getAuthorId()));
        sb.append(CSV_SEPARATOR);
        sb.append(escapeCSV(event.getAuthorName()));
        
        return sb.toString();
    }
    
    /**
     * Convert CSV line to EventData
     */
    private static EventData csvLineToEvent(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 22) {
            throw new IllegalArgumentException("Invalid CSV line format - expected 22 fields, got " + fields.length);
        }
        
        try {
            // Parse event ID
            String eventId = fields[0];
            
            // Parse basic fields
            String name = fields[1];
            String organiser = fields[2];
            String eventType = fields[3];
            String venue = fields[4];
            int capacity = Integer.parseInt(fields[5]);
            Date date = DATE_FORMAT.parse(fields[6]);
            double fee = Double.parseDouble(fields[7]);
            String description = fields[8];
            double fixedCost = Double.parseDouble(fields[9]);
            double variableCost = Double.parseDouble(fields[10]);
            
            // Parse early bird fields
            boolean earlyBirdEnabled = Boolean.parseBoolean(fields[11]);
            Date earlyBirdEnd = fields[12].isEmpty() ? null : DATE_FORMAT.parse(fields[12]);
            String earlyBirdDiscountType = fields[13].trim().isEmpty() ? null : fields[13];
            double earlyBirdDiscountValue = Double.parseDouble(fields[14]);
            
            // Parse promo fields
            boolean promoEnabled = Boolean.parseBoolean(fields[15]);
            String promoCode = fields[16].trim().isEmpty() ? null : fields[16];
            String promoDiscountType = fields[17].trim().isEmpty() ? null : fields[17];
            double promoDiscount = Double.parseDouble(fields[18]);
            
            // Parse status
            String status = (fields.length > 19 && fields[19] != null && !fields[19].isEmpty()) ? fields[19] : "Active";
            String authorId = (fields.length > 20) ? fields[20] : null;
            String authorName = (fields.length > 21) ? fields[21] : null;
            
            return new EventData(eventId, name, organiser, eventType, venue, capacity, date, fee, description, fixedCost, variableCost,
                    earlyBirdEnabled, earlyBirdEnd, earlyBirdDiscountType, earlyBirdDiscountValue, 
                    promoEnabled, promoCode, promoDiscountType, promoDiscount, status, authorId, authorName);
                    
        } catch (ParseException | NumberFormatException e) {
            throw new IllegalArgumentException("Error parsing CSV fields: " + e.getMessage());
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
     * Check if CSV file exists
     */
    public static boolean csvFileExists() {
        return new File(CSV_FILE).exists();
    }
    
    /**
     * Get CSV file path
     */
    public static String getCSVFilePath() {
        return new File(CSV_FILE).getAbsolutePath();
    }
    
    /**
     * Generate next event ID
     */
    public static String generateNextEventId() {
        List<EventData> events = loadEventsFromCSV();
        int maxId = 0;
        
        for (EventData event : events) {
            String eventId = event.getEventId();
            if (eventId != null && eventId.startsWith("E")) {
                try {
                    int id = Integer.parseInt(eventId.substring(1));
                    maxId = Math.max(maxId, id);
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }
        
        return String.format("E%04d", maxId + 1);
    }

    /**
     * Get CSV file info
     */
    public static String getCSVInfo() {
        File file = new File(CSV_FILE);
        if (file.exists()) {
            return "CSV file: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)";
        } else {
            return "CSV file does not exist yet";
        }
    }
} 