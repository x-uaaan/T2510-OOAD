// PaymentPage.java
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

// Add PaymentPage class
class PaymentPage extends JFrame {
    private JCheckBox cateringBox;
    private JCheckBox transportBox;
    private JTextField paxField;
    private JButton confirmPaxButton;
    private JLabel billLabel;
    private EventData event;
    private double cateringFee = 20.0;
    private double transportFee = 10.0;
    private int confirmedPax = 1;
    private VoucherData appliedVoucher = null;
    private boolean appliedEventPromoCode = false; // Track if event promo code is applied
    private JLabel voucherLabel;
    private JButton selectVoucherButton;
    private String currentUserType = "STUDENT"; // This should come from logged in user

    public PaymentPage(EventData event) {
        this.event = event;
        setTitle("Event Registration & Payment");
        setSize(500, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(Color.WHITE);

        // Event Info Section
        JLabel eventLabel = new JLabel("Event: " + event.getName());
        eventLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(eventLabel);
        formPanel.add(Box.createVerticalStrut(15));

        // Participants Section
        JPanel paxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paxPanel.setBackground(Color.WHITE);
        paxPanel.add(new JLabel("Number of Participants: "));
        paxField = new JTextField("1", 3);
        confirmPaxButton = new JButton("Update");
        paxPanel.add(paxField);
        paxPanel.add(confirmPaxButton);
        formPanel.add(paxPanel);
        formPanel.add(Box.createVerticalStrut(10));

        // Additional Services Section
        JLabel servicesLabel = new JLabel("Additional Services:");
        servicesLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(servicesLabel);
        
        cateringBox = new JCheckBox("Catering (+RM 20.00 per person)");
        cateringBox.setBackground(Color.WHITE);
        formPanel.add(cateringBox);
        
        transportBox = new JCheckBox("Transportation (+RM 10.00 per person)");
        transportBox.setBackground(Color.WHITE);
        formPanel.add(transportBox);
        formPanel.add(Box.createVerticalStrut(15));

        // Voucher Section
        JLabel voucherSectionLabel = new JLabel("Voucher/Discount:");
        voucherSectionLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(voucherSectionLabel);
        
        JPanel voucherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        voucherPanel.setBackground(Color.WHITE);
        
        selectVoucherButton = new JButton("Select Voucher");
        voucherLabel = new JLabel("No voucher selected");
        voucherLabel.setForeground(Color.GRAY);
        
        JButton clearVoucherButton = new JButton("Clear");
        
        voucherPanel.add(selectVoucherButton);
        voucherPanel.add(clearVoucherButton);
        formPanel.add(voucherPanel);
        formPanel.add(voucherLabel);
        formPanel.add(Box.createVerticalStrut(15));

        // Fee Breakdown Section
        JLabel breakdownLabel = new JLabel("Fee Breakdown:");
        breakdownLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(breakdownLabel);
        
        billLabel = new JLabel();
        billLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        billLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(billLabel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Payment Button
        JButton payButton = new JButton("Proceed to Pay");
        payButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        payButton.setBackground(new Color(0, 123, 255));
        payButton.setForeground(Color.WHITE);
        formPanel.add(payButton);
        
        add(formPanel, BorderLayout.CENTER);

        // Event Handlers
        cateringBox.addActionListener(e -> updateBill());
        transportBox.addActionListener(e -> updateBill());
        
        confirmPaxButton.addActionListener(e -> {
            try {
                int newPax = Math.max(1, Integer.parseInt(paxField.getText().trim()));
                confirmedPax = newPax;
                paxField.setText(String.valueOf(confirmedPax));
                
                // Re-check platform vouchers when pax changes (for group order eligibility)
                checkAndApplyPlatformVouchers();
                
                updateBill();
            } catch (Exception ex) { 
                paxField.setText(String.valueOf(confirmedPax));
                JOptionPane.showMessageDialog(this, "Please enter a valid number of participants.");
            }
        });

        selectVoucherButton.addActionListener(e -> openVoucherSelection());
        clearVoucherButton.addActionListener(e -> {
            appliedVoucher = null;
            appliedEventPromoCode = false;
            // Note: Cannot clear event-based early bird discount - it's automatic
            updateVoucherDisplayText();
            updateBill();
        });

        payButton.addActionListener(e -> {
            // Build voucher code string including event promo if applied
            String voucherCode = null;
            if (appliedEventPromoCode && appliedVoucher != null) {
                // Both event promo and regular voucher applied
                voucherCode = "EVENT_PROMO:" + event.getPromoCode() + "," + appliedVoucher.getVoucherCode();
            } else if (appliedEventPromoCode) {
                // Only event promo applied
                voucherCode = "EVENT_PROMO:" + event.getPromoCode();
            } else if (appliedVoucher != null) {
                // Only regular voucher applied
                voucherCode = appliedVoucher.getVoucherCode();
            }
            
            FeeBreakdownPage breakdownPage = new FeeBreakdownPage(event, confirmedPax, 
                cateringBox.isSelected(), transportBox.isSelected(), voucherCode);
            breakdownPage.setVisible(true);
            this.dispose();
        });

        // Auto-apply early bird voucher if eligible
        checkAndApplyEarlyBird();
        
        // Auto-apply platform vouchers if eligible
        checkAndApplyPlatformVouchers();
        
        // Initial bill update
        updateBill();
    }

    private void checkAndApplyEarlyBird() {
        try {
            // Check if this event has early bird discount enabled and user is within early bird period
            if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
                long now = System.currentTimeMillis();
                if (now <= event.getEarlyBirdEnd().getTime()) {
                    // Event-based early bird is active - no need to check voucher usage
                    // since it's automatically applied for this event registration
                    System.out.println("Early bird discount applied for event: " + event.getName());
                } else {
                    System.out.println("Early bird period has ended for event: " + event.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking early bird discount: " + e.getMessage());
        }
    }
    
    private void checkAndApplyPlatformVouchers() {
        try {
            // Assume current user ID is U0001 - in real implementation this would come from login
            String currentUserId = "U0001"; // TODO: Get from login session
            
            // Get auto-applicable platform vouchers
            List<VoucherData> autoVouchers = VoucherCSVManager.getAutoApplicablePlatformVouchers(currentUserId, confirmedPax);
            
            if (!autoVouchers.isEmpty()) {
                // Apply the first available platform voucher (priority: NEW_USER > GROUP_ORDER)
                VoucherData selectedVoucher = null;
                
                // Prioritize NEW_USER voucher
                for (VoucherData voucher : autoVouchers) {
                    if ("NEW_USER".equals(voucher.getVoucherType())) {
                        selectedVoucher = voucher;
                        break;
                    }
                }
                
                // If no NEW_USER voucher, use GROUP_ORDER voucher
                if (selectedVoucher == null) {
                    for (VoucherData voucher : autoVouchers) {
                        if ("GROUP_ORDER".equals(voucher.getVoucherType())) {
                            selectedVoucher = voucher;
                            break;
                        }
                    }
                }
                
                if (selectedVoucher != null) {
                    appliedVoucher = selectedVoucher;
                    updateVoucherDisplayText();
                    System.out.println("Auto-applied platform voucher: " + selectedVoucher.getVoucherCode());
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking platform vouchers: " + e.getMessage());
        }
    }
    
    // Check if user has already used this event's promo code
    private boolean hasUserUsedEventPromoCode(String userId, String eventId, String promoCode) {
        if (promoCode == null || promoCode.trim().isEmpty()) {
            return false;
        }
        
        try {
            List<RegistrationData> registrations = RegistrationCSVManager.loadRegistrationsFromCSV();
            for (RegistrationData registration : registrations) {
                if (userId.equals(registration.getUserId()) && 
                    eventId.equals(registration.getEventId()) &&
                    registration.getPromoCodeUsed() != null &&
                    registration.getPromoCodeUsed().contains("EVENT_PROMO:" + promoCode)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking event promo code usage: " + e.getMessage());
        }
        
        return false;
    }

    private void openVoucherSelection() {
        // Create a dialog to show applicable vouchers
        JDialog voucherDialog = new JDialog(this, "Select Voucher", true);
        voucherDialog.setSize(600, 400);
        voucherDialog.setLocationRelativeTo(this);
        voucherDialog.setLayout(new BorderLayout());

        // Get applicable vouchers (exclude early bird and already used vouchers)
        double currentAmount = calculateCurrentTotal();
        List<VoucherData> allApplicableVouchers = VoucherCSVManager.getApplicableVouchers(
            currentAmount, confirmedPax, event.getEventId(), currentUserType);
        
        // Filter out early bird vouchers and vouchers already used by this user
        List<Object> applicableOptions = new ArrayList<>(); // Changed to Object to include event promo
        String currentUserId = "U0001"; // This should come from logged in user
        
        // Add event promo code if available and not used
        if (event.isPromoEnabled() && event.getPromoCode() != null && 
            !hasUserUsedEventPromoCode(currentUserId, event.getEventId(), event.getPromoCode())) {
            applicableOptions.add("EVENT_PROMO"); // Add special marker for event promo
        }
        
        for (VoucherData voucher : allApplicableVouchers) {
            // Skip early bird vouchers - they are auto-applied
            if ("EARLY_BIRD".equals(voucher.getVoucherType())) {
                continue;
            }
            
            // Skip vouchers already used by this user
            if (VoucherCSVManager.hasUserUsedVoucher(currentUserId, voucher.getVoucherCode())) {
                continue;
            }
            
            applicableOptions.add(voucher);
        }

        // Sort ALL vouchers by save amount (highest savings first) - no priority for event vs platform vouchers
        applicableOptions.sort((option1, option2) -> {
            double save1 = 0;
            double save2 = 0;
            
            // Calculate save amount for option1
            if ("EVENT_PROMO".equals(option1)) {
                save1 = event.calculatePromoDiscount(currentAmount);
            } else if (option1 instanceof VoucherData) {
                save1 = ((VoucherData) option1).calculateDiscountAmount(currentAmount);
            }
            
            // Calculate save amount for option2
            if ("EVENT_PROMO".equals(option2)) {
                save2 = event.calculatePromoDiscount(currentAmount);
            } else if (option2 instanceof VoucherData) {
                save2 = ((VoucherData) option2).calculateDiscountAmount(currentAmount);
            }
            
            // Sort by highest savings first
            return Double.compare(save2, save1); // Descending order (highest savings first)
        });

        if (applicableOptions.isEmpty()) {
            JLabel noVouchersLabel = new JLabel("No applicable vouchers found for this registration.", JLabel.CENTER);
            noVouchersLabel.setForeground(Color.GRAY);
            voucherDialog.add(noVouchersLabel, BorderLayout.CENTER);
            
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> voucherDialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            voucherDialog.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            // Create voucher list
            DefaultListModel<Object> listModel = new DefaultListModel<>();
            for (Object option : applicableOptions) {
                listModel.addElement(option);
            }
            
            JList<Object> voucherList = new JList<>(listModel);
            voucherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            voucherList.setCellRenderer(new VoucherListCellRenderer());
            
            JScrollPane scrollPane = new JScrollPane(voucherList);
            voucherDialog.add(scrollPane, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton selectButton = new JButton("Select");
            JButton cancelButton = new JButton("Cancel");
            
            selectButton.addActionListener(e -> {
                Object selected = voucherList.getSelectedValue();
                if (selected != null) {
                    if ("EVENT_PROMO".equals(selected)) {
                        // Apply event promo code
                        appliedVoucher = null; // Clear any regular voucher
                        appliedEventPromoCode = true;
                    } else if (selected instanceof VoucherData) {
                        // Apply regular voucher
                        appliedVoucher = (VoucherData) selected;
                        appliedEventPromoCode = false;
                    }
                    updateVoucherDisplayText();
                    updateBill();
                    voucherDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(voucherDialog, "Please select a voucher.");
                }
            });
            
            cancelButton.addActionListener(e -> voucherDialog.dispose());
            
            buttonPanel.add(selectButton);
            buttonPanel.add(cancelButton);
            voucherDialog.add(buttonPanel, BorderLayout.SOUTH);
        }

        voucherDialog.setVisible(true);
    }

    private void updateVoucherDisplayText() {
        StringBuilder voucherText = new StringBuilder();
        boolean hasVouchers = false;
        
        // Check for event-based early bird discount
        if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
            long now = System.currentTimeMillis();
            if (now <= event.getEarlyBirdEnd().getTime()) {
                voucherText.append("Early Bird (").append(event.getEarlyBirdDiscountDisplay()).append(")");
                hasVouchers = true;
            }
        }
        
        // Check for event promo code
        if (appliedEventPromoCode) {
            if (hasVouchers) voucherText.append(" + ");
            voucherText.append("Event Promo (").append(event.getPromoCode()).append(")");
            hasVouchers = true;
        }
        
        if (appliedVoucher != null) {
            if (hasVouchers) voucherText.append(" + ");
            voucherText.append(appliedVoucher.getVoucherCode());
            hasVouchers = true;
        }
        
        if (hasVouchers) {
            voucherLabel.setText("Applied: " + voucherText.toString());
            voucherLabel.setForeground(new Color(0, 128, 0));
        } else {
            voucherLabel.setText("No voucher selected");
            voucherLabel.setForeground(Color.GRAY);
        }
    }

    private double calculateCurrentTotal() {
        double baseFee = event.getFee() * confirmedPax;
        double additionalServices = 0;
        if (cateringBox.isSelected()) additionalServices += cateringFee * confirmedPax;
        if (transportBox.isSelected()) additionalServices += transportFee * confirmedPax;
        return baseFee + additionalServices;
    }

    private void updateBill() {
        // Build voucher code string for centralized calculation
        String voucherCode = null;
        if (appliedEventPromoCode && appliedVoucher != null) {
            // Both event promo and regular voucher applied
            voucherCode = "EVENT_PROMO:" + event.getPromoCode() + "," + appliedVoucher.getVoucherCode();
        } else if (appliedEventPromoCode) {
            // Only event promo applied
            voucherCode = "EVENT_PROMO:" + event.getPromoCode();
        } else if (appliedVoucher != null) {
            // Only regular voucher applied
            voucherCode = appliedVoucher.getVoucherCode();
        }
        
        // Use centralized fee calculation
        FeeCalculator.FeeBreakdown breakdown = FeeCalculator.calculateFees(
            event, confirmedPax, cateringBox.isSelected(), transportBox.isSelected(), voucherCode);
        
        StringBuilder bill = new StringBuilder();
        bill.append("<html>");
        
        // Base Fee (always show)
        bill.append(String.format("<b>Base Fee:</b> RM %.2f<br>", breakdown.baseFee));
        
        // Additional Services (only show if exist)
        if (breakdown.cateringCost > 0 || breakdown.transportCost > 0) {
            bill.append("<b>Additional Services:</b><br>");
            if (breakdown.cateringCost > 0) {
                bill.append(String.format("&nbsp;&nbsp;• Catering: RM %.2f<br>", breakdown.cateringCost));
            }
            if (breakdown.transportCost > 0) {
                bill.append(String.format("&nbsp;&nbsp;• Transport: RM %.2f<br>", breakdown.transportCost));
            }
            bill.append("<br>");
        }
        
        // Subtotal (always show)
        bill.append(String.format("<b>Subtotal:</b> RM %.2f<br>", breakdown.subtotal));
        
        // Voucher Applied (only show if there are vouchers)
        if (breakdown.appliedVoucherDetails != null && !breakdown.appliedVoucherDetails.trim().isEmpty()) {
            bill.append("<br><b>Voucher Applied:</b><br>");
            String[] lines = breakdown.appliedVoucherDetails.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    bill.append("&nbsp;&nbsp;").append(line).append("<br>");
                }
            }
            bill.append("<br>");
        }
        
        // Service Tax (calculated on after-discount amount)
        bill.append(String.format("<b>Service Tax (6%%):</b> RM %.2f<br>", breakdown.serviceTax));
        
        // Net Pay (always show)
        bill.append(String.format("<b>Net Pay: RM %.2f</b>", breakdown.netPay));
        bill.append("</html>");
        
        billLabel.setText(bill.toString());
        
        // Update voucher display text
        updateVoucherDisplayText();
    }

    // Custom cell renderer for voucher list
    private class VoucherListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            // Add more vertical spacing between vouchers
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            
            if ("EVENT_PROMO".equals(value)) {
                // Display event promo code
                double currentTotal = calculateCurrentTotal();
                double discount = event.calculatePromoDiscount(currentTotal);
                
                setText(String.format("<html><div style='padding: 5px;'>" +
                       "<b>%s</b>&nbsp;&nbsp;-&nbsp;&nbsp;%s&nbsp;&nbsp;-&nbsp;&nbsp;Save RM%.2f" +
                       "</div></html>",
                       event.getPromoCode(),
                       event.getPromoDiscountDisplay(),
                       discount));
            } else if (value instanceof VoucherData) {
                VoucherData voucher = (VoucherData) value;
                double currentTotal = calculateCurrentTotal();
                double discount = voucher.calculateDiscountAmount(currentTotal);
                
                setText(String.format("<html><div style='padding: 5px;'>" +
                       "<b>%s</b>&nbsp;&nbsp;-&nbsp;&nbsp;%s&nbsp;&nbsp;-&nbsp;&nbsp;Save RM%.2f" +
                       "</div></html>",
                       voucher.getVoucherCode(),
                       voucher.getDiscountDisplay(),
                       discount));
            }
            
            return this;
        }
    }
}