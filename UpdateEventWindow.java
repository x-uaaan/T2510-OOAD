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
    private JComboBox<String> statusBox;
    
    public UpdateEventWindow(EventData event, JFrame parentWindow) {
        super(null); // We'll handle addEvent manually
        this.originalEvent = event;
        this.parentWindow = parentWindow;
        setTitle("Update Event - " + event.getName());
        
        // Add status dropdown at the top
        statusBox = new JComboBox<>(new String[]{"Ongoing", "Completed", "Cancelled"});
        statusBox.setSelectedItem(event.getStatus() != null ? event.getStatus() : "Ongoing");
        JPanel formPanel = (JPanel)getContentPane().getComponent(0);
        // Insert status above event name
        GridBagLayout layout = (GridBagLayout) formPanel.getLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(8, 8, 8, 8);
        JLabel statusLabel = new JLabel("Event Status:");
        formPanel.add(statusLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(statusBox, gbc);

        // --- Shift all other fields down by 1 row ---
        Component[] components = formPanel.getComponents();
        for (Component comp : components) {
            if (comp == statusLabel || comp == statusBox) continue; // skip status row
            GridBagConstraints c = layout.getConstraints(comp);
            c.gridy = c.gridy + 1;
            layout.setConstraints(comp, c);
        }
        formPanel.revalidate();
        formPanel.repaint();
        // --- End shift ---
        
        // --- Ensure text fields are visible and properly sized ---
        Component[] fixFields = { eventNameField, fixedCostField, variableCostField, feeField };
        for (Component comp : fixFields) {
            GridBagConstraints c = layout.getConstraints(comp);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            layout.setConstraints(comp, c);
        }
        // Description uses a JScrollPane
        for (Component comp : formPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                GridBagConstraints c = layout.getConstraints(comp);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.weightx = 1.0;
                layout.setConstraints(comp, c);
            }
        }
        formPanel.revalidate();
        formPanel.repaint();
        // --- End ensure sizing ---
        
        // Pre-fill basic fields
        eventNameField.setText(event.getName());
        organiserBox.setSelectedItem(event.getOrganiser());
        descriptionArea.setText(event.getDescription() != null ? event.getDescription() : "");
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
            
            // Pre-fill early bird discount settings
            if (event.getEarlyBirdDiscountType() != null) {
                earlyBirdDiscountTypeBox.setSelectedItem(event.getEarlyBirdDiscountType());
            }
            earlyBirdDiscountValueField.setText(String.format("%.2f", event.getEarlyBirdDiscountValue()));
            
            // Trigger the action listener to show the early bird panels
            earlyBirdCheckBox.getActionListeners()[0].actionPerformed(null);
        }
        
        // Pre-fill promo code settings
        if (event.isPromoEnabled() && event.getPromoCode() != null) {
            promoCodeCheckBox.setSelected(true);
            promoCodeField.setText(event.getPromoCode());
            
            // Pre-fill promo discount settings
            if (event.getPromoDiscountType() != null) {
                promoDiscountTypeBox.setSelectedItem(event.getPromoDiscountType());
            }
            promoDiscountField.setText(String.format("%.2f", event.getPromoDiscount()));
            
            // Trigger the action listener to show the promo panels
            promoCodeCheckBox.getActionListeners()[0].actionPerformed(null);
        }
        
        // Update button text and action
        createButton.setText("Update Event");
        for (ActionListener al : createButton.getActionListeners()) {
            createButton.removeActionListener(al);
        }
        createButton.addActionListener(e -> handleUpdateEvent());
        
        fixedCostField.setText(String.valueOf(event.getFixedCost()));
        variableCostField.setText(String.valueOf(event.getVariableCost()));

        // After shifting and resizing, ensure correct show/hide for new right-aligned panels
        try {
            JPanel earlyBirdRightPanel = null;
            JPanel earlyBirdDiscountPanel = null;
            for (Component comp : formPanel.getComponents()) {
                if (comp instanceof JPanel && ((JPanel) comp).getComponentCount() > 0) {
                    Component first = ((JPanel) comp).getComponent(0);
                    if (first instanceof JLabel && ((JLabel) first).getText().startsWith("Until:")) {
                        earlyBirdRightPanel = (JPanel) comp;
                    }
                    if (first instanceof JComboBox) {
                        earlyBirdDiscountPanel = (JPanel) comp;
                    }
                }
            }
            if (earlyBirdRightPanel != null) earlyBirdRightPanel.setVisible(earlyBirdCheckBox.isSelected());
            if (earlyBirdDiscountPanel != null) earlyBirdDiscountPanel.setVisible(earlyBirdCheckBox.isSelected());
        } catch (Exception ignore) {}
        try {
            JPanel promoRightPanel = null;
            JPanel promoDiscountPanel = null;
            for (Component comp : formPanel.getComponents()) {
                if (comp instanceof JPanel && ((JPanel) comp).getComponentCount() > 0) {
                    Component first = ((JPanel) comp).getComponent(0);
                    if (first instanceof JLabel && ((JLabel) first).getText().startsWith("Code:")) {
                        promoRightPanel = (JPanel) comp;
                    }
                    if (first instanceof JComboBox) {
                        promoDiscountPanel = (JPanel) comp;
                    }
                }
            }
            if (promoRightPanel != null) promoRightPanel.setVisible(promoCodeCheckBox.isSelected());
            if (promoDiscountPanel != null) promoDiscountPanel.setVisible(promoCodeCheckBox.isSelected());
        } catch (Exception ignore) {}
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
        String status = (String) statusBox.getSelectedItem();
        
        String fixedStr = fixedCostField.getText().trim();
        String varStr = variableCostField.getText().trim();
        double fixedCost = 0, variableCost = 0;
        try {
            fixedCost = Double.parseDouble(fixedStr);
        } catch (Exception e) { fixedCost = 0; }
        try {
            variableCost = Double.parseDouble(varStr);
        } catch (Exception e) { variableCost = 0; }
        
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
                String earlyBirdDiscountType = null;
                double earlyBirdDiscountValue = 0;
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
            
            // Early bird discount validation
            earlyBirdDiscountType = (String) earlyBirdDiscountTypeBox.getSelectedItem();
            String discountValueStr = earlyBirdDiscountValueField.getText().trim();
            if (discountValueStr.isEmpty()) {
                feedbackLabel.setText("Please enter early bird discount value.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            try {
                earlyBirdDiscountValue = Double.parseDouble(discountValueStr);
                if (earlyBirdDiscountValue <= 0) throw new NumberFormatException();
                if ("PERCENTAGE".equals(earlyBirdDiscountType) && earlyBirdDiscountValue >= 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                String message = "PERCENTAGE".equals(earlyBirdDiscountType) ? 
                    "Early bird discount percentage must be between 0 and 100." : 
                    "Early bird discount amount must be greater than 0.";
                feedbackLabel.setText(message);
                feedbackLabel.setForeground(Color.RED);
                return;
            }
                }

        // Handle promo code settings
                boolean promoEnabled = promoCodeCheckBox.isSelected();
                String promoCode = null;
                String promoDiscountType = null;
                double promoDiscount = 0;
                if (promoEnabled) {
                    promoCode = promoCodeField.getText().trim();
                    promoDiscountType = (String) promoDiscountTypeBox.getSelectedItem();
                    String promoDiscStr = promoDiscountField.getText().trim();
            if (promoCode.isEmpty() || promoDiscStr.isEmpty()) {
                feedbackLabel.setText("Please enter promo code and discount value.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
                    try {
                        promoDiscount = Double.parseDouble(promoDiscStr);
                        if (promoDiscount <= 0) throw new NumberFormatException();
                        if ("PERCENTAGE".equals(promoDiscountType) && promoDiscount >= 100) {
                            throw new NumberFormatException();
                        }
            } catch (NumberFormatException ex) {
                String message = "PERCENTAGE".equals(promoDiscountType) ? 
                    "Promo discount percentage must be between 0 and 100." : 
                    "Promo discount amount must be greater than 0.";
                feedbackLabel.setText(message);
                feedbackLabel.setForeground(Color.RED);
                return;
                    }
                }
                
        // Set authorId and authorName from logged-in user
        UserData author = LoginRegisterWindow.getLoggedInUser();
        String authorId = author != null ? author.getUserId() : null;
        String authorName = author != null ? author.getUserName() : null;
        // Update the event using CSV operations
        EventData updatedEvent = new EventData(name, organiser, eventType, fullVenue, 
            capacity, eventDateTime, fee, desc, fixedCost, variableCost, earlyBirdEnabled, earlyBirdEnd, 
            earlyBirdDiscountType, earlyBirdDiscountValue, promoEnabled, promoCode, promoDiscountType, promoDiscount, status, authorId, authorName);
        
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
                String userType = "Student"; // Assuming a default userType
                EventDetailsWindow detailsWindow = new EventDetailsWindow(updatedEvent, mainAppRef, userType);
                detailsWindow.setVisible(true);
                break;
            }
        }
    }
}