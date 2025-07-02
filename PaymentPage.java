// PaymentPage.java
import java.awt.*;
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
    private double groupDiscount = 0.15; // 15%
    private int confirmedPax = 1;
    private JTextField promoCodeInputField;
    private String appliedPromoCode = null;

    public PaymentPage(EventData event) {
        this.event = event;
        setTitle("Event Registration & Payment");
        setSize(500, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Base Registration Fee: RM " + String.format("%.2f", event.getFee())));
        formPanel.add(Box.createVerticalStrut(10));
        cateringBox = new JCheckBox("Catering (+RM 20.00)");
        cateringBox.setBackground(Color.WHITE);
        formPanel.add(cateringBox);
        transportBox = new JCheckBox("Transportation (+RM 10.00)");
        transportBox.setBackground(Color.WHITE);
        formPanel.add(transportBox);
        formPanel.add(Box.createVerticalStrut(10));
        JPanel paxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paxPanel.setBackground(Color.WHITE);
        paxPanel.add(new JLabel("Pax: "));
        paxField = new JTextField("1", 3);
        confirmPaxButton = new JButton("Confirm");
        paxPanel.add(paxField);
        paxPanel.add(confirmPaxButton);
        formPanel.add(paxPanel);
        formPanel.add(Box.createVerticalStrut(10));
        if (event.isPromoEnabled()) {
            JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            promoPanel.setBackground(Color.WHITE);
            promoPanel.add(new JLabel("Promo Code: "));
            promoCodeInputField = new JTextField(8);
            promoPanel.add(promoCodeInputField);
            JButton applyPromoBtn = new JButton("Apply");
            promoPanel.add(applyPromoBtn);
            formPanel.add(promoPanel);
            applyPromoBtn.addActionListener(e -> {
                String input = promoCodeInputField.getText().trim();
                if (input.equals(event.getPromoCode())) {
                    appliedPromoCode = input;
                    JOptionPane.showMessageDialog(this, "Promo code applied!");
                } else {
                    appliedPromoCode = null;
                    JOptionPane.showMessageDialog(this, "Invalid promo code.");
                }
                updateBill();
            });
        }
        billLabel = new JLabel();
        billLabel.setFont(new Font("Monospaced", Font.PLAIN, 13));
        updateBill();
        formPanel.add(billLabel);
        formPanel.add(Box.createVerticalStrut(10));
        JButton payButton = new JButton("Proceed to Pay");
        formPanel.add(payButton);
        add(formPanel, BorderLayout.CENTER);

        cateringBox.addActionListener(e -> updateBill());
        transportBox.addActionListener(e -> updateBill());
        confirmPaxButton.addActionListener(e -> {
            try {
                confirmedPax = Math.max(1, Integer.parseInt(paxField.getText().trim()));
            } catch (Exception ex) { confirmedPax = 1; }
            updateBill();
        });

        payButton.addActionListener(e -> {
            FeeBreakdownPage breakdownPage = new FeeBreakdownPage(event, confirmedPax, cateringBox.isSelected(), transportBox.isSelected(), appliedPromoCode);
            breakdownPage.setVisible(true);
            this.dispose();
        });
    }

    private void updateBill() {
        double baseFee = event.getFee();
        double addServices = 0;
        if (cateringBox.isSelected()) addServices += cateringFee;
        if (transportBox.isSelected()) addServices += transportFee;
        int groupSize = confirmedPax;
        double totalBeforeDiscount = (baseFee + addServices) * groupSize;
        double discount = 0;
        StringBuilder discountDetails = new StringBuilder();
        if (event.isEarlyBirdEnabled() && event.getEarlyBirdEnd() != null) {
            long now = System.currentTimeMillis();
            if (now <= event.getEarlyBirdEnd().getTime()) {
                discount += totalBeforeDiscount * 0.10;
                discountDetails.append(String.format("Early Bird (10%%): -RM %.2f\n", totalBeforeDiscount * 0.10));
            }
        }
        if (groupSize >= 5) {
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
        bill.append(String.format("Base Fee:           RM %.2f\n", baseFee * groupSize));
        bill.append(String.format("Additional Services: RM %.2f\n", addServices * groupSize));
        bill.append(String.format("Total Before Disc.:  RM %.2f\n", totalBeforeDiscount));
        bill.append(discountDetails);
        bill.append(String.format("Discount Amount:     RM %.2f\n", discount));
        bill.append(String.format("Net Payable:         RM %.2f", netPayable));
        billLabel.setText("<html>" + bill.toString().replace("\n", "<br>") + "</html>");
    }
}