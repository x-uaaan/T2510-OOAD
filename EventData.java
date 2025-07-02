// EventData.java
import java.util.Date;

// EventData class to hold event info
class EventData {
    private String eventId;
    private String name, organiser, eventType, venue;
    private int capacity;
    private Date date;
    private double fee;
    // New fields for discounts
    private boolean earlyBirdEnabled;
    private Date earlyBirdEnd;
    private boolean promoEnabled;
    private String promoCode;
    private double promoDiscount;
    
    // Full constructor with eventId
    public EventData(String eventId, String name, String organiser, String eventType, String venue, int capacity, Date date, double fee, boolean earlyBirdEnabled, Date earlyBirdEnd, boolean promoEnabled, String promoCode, double promoDiscount) {
        this.eventId = eventId;
        this.name = name;
        this.organiser = organiser;
        this.eventType = eventType;
        this.venue = venue;
        this.capacity = capacity;
        this.date = date;
        this.fee = fee;
        this.earlyBirdEnabled = earlyBirdEnabled;
        this.earlyBirdEnd = earlyBirdEnd;
        this.promoEnabled = promoEnabled;
        this.promoCode = promoCode;
        this.promoDiscount = promoDiscount;
    }
    
    // Constructor without eventId (auto-generate)
    public EventData(String name, String organiser, String eventType, String venue, int capacity, Date date, double fee, boolean earlyBirdEnabled, Date earlyBirdEnd, boolean promoEnabled, String promoCode, double promoDiscount) {
        this(EventCSVManager.generateNextEventId(), name, organiser, eventType, venue, capacity, date, fee, earlyBirdEnabled, earlyBirdEnd, promoEnabled, promoCode, promoDiscount);
    }
    // Backward compatible constructor
    public EventData(String name, String organiser, String eventType, String venue, int capacity, Date date, double fee) {
        this(name, organiser, eventType, venue, capacity, date, fee, false, null, false, null, 0);
    }
    public String getEventId() { return eventId; }
    public String getName() { return name; }
    public String getOrganiser() { return organiser; }
    public String getEventType() { return eventType; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public Date getDate() { return date; }
    public double getFee() { return fee; }
    // New getters
    public boolean isEarlyBirdEnabled() { return earlyBirdEnabled; }
    public Date getEarlyBirdEnd() { return earlyBirdEnd; }
    public boolean isPromoEnabled() { return promoEnabled; }
    public String getPromoCode() { return promoCode; }
    public double getPromoDiscount() { return promoDiscount; }
}