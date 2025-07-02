// UpdateEventWindow.java
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

// Add UpdateEventWindow class
class UpdateEventWindow extends CreateEventWindow {
    private EventData originalEvent;
    private JFrame parentWindow;
    
    public UpdateEventWindow(EventData event, JFrame parentWindow) {
        super(null); // We'll handle addEvent manually
        this.originalEvent = event;
        this.parentWindow = parentWindow;
        setTitle("Update Event - " + event.getName());
        
        // Pre-fill basic fields
        eventNameField.setText(event.getName());
        organiserBox.setSelectedItem(event.getOrganiser());
        descriptionArea.setText(""); // No description stored in EventData
        dateSpinner.setValue(event.getDate());
        hourBox.setSelectedItem(new SimpleDateFormat("HH").format(event.getDate()));
        minuteBox.setSelectedItem(new SimpleDateFormat("mm").format(event.getDate()));
        eventTypeBox.setSelectedItem(event.getEventType());
        capacitySpinner.setValue(event.getCapacity());
        
        // Make fee field editable for updates
        feeField.setEditable(true);
        feeField.setText(String.format("%.2f", event.getFee()));
        
        // Parse and set venue information
        parseAndSetVenue(event.getVenue());
        
        // Pre-fill early bird settings
        if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
            earlyBirdCheckBox.setSelected(true);
            earlyBirdEndDateSpinner.setValue(event.getEarlyBirdEnd());
            earlyBirdHourBox.setSelectedItem(new SimpleDateFormat("HH").format(event.getEarlyBirdEnd()));
            earlyBirdMinuteBox.setSelectedItem(new SimpleDateFormat("mm").format(event.getEarlyBirdEnd()));
            // Trigger the action listener to show the early bird panel
            earlyBirdCheckBox.getActionListeners()[0].actionPerformed(null);
        }
        
        // Pre-fill promo code settings
        if (event.isPromoEnabled() && event.getPromoCode() != null) {
            promoCodeCheckBox.setSelected(true);
            promoCodeField.setText(event.getPromoCode());
            promoDiscountField.setText(String.format("%.0f", event.getPromoDiscount()));
            // Trigger the action listener to show the promo panel
            promoCodeCheckBox.getActionListeners()[0].actionPerformed(null);
        }
        
        // Update button text and action
        createButton.setText("Update Event");
        for (ActionListener al : createButton.getActionListeners()) {
            createButton.removeActionListener(al);
        }
        createButton.addActionListener(e -> handleUpdateEvent());
    }
    
    private void parseAndSetVenue(String fullVenue) {
        if (fullVenue == null || fullVenue.isEmpty()) {
            return;
        }
        
        // Handle complex venue strings like "Venue: Faculty | FCI | CQAR1001"
        if (fullVenue.startsWith("Venue: Faculty |")) {
            venueBox.setSelectedItem("Faculty");
            // Trigger venue change to show faculty dropdown
            venueBox.getActionListeners()[0].actionPerformed(null);
            
            String[] parts = fullVenue.split(" \\| ");
            if (parts.length >= 2) {
                String faculty = parts[1].trim();
                facultyBox.setSelectedItem(faculty);
                // Trigger faculty change to show class dropdown
                facultyBox.getActionListeners()[0].actionPerformed(null);
                
                if (parts.length >= 3) {
                    String classRoom = parts[2].trim();
                    classBox.setSelectedItem(classRoom);
                }
            }
        } else if (fullVenue.startsWith("Venue: Lecture Hall |")) {
            venueBox.setSelectedItem("Lecture Hall");
            // Trigger venue change to show lecture hall dropdown
            venueBox.getActionListeners()[0].actionPerformed(null);
            
            String[] parts = fullVenue.split(" \\| ");
            if (parts.length >= 2) {
                String lectureHall = parts[1].trim();
                lectureHallBox.setSelectedItem(lectureHall);
            }
        } else {
            // Simple venue selection
            venueBox.setSelectedItem(fullVenue);
        }
    }
    
    private void handleUpdateEvent() {
        String name = eventNameField.getText().trim();
        String organiser = (String) organiserBox.getSelectedItem();
        String desc = descriptionArea.getText().trim();
        Date date = (Date) dateSpinner.getValue();
        String hourStr = (String) hourBox.getSelectedItem();
        String minStr = (String) minuteBox.getSelectedItem();
        
        // Create event date/time
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStr));
        cal.set(Calendar.MINUTE, Integer.parseInt(minStr));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date eventDateTime = cal.getTime();
        
        // Get venue information
        String venue = (String) venueBox.getSelectedItem();
        String faculty = (facultyBox.isVisible()) ? (String) facultyBox.getSelectedItem() : null;
        String classRoom = (classBox.isVisible()) ? (String) classBox.getSelectedItem() : null;
        String lectureHall = (lectureHallBox.isVisible()) ? (String) lectureHallBox.getSelectedItem() : null;
        String eventType = (String) eventTypeBox.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        String feeStr = feeField.getText().trim();

        // Input validation
        if (name.isEmpty() || organiser == null || organiser.isEmpty() || 
            venue == null || venue.isEmpty() || eventType == null || eventType.isEmpty() || 
            feeStr.isEmpty()) {
            feedbackLabel.setText("Please fill in all required fields.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        
        if (capacity <= 0) {
            feedbackLabel.setText("Capacity must be at least 1.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        
        // Validate venue-specific fields
        if ("Faculty".equals(venue)) {
            if (faculty == null || faculty.equals("Faculty") || 
                classRoom == null || classRoom.equals("Class")) {
                feedbackLabel.setText("Please select faculty and class.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        
        if ("Lecture Hall".equals(venue)) {
            if (lectureHall == null || lectureHall.equals("Lecture Hall")) {
                feedbackLabel.setText("Please select a lecture hall.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        
        // Validate fee
        double fee = 0;
        try {
            fee = Double.parseDouble(feeStr);
            if (fee < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Invalid registration fee. Must be >= 0.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        
        // Compose full venue string
        String fullVenue = venue;
        if ("Faculty".equals(venue)) {
            fullVenue = "Venue: Faculty | " + faculty + " | " + classRoom;
        } else if ("Lecture Hall".equals(venue)) {
            fullVenue = "Venue: Lecture Hall | " + lectureHall;
        }
        
        // Handle early bird settings
        boolean earlyBirdEnabled = earlyBirdCheckBox.isSelected();
        Date earlyBirdEnd = null;
        if (earlyBirdEnabled) {
            Date ebDate = (Date) earlyBirdEndDateSpinner.getValue();
            String ebHour = (String) earlyBirdHourBox.getSelectedItem();
            String ebMin = (String) earlyBirdMinuteBox.getSelectedItem();
            Calendar ebCal = Calendar.getInstance();
            ebCal.setTime(ebDate);
            ebCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ebHour));
            ebCal.set(Calendar.MINUTE, Integer.parseInt(ebMin));
            ebCal.set(Calendar.SECOND, 0);
            ebCal.set(Calendar.MILLISECOND, 0);
            earlyBirdEnd = ebCal.getTime();
            
            // Validate early bird end date
            if (earlyBirdEnd.after(eventDateTime)) {
                feedbackLabel.setText("Early bird end date must be before event date.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        
        // Handle promo code settings
        boolean promoEnabled = promoCodeCheckBox.isSelected();
        String promoCode = null;
        double promoDiscount = 0;
        if (promoEnabled) {
            promoCode = promoCodeField.getText().trim();
            String promoDiscStr = promoDiscountField.getText().trim();
            if (promoCode.isEmpty() || promoDiscStr.isEmpty()) {
                feedbackLabel.setText("Please enter promo code and discount percentage.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            try {
                promoDiscount = Double.parseDouble(promoDiscStr);
                if (promoDiscount <= 0 || promoDiscount >= 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("Promo discount must be between 0 and 100.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        
        // Update the event using CSV operations
        EventData updatedEvent = new EventData(name, organiser, eventType, fullVenue, 
            capacity, eventDateTime, fee, earlyBirdEnabled, earlyBirdEnd, 
            promoEnabled, promoCode, promoDiscount);
        
        // Update in CSV directly
        EventCSVManager.updateEventInCSV(originalEvent, updatedEvent);
        
        // Find and refresh the main application
        Window[] windows = Window.getWindows();
        for (Window w : windows) {
            if (w instanceof EventManagementApp) {
                EventManagementApp mainAppRef = (EventManagementApp) w;
                
                // Reload events from CSV to sync memory
                mainAppRef.loadEventsFromCSV();
                mainAppRef.refreshEventList();
                
                // Show success message
                feedbackLabel.setText("Event updated successfully!");
                feedbackLabel.setForeground(Color.GREEN);
                
                // Close windows and show updated event details
                this.dispose();
                if (parentWindow != null) parentWindow.dispose();
                EventDetailsWindow detailsWindow = new EventDetailsWindow(updatedEvent, mainAppRef);
                detailsWindow.setVisible(true);
                break;
            }
        }
    }
}