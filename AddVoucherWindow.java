import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

public class AddVoucherWindow extends JDialog {
    private EventManagementApp parentApp;
    private JTextField voucherCodeField;
    private JComboBox<String> voucherTypeCombo;
    private JComboBox<String> discountTypeCombo;
    private JTextField discountValueField;
    private JTextArea descriptionArea;
    private JTextField validFromField;
    private JTextField validUntilField;
    private JTextField usageLimitField;
    private JCheckBox isActiveCheckbox;
    private JLabel minSpendLabel;
    private JPanel minSpendPanel;
    private JCheckBox minSpendRestrictionCheckbox;
    private JCheckBox groupOrderRestrictionCheckbox;
    private JTextField quantityField;
    private JTextField minimumAmountField;
    private JTextField minimumPaxField;
    private JComboBox<String> userTypeCombo;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public AddVoucherWindow(EventManagementApp parent) {
        this.parentApp = parent;
        initializeWindow();
        createComponents();
        layoutComponents();
        attachListeners();
    }

    private void initializeWindow() {
        setTitle("Add Platform Voucher - Admin Only");
        setSize(600, 700);
        setLocationRelativeTo(parentApp);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        voucherCodeField = new JTextField(20);
        
        voucherTypeCombo = new JComboBox<>(new String[]{
            "PROMO_CODE", "EARLY_BIRD", "GROUP", "MEMBER", "PLATFORM_SPECIAL"
        });
        
        discountTypeCombo = new JComboBox<>(new String[]{"PERCENTAGE", "FIXED_AMOUNT"});
        discountValueField = new JTextField(10);
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        String today = dateFormat.format(new Date());
        validFromField = new JTextField(today, 15);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        String defaultExpiry = dateFormat.format(cal.getTime());
        validUntilField = new JTextField(defaultExpiry, 15);
        
        usageLimitField = new JTextField("1", 10);
        
        isActiveCheckbox = new JCheckBox("Active", true);
        
        // Restriction checkboxes
        minSpendRestrictionCheckbox = new JCheckBox("Minimum Spend", false);
        groupOrderRestrictionCheckbox = new JCheckBox("Group Order (Min Participants)", false);
        // Quantity (usage limit)
        quantityField = new JTextField("-1", 10);
        quantityField.setToolTipText("Total number of vouchers available. Only the first N users can use this voucher.");
        minimumAmountField = new JTextField("0.00", 10);
        minimumPaxField = new JTextField("3", 10);
        userTypeCombo = new JComboBox<>(new String[]{
            "ALL", "STUDENT", "STAFF", "EXTERNAL"
        });
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Create Platform Voucher");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Voucher Code*:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(voucherCodeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Voucher Type*:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(voucherTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Discount Type*:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(discountTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Discount Value*:"), gbc);
        gbc.gridx = 1;
        JPanel discountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        discountPanel.add(discountValueField);
        JLabel discountHint = new JLabel("(% for percentage, RM for fixed)");
        discountHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        discountHint.setForeground(Color.GRAY);
        discountPanel.add(discountHint);
        mainPanel.add(discountPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(250, 60));
        mainPanel.add(descScroll, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Valid From*:"), gbc);
        gbc.gridx = 1;
        JPanel fromPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fromPanel.add(validFromField);
        JLabel dateHint1 = new JLabel("(yyyy-mm-dd)");
        dateHint1.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dateHint1.setForeground(Color.GRAY);
        fromPanel.add(dateHint1);
        mainPanel.add(fromPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Valid Until*:"), gbc);
        gbc.gridx = 1;
        JPanel untilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        untilPanel.add(validUntilField);
        JLabel dateHint2 = new JLabel("(yyyy-mm-dd)");
        dateHint2.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dateHint2.setForeground(Color.GRAY);
        untilPanel.add(dateHint2);
        mainPanel.add(untilPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Voucher Quantity*:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(quantityField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 9;
        mainPanel.add(new JLabel("Usage Limit (per user):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(usageLimitField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 10;
        mainPanel.add(new JLabel("User Restriction:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(userTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.gridwidth = 2;
        JLabel restrictionLabel = new JLabel("Restriction(s):");
        restrictionLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainPanel.add(restrictionLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 12;
        mainPanel.add(minSpendRestrictionCheckbox, gbc);
        gbc.gridx = 1;
        mainPanel.add(groupOrderRestrictionCheckbox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 13;
        minSpendLabel = new JLabel("Min Spend (RM):");
        minSpendLabel.setToolTipText("Minimum spend = base fee + additional services");
        minSpendLabel.setVisible(false);
        mainPanel.add(minSpendLabel, gbc);
        gbc.gridx = 1;
        minSpendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        minSpendPanel.add(minimumAmountField);
        if (minSpendPanel.getComponentCount() < 2) {
            JLabel minSpendHint = new JLabel("(base fee + add services must be >= this amount)");
            minSpendHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
            minSpendHint.setForeground(Color.GRAY);
            minSpendPanel.add(minSpendHint);
        }
        minSpendPanel.setVisible(false);
        mainPanel.add(minSpendPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 14;
        JLabel minPaxLabel = new JLabel("Min Participants:");
        minPaxLabel.setToolTipText("Minimum number of people required for the voucher to be valid (e.g., for group discounts).");
        minPaxLabel.setVisible(false);
        mainPanel.add(minPaxLabel, gbc);
        gbc.gridx = 1;
        minimumPaxField.setVisible(false);
        mainPanel.add(minimumPaxField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 15;
        gbc.gridwidth = 2;
        mainPanel.add(isActiveCheckbox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Voucher");
        createButton.setBackground(new Color(0, 123, 255));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 16;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);
        
        createButton.addActionListener(this::createVoucher);
        cancelButton.addActionListener(e -> dispose());
        
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
    }

    private void attachListeners() {
        voucherTypeCombo.addActionListener(e -> suggestVoucherCode());
        
        // Show/hide minimum spend field
        minSpendRestrictionCheckbox.addActionListener(e -> {
            boolean enabled = minSpendRestrictionCheckbox.isSelected();
            minSpendLabel.setVisible(enabled);
            minSpendPanel.setVisible(enabled);
            if (!enabled) minimumAmountField.setText("0.00");
            getContentPane().revalidate();
            getContentPane().repaint();
        });
        // Show/hide min participants field
        groupOrderRestrictionCheckbox.addActionListener(e -> {
            boolean enabled = groupOrderRestrictionCheckbox.isSelected();
            minimumPaxField.setVisible(enabled);
            // Find the label and show/hide
            for (Component c : ((Container)minimumPaxField.getParent()).getComponents()) {
                if (c instanceof JLabel && ((JLabel)c).getText().contains("Min Participants")) {
                    c.setVisible(enabled);
                }
            }
            if (!enabled) minimumPaxField.setText("3");
            getContentPane().revalidate();
            getContentPane().repaint();
        });
    }
    
    private void suggestVoucherCode() {
        String type = (String) voucherTypeCombo.getSelectedItem();
        String suggestion = "";
        
        switch (type) {
            case "PROMO_CODE":
                suggestion = "PROMO" + System.currentTimeMillis() % 10000;
                break;
            case "EARLY_BIRD":
                suggestion = "EARLY" + System.currentTimeMillis() % 10000;
                break;
            case "GROUP":
                suggestion = "GROUP" + System.currentTimeMillis() % 10000;
                break;
            case "MEMBER":
                suggestion = "MEMBER" + System.currentTimeMillis() % 10000;
                break;
            case "PLATFORM_SPECIAL":
                suggestion = "SPECIAL" + System.currentTimeMillis() % 10000;
                break;
        }
        
        if (voucherCodeField.getText().trim().isEmpty()) {
            voucherCodeField.setText(suggestion);
        }
    }

    private void createVoucher(ActionEvent e) {
        try {
            if (!validateInputs()) {
                return;
            }
            
            String voucherId = VoucherCSVManager.generateNextVoucherId();
            String voucherCode = voucherCodeField.getText().trim().toUpperCase();
            String voucherType = (String) voucherTypeCombo.getSelectedItem();
            String discountType = (String) discountTypeCombo.getSelectedItem();
            double discountValue = Double.parseDouble(discountValueField.getText().trim());
            String description = descriptionArea.getText().trim();
            Date validFrom = dateFormat.parse(validFromField.getText().trim());
            Date validUntil = dateFormat.parse(validUntilField.getText().trim());
            int usageLimit = Integer.parseInt(quantityField.getText().trim());
            double minimumAmount = minSpendRestrictionCheckbox.isSelected() ? Double.parseDouble(minimumAmountField.getText().trim()) : 0.0;
            int minimumPax = groupOrderRestrictionCheckbox.isSelected() ? Integer.parseInt(minimumPaxField.getText().trim()) : 3;
            String userTypeEligible = (String) userTypeCombo.getSelectedItem();
            boolean isActive = isActiveCheckbox.isSelected();
            
            if (VoucherCSVManager.findVoucherByCode(voucherCode) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Voucher code '" + voucherCode + "' already exists. Please use a different code.",
                    "Duplicate Voucher Code", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            VoucherData newVoucher = new VoucherData(
                voucherId, voucherCode, voucherType, discountType, discountValue,
                description, validFrom, validUntil, usageLimit, 0,
                minimumAmount, minimumPax, "ALL", userTypeEligible,
                isActive, "ADMIN", new Date()
            );
            
            VoucherCSVManager.addVoucherToCSV(newVoucher);
            
            JOptionPane.showMessageDialog(this,
                "Platform voucher created successfully!\n" +
                "Code: " + voucherCode + "\n" +
                "Discount: " + newVoucher.getDiscountDisplay(),
                "Voucher Created", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid numbers for discount value, quantity, minimum amount, and minimum participants.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter valid dates in yyyy-mm-dd format.",
                "Invalid Date Format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating voucher: " + ex.getMessage(),
                "Creation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInputs() {
        if (voucherCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Voucher code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            voucherCodeField.requestFocus();
            return false;
        }
        
        try {
            double value = Double.parseDouble(discountValueField.getText().trim());
            String discountType = (String) discountTypeCombo.getSelectedItem();
            
            if (value <= 0) {
                JOptionPane.showMessageDialog(this, "Discount value must be greater than 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                discountValueField.requestFocus();
                return false;
            }
            
            if ("PERCENTAGE".equals(discountType) && value > 100) {
                JOptionPane.showMessageDialog(this, "Percentage discount cannot exceed 100%.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                discountValueField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid discount value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            discountValueField.requestFocus();
            return false;
        }
        
        try {
            Date validFrom = dateFormat.parse(validFromField.getText().trim());
            Date validUntil = dateFormat.parse(validUntilField.getText().trim());
            
            if (validUntil.before(validFrom)) {
                JOptionPane.showMessageDialog(this, "Valid until date must be after valid from date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                validUntilField.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates in yyyy-mm-dd format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < -1) {
                JOptionPane.showMessageDialog(this, "Quantity must be -1 (unlimited) or a positive integer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            quantityField.requestFocus();
            return false;
        }
        
        if (minSpendRestrictionCheckbox.isSelected()) {
            try {
                double amount = Double.parseDouble(minimumAmountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Minimum amount must be greater than 0 when enabled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    minimumAmountField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid minimum amount.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                minimumAmountField.requestFocus();
                return false;
            }
        }
        
        if (groupOrderRestrictionCheckbox.isSelected()) {
            try {
                int pax = Integer.parseInt(minimumPaxField.getText().trim());
                if (pax < 3) {
                    JOptionPane.showMessageDialog(this, "Minimum participants must be at least 3 for group order.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    minimumPaxField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid minimum participant count.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                minimumPaxField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
} 