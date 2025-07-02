// FeeBreakdownPage.java
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

// Add FeeBreakdownPage class
class FeeBreakdownPage extends JFrame {
    public FeeBreakdownPage(EventData event, int pax, boolean catering, boolean transport, String appliedPromoCode) {
        setTitle("Fee Breakdown");
        setSize(500, 550);
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
        panel.add(makeDetailLabel("Capacity: ", String.valueOf(event.getCapacity())));
        panel.add(makeDetailLabel("Fee: ", String.format("RM %.2f", event.getFee())));
        panel.add(Box.createVerticalStrut(10));

        double baseFee = event.getFee();
        double cateringFee = catering ? 20.0 : 0.0;
        double transportFee = transport ? 10.0 : 0.0;
        double addServices = (cateringFee + transportFee);
        double totalBeforeDiscount = (baseFee + addServices) * pax;
        double groupDiscount = 0.15;
        double discount = 0;
        StringBuilder discountDetails = new StringBuilder();
        if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
            long now = System.currentTimeMillis();
            if (now <= event.getEarlyBirdEnd().getTime()) {
                discount += totalBeforeDiscount * 0.10;
                discountDetails.append(String.format("Early Bird (10%%): -RM %.2f\n", totalBeforeDiscount * 0.10));
            }
        }
        if (pax >= 5) {
            discount += totalBeforeDiscount * groupDiscount;
            discountDetails.append(String.format("Group (15%%): -RM %.2f\n", totalBeforeDiscount * groupDiscount));
        }
        if (event.isPromoEnabled() && appliedPromoCode != null && appliedPromoCode.equals(event.getPromoCode())) {
            double promoDisc = event.getPromoDiscount() / 100.0;
            discount += totalBeforeDiscount * promoDisc;
            discountDetails.append(String.format("Promo (%s, %.0f%%): -RM %.2f\n", event.getPromoCode(), event.getPromoDiscount(), totalBeforeDiscount * promoDisc));
        }
        double netPayable = totalBeforeDiscount - discount;
        StringBuilder bill = new StringBuilder();
        bill.append(String.format("Base Fee:           RM %.2f\n", baseFee * pax));
        bill.append(String.format("Additional Services: RM %.2f\n", addServices * pax));
        bill.append(String.format("Total Before Disc.:  RM %.2f\n", totalBeforeDiscount));
        bill.append(discountDetails);
        bill.append(String.format("Discount Amount:     RM %.2f\n", discount));
        bill.append(String.format("Net Payable:         RM %.2f", netPayable));

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
            JOptionPane.showMessageDialog(this, "Pay successfully, register successfully");
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