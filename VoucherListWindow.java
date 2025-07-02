import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

public class VoucherListWindow extends JFrame {
    private JTable voucherTable;
    private VoucherTableModel tableModel;
    private JLabel statusLabel;
    private String currentUserType = "STUDENT"; // This should come from logged in user
    private EventData currentEvent = null; // Set when viewing vouchers for specific event
    
    public VoucherListWindow() {
        this("STUDENT");
    }
    
    public VoucherListWindow(String userType) {
        this.currentUserType = userType;
        initializeComponents();
        setupLayout();
        setProperties();
        loadVouchers();
    }
    
    public VoucherListWindow(String userType, EventData event) {
        this.currentUserType = userType;
        this.currentEvent = event;
        initializeComponents();
        setupLayout();
        setProperties();
        loadVouchers();
        setTitle("Available Vouchers for " + event.getName());
    }
    
    private void initializeComponents() {
        // Table setup
        tableModel = new VoucherTableModel();
        voucherTable = new JTable(tableModel);
        voucherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        voucherTable.setRowHeight(25);
        
        // Column widths (removed ID column)
        voucherTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Code
        voucherTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        voucherTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Discount
        voucherTable.getColumnModel().getColumn(3).setPreferredWidth(220); // Description (wider)
        voucherTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Valid Until
        voucherTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Status
        
        // Status label
        statusLabel = new JLabel("Ready");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Center panel with table (no top search/filter panel)
        JScrollPane scrollPane = new JScrollPane(voucherTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Button panel (removed copy code button)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewDetailsButton = new JButton("View Details");
        JButton testVoucherButton = new JButton("Test Voucher");
        JButton addVoucherButton = new JButton("Add Voucher");
        
        buttonPanel.add(viewDetailsButton);
        if (currentEvent != null) {
            buttonPanel.add(testVoucherButton);
        }
        if ("Admin".equalsIgnoreCase(currentUserType)) {
            buttonPanel.add(addVoucherButton);
        }
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Status: "));
        statusPanel.add(statusLabel);
        
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add button actions (removed copy code action)
        viewDetailsButton.addActionListener(e -> viewSelectedVoucherDetails());
        if (currentEvent != null) {
            testVoucherButton.addActionListener(e -> testVoucherOnEvent());
        }
        addVoucherButton.addActionListener(e -> {
            AddVoucherWindow addVoucherWindow = new AddVoucherWindow(null);
            addVoucherWindow.setVisible(true);
        });
    }
    
    private void setProperties() {
        setTitle("Available Vouchers & Promo Codes");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void loadVouchers() {
        try {
            String currentUserId = "U0001"; // This should come from logged in user
            List<VoucherData> allVouchers = VoucherCSVManager.loadVouchersFromCSV();
            List<VoucherData> availableVouchers = new ArrayList<>();
            
            for (VoucherData voucher : allVouchers) {
                // Show only active vouchers that are applicable to current user
                if (voucher.isCurrentlyValid()) {
                    // Show if userTypeEligible is ALL or matches current user type
                    if ("ALL".equalsIgnoreCase(voucher.getUserTypeEligible()) ||
                        voucher.getUserTypeEligible().equalsIgnoreCase(currentUserType)) {
                        if (!VoucherCSVManager.hasUserUsedVoucher(currentUserId, voucher.getVoucherCode())) {
                            availableVouchers.add(voucher);
                        }
                    }
                }
            }
            
            // Sort vouchers by valid date (closest expiry first)
            availableVouchers = VoucherCSVManager.sortVouchersByValidDate(availableVouchers);
            
            tableModel.setVouchers(availableVouchers);
            statusLabel.setText(String.format("Showing %d available vouchers", availableVouchers.size()));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading vouchers: " + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    
    private void copySelectedVoucherCode() {
        int selectedRow = voucherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a voucher first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        VoucherData voucher = tableModel.getVoucherAt(selectedRow);
        
        // Copy to clipboard
        try {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(voucher.getVoucherCode()), null);
            statusLabel.setText("Voucher code '" + voucher.getVoucherCode() + "' copied to clipboard");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error copying to clipboard: " + e.getMessage(), 
                "Copy Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewSelectedVoucherDetails() {
        int selectedRow = voucherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a voucher first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        VoucherData voucher = tableModel.getVoucherAt(selectedRow);
        showVoucherDetails(voucher);
    }
    
    private void testVoucherOnEvent() {
        if (currentEvent == null) return;
        
        int selectedRow = voucherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a voucher first.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        VoucherData voucher = tableModel.getVoucherAt(selectedRow);
        
        // Test with sample data
        String paxStr = JOptionPane.showInputDialog(this, "Enter number of participants:", "1");
        if (paxStr == null) return;
        
        try {
            int pax = Integer.parseInt(paxStr);
            double originalAmount = currentEvent.getFee() * pax;
            
            if (voucher.isApplicableFor(originalAmount, pax, currentEvent.getEventId(), currentUserType)) {
                double discountAmount = voucher.calculateDiscountAmount(originalAmount);
                double finalAmount = originalAmount - discountAmount;
                
                String message = String.format(
                    "Voucher Test Result:\n\n" +
                    "Voucher Code: %s\n" +
                    "Original Amount: RM%.2f\n" +
                    "Discount: %s (RM%.2f)\n" +
                    "Final Amount: RM%.2f\n\n" +
                    "✅ This voucher is applicable!",
                    voucher.getVoucherCode(), originalAmount, voucher.getDiscountDisplay(), 
                    discountAmount, finalAmount
                );
                
                JOptionPane.showMessageDialog(this, message, "Voucher Test - Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                String message = String.format(
                    "Voucher Test Result:\n\n" +
                    "Voucher Code: %s\n" +
                    "Original Amount: RM%.2f\n\n" +
                    "❌ This voucher is not applicable.\n\n" +
                    "Possible reasons:\n" +
                    "• Minimum amount not met (Required: RM%.2f)\n" +
                    "• Minimum participants not met (Required: %d)\n" +
                    "• User type not eligible (Required: %s)\n" +
                    "• Event not covered by this voucher",
                    voucher.getVoucherCode(), originalAmount, voucher.getMinimumAmount(),
                    voucher.getMinimumPax(), voucher.getUserTypeEligible()
                );
                
                JOptionPane.showMessageDialog(this, message, "Voucher Test - Not Applicable", 
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of participants.", 
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showVoucherDetails(VoucherData voucher) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        String details = String.format(
            "Voucher Details\n" +
            "═══════════════════════════════════════\n\n" +
            "Code: %s\n" +
            "Type: %s\n" +
            "Discount: %s\n" +
            "Description: %s\n\n" +
            "Validity Period:\n" +
            "From: %s\n" +
            "Until: %s\n\n" +
            "Usage Information:\n" +
            "Used: %d times\n" +
            "Limit: %s\n\n" +
            "Eligibility Criteria:\n" +
            "User Type: %s\n" +
            "Minimum Amount: RM%.2f\n" +
            "Minimum Participants: %d\n" +
            "Applicable Events: %s\n\n" +
            "Status: %s",
            voucher.getVoucherCode(),
            voucher.getVoucherType(),
            voucher.getDiscountDisplay(),
            voucher.getDescription(),
            sdf.format(voucher.getValidFrom()),
            voucher.getValidUntil() != null ? sdf.format(voucher.getValidUntil()) : "No expiry",
            voucher.getUsedCount(),
            voucher.getUsageLimit() == -1 ? "Unlimited" : String.valueOf(voucher.getUsageLimit()),
            voucher.getUserTypeEligible(),
            voucher.getMinimumAmount(),
            voucher.getMinimumPax(),
            voucher.getApplicableEvents(),
            voucher.getStatusDisplay()
        );
        
        JOptionPane.showMessageDialog(this, details, "Voucher Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Table model for vouchers
    private class VoucherTableModel extends AbstractTableModel {
        private List<VoucherData> vouchers = new ArrayList<>();
        private final String[] columnNames = {"Code", "Type", "Discount", "Description", "Valid Until", "Status"};
        
        public void setVouchers(List<VoucherData> vouchers) {
            this.vouchers = vouchers;
            fireTableDataChanged();
        }
        
        public VoucherData getVoucherAt(int row) {
            return vouchers.get(row);
        }
        
        @Override
        public int getRowCount() {
            return vouchers.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            VoucherData voucher = vouchers.get(rowIndex);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            switch (columnIndex) {
                case 0: return voucher.getVoucherCode();
                case 1: return voucher.getVoucherType();
                case 2: return voucher.getDiscountDisplay();
                case 3: return voucher.getDescription().length() > 40 ? 
                           voucher.getDescription().substring(0, 40) + "..." : voucher.getDescription();
                case 4: return voucher.getValidUntil() != null ? sdf.format(voucher.getValidUntil()) : "No expiry";
                case 5: return voucher.getStatusDisplay();
                default: return "";
            }
        }
    }
} 