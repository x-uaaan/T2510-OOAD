// FeeBreakdownPage.java
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

// Add FeeBreakdownPage class
class FeeBreakdownPage extends JFrame {
    public FeeBreakdownPage(EventData event, int pax, boolean catering, boolean transport, String appliedVoucherCode) {
        setTitle("Fee Breakdown");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        // Event details
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
        panel.add(makeDetailLabel("Event Title: ", event.getName()));
        panel.add(makeDetailLabel("Organiser: ", event.getOrganiser()));
        panel.add(makeDetailLabel("Event Type: ", event.getEventType()));
        panel.add(makeDetailLabel("Venue: ", event.getVenue()));
        panel.add(makeDetailLabel("Date: ", sdf.format(event.getDate())));
        panel.add(makeDetailLabel("Time: ", stf.format(event.getDate())));
        panel.add(makeDetailLabel("Fee: ", String.format("RM %.2f", event.getFee())));
        panel.add(Box.createVerticalStrut(10));


        // Use centralized fee calculation to ensure consistency with PaymentPage
        FeeCalculator.FeeBreakdown breakdown = FeeCalculator.calculateFees(
            event, pax, catering, transport, appliedVoucherCode);
        
        final double totalDiscounts = breakdown.totalDiscount;
        final double netPayable = breakdown.netPay;
        
        // Build the fee breakdown in the requested format
        StringBuilder bill = new StringBuilder();
        
        // Base Fee
        bill.append(String.format("Base Fee: RM %.2f\n", breakdown.baseFee));
        
        // Additional Services (only show if there are any)
        if (breakdown.cateringCost > 0 || breakdown.transportCost > 0) {
            bill.append("Additional Services:\n");
            if (breakdown.cateringCost > 0) {
                bill.append(String.format("• Catering: RM %.2f\n", breakdown.cateringCost));
            }
            if (breakdown.transportCost > 0) {
                bill.append(String.format("• Transport: RM %.2f\n", breakdown.transportCost));
            }
        }
        
        // Subtotal (base + additional services)
        bill.append(String.format("Subtotal: RM %.2f\n", breakdown.subtotal));
        
        // Show voucher details if any were applied
        if (breakdown.appliedVoucherDetails != null && breakdown.appliedVoucherDetails.trim().length() > 0) {
            bill.append("\nVoucher Applied:\n");
            bill.append(breakdown.appliedVoucherDetails);
        }
        
        // Discount Amount (only show if there are discounts)
        if (totalDiscounts > 0) {
            bill.append(String.format("Discount Amount: RM %.2f\n", totalDiscounts));
            if (breakdown.earlyBirdDiscount > 0) {
                bill.append(String.format("    Early Bird: -RM %.2f\n", breakdown.earlyBirdDiscount));
            }
            if (breakdown.groupOrderDiscount > 0) {
                bill.append(String.format("    Group Order: -RM %.2f\n", breakdown.groupOrderDiscount));
            }
        }
        
        // Service Tax (6% of after-discount amount)
        bill.append(String.format("\nService Tax (6%%): RM %.2f\n", breakdown.serviceTax));
        
        // Net Pay
        bill.append(String.format("Net Pay: RM %.2f", netPayable));

        JPanel billPanel = new JPanel();
        billPanel.setLayout(new BoxLayout(billPanel, BoxLayout.Y_AXIS));
        billPanel.setBackground(Color.WHITE);
        billPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        JLabel billLabel = new JLabel("<html>" + bill.toString().replace("\n", "<br>") + "</html>");
        billLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billLabel.setAlignmentX(0.0f);
        billPanel.add(billLabel);
        panel.add(billPanel);
        panel.add(Box.createVerticalStrut(20));
        JButton payButton = new JButton("Pay");
        panel.add(payButton);
        add(panel, BorderLayout.CENTER);

        payButton.addActionListener(e -> {
            // Process registration and save to registrations.csv only
            try {
                // Get current logged in user from login system
                UserData user = LoginRegisterWindow.getLoggedInUser();
                String userId = user != null ? user.getUserId() : "";
                String userName = user != null ? user.getUserName() : "";
                String userType = user != null ? user.getUserType() : "";
                
                // Generate registration ID
                String registrationId = RegistrationCSVManager.generateNextRegistrationId();
                
                // Create registration record
                RegistrationData registration = new RegistrationData(
                    registrationId, event.getEventId(), userId, event.getName(), userName,
                    pax, catering, transport, appliedVoucherCode, netPayable
                );
                registration.setBaseAmount(breakdown.subtotal);
                registration.setDiscountAmount(totalDiscounts);
                registration.setStatus("Confirmed");
                RegistrationCSVManager.addRegistrationToCSV(registration);
                
                // Deactivate welcome voucher after first registration
                VoucherCSVManager.deactivateWelcomeVoucherForUser(userId);
                
                // Increment voucher usage if vouchers were used
                if (appliedVoucherCode != null && !appliedVoucherCode.trim().isEmpty()) {
                    String[] voucherCodes = appliedVoucherCode.split(",");
                    for (String code : voucherCodes) {
                        code = code.trim();
                        if (!code.isEmpty()) {
                            VoucherCSVManager.incrementVoucherUsage(code);
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    String.format("Registration successful!\n\nRegistration ID: %s", 
                    registrationId));
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error processing registration: " + ex.getMessage(), 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Close all open registration/payment windows and return to event list
            Window[] windows = Window.getWindows();
            for (Window w : windows) {
                if (w instanceof FeeBreakdownPage || w instanceof PaymentPage || w instanceof EventDetailsWindow) {
                    w.dispose();
                }
            }
        });
    }

    private JPanel makeDetailLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(l);
        panel.add(v);
        return panel;
    }
}