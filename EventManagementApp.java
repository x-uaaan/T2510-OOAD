// EventManagementApp.java
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

public class EventManagementApp extends JFrame {
    private List<EventData> eventList = new ArrayList<>();
    private JPanel eventListPanel;
    private JScrollPane eventListScroll;
    private JButton createEventButton;
    private String userType = "STUDENT"; // Default, should be set from login

    public EventManagementApp() {
        // Set window title with user info
        String title = "Event Management System - CSV Storage";
        // TODO: Integrate with login system
        // if (LoginRegisterWindow.isUserLoggedIn()) {
        //     UserData user = LoginRegisterWindow.getLoggedInUser();
        //     title += " - Welcome, " + user.getUserName();
        // }
        
        setTitle(title);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load events from CSV on startup
        loadEventsFromCSV();

        // Initialize GUI components
        eventListPanel = new JPanel();
        eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));
        eventListScroll = new JScrollPane(eventListPanel);
        eventListScroll.setBorder(BorderFactory.createEmptyBorder());
        add(eventListScroll, BorderLayout.CENTER);

        // Create button panel with essential buttons only
        JPanel buttonPanel = new JPanel();
        createEventButton = new JButton("Create Event");
        JButton viewVouchersButton = new JButton("View Vouchers");
        JButton logoutButton = new JButton("Logout");
        
        buttonPanel.add(createEventButton);
        buttonPanel.add(viewVouchersButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        createEventButton.addActionListener(e -> showCreateEventWindow());
        viewVouchersButton.addActionListener(e -> showVoucherListWindow());
        logoutButton.addActionListener(e -> handleLogout());
        
        // Add window closing listener to auto-save
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Auto-save events on close (silent operation)
                try {
                    EventCSVManager.saveEventsToCSV(eventList);
                    System.out.println("Application closing - events saved to CSV");
                } catch (Exception e) {
                    System.err.println("Error saving events on close: " + e.getMessage());
                }
                System.exit(0);
            }
        });

        refreshEventList();
    }

    private void showCreateEventWindow() {
        CreateEventWindow createEventWindow = new CreateEventWindow(this);
        createEventWindow.setVisible(true);
    }
    
    private void showVoucherListWindow() {
        // Get user type from logged in user if available
        String userType = "STUDENT"; // Default - TODO: integrate with login system
        // if (LoginRegisterWindow.isUserLoggedIn()) {
        //     UserData user = LoginRegisterWindow.getLoggedInUser();
        //     userType = user.getUserType();
        // }
        
        VoucherListWindow voucherWindow = new VoucherListWindow(userType);
        voucherWindow.setVisible(true);
    }

    public void addEvent(EventData event) {
        // Add to CSV directly
        EventCSVManager.addEventToCSV(event);
        // Reload from CSV to sync memory
        loadEventsFromCSV();
        refreshEventList();
    }

    public void refreshEventList() {
        eventListPanel.removeAll();
        if (eventList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No events available.");
            emptyLabel.setForeground(Color.DARK_GRAY);
            eventListPanel.add(emptyLabel);
        } else {
            for (EventData event : eventList) {
                JPanel card = createEventCard(event);
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        showEventDetailsWindow(event);
                    }
                });
                eventListPanel.add(card);
            }
        }
        eventListPanel.revalidate();
        eventListPanel.repaint();
    }

    private void showEventDetailsWindow(EventData event) {
        EventDetailsWindow detailsWindow = new EventDetailsWindow(event, this, userType);
        detailsWindow.setVisible(true);
    }

    private JPanel createEventCard(EventData event) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Set background color based on status
                Color bgColor = Color.WHITE;
                String status = event.getStatus() != null ? event.getStatus() : "Ongoing";
                boolean isFull = event.getCapacity() == 0;
                boolean isCancelled = "Cancelled".equalsIgnoreCase(status);
                boolean isClosedSoon = false;
                long millisToEvent = event.getDate().getTime() - System.currentTimeMillis();
                long daysToEvent = millisToEvent / (1000 * 60 * 60 * 24);
                if (!isFull && !isCancelled) {
                    if (daysToEvent < 3 || event.getCapacity() < 10) {
                        isClosedSoon = true;
                    }
                }
                if (isFull) {
                    bgColor = new Color(230, 230, 230); // light grey
                } else if (isCancelled) {
                    bgColor = new Color(100, 100, 100); // dark grey
                } else if (isClosedSoon) {
                    bgColor = new Color(255, 255, 180); // yellow
                }
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 0);
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel titleLabel = new JLabel(event.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        card.add(titleLabel);

        JLabel organiserLabel = new JLabel(event.getOrganiser());
        organiserLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        organiserLabel.setForeground(Color.DARK_GRAY);
        card.add(organiserLabel);

        String details = String.format("Event Type: %s      Venue: %s      Capacity: %s    Fee: RM %.2f",
            event.getEventType(),
            event.getVenue(),
            (event.getCapacity() > 0 ? event.getCapacity() : "No Cap"),
            event.getFee()
        );
        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailsLabel.setForeground(Color.GRAY);
        card.add(detailsLabel);

        // Add status label
        String statusText = event.getStatus() != null ? event.getStatus() : "Ongoing";
        boolean isFull = event.getCapacity() == 0;
        boolean isCancelled = "Cancelled".equalsIgnoreCase(statusText);
        boolean isClosedSoon = false;
        long millisToEvent = event.getDate().getTime() - System.currentTimeMillis();
        long daysToEvent = millisToEvent / (1000 * 60 * 60 * 24);
        if (!isFull && !isCancelled) {
            if (daysToEvent < 3 || event.getCapacity() < 10) {
                isClosedSoon = true;
            }
        }
        String statusLabelText = "";
        if (isFull) statusLabelText = "FULL";
        else if (isCancelled) statusLabelText = "CANCELLED";
        else if (isClosedSoon) statusLabelText = "CLOSED SOON";
        else statusLabelText = statusText.toUpperCase();
        JLabel statusLabel = new JLabel(statusLabelText);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        if (isFull) statusLabel.setForeground(new Color(120, 120, 120));
        else if (isCancelled) statusLabel.setForeground(new Color(60, 60, 60));
        else if (isClosedSoon) statusLabel.setForeground(new Color(180, 140, 0));
        else statusLabel.setForeground(new Color(0, 120, 0));
        card.add(statusLabel);

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // INTEGRATED LOGIN FLOW
            // Show login window first, then main app after successful login
            LoginRegisterWindow loginWindow = new LoginRegisterWindow((UserData loggedInUser) -> {
                // This code runs after successful login
                EventManagementApp app = new EventManagementApp();
                // Optionally, pass user info to app if needed
                app.setVisible(true);
            });
            loginWindow.setVisible(true);
        });
    }

    public void deleteEvent(EventData event) {
        // Delete from CSV directly
        EventCSVManager.deleteEventFromCSV(event);
        // Reload from CSV to sync memory
        loadEventsFromCSV();
        refreshEventList();
    }
    
    // CSV Operations - Core functionality for automatic operations
    public void loadEventsFromCSV() {
        try {
            List<EventData> loadedEvents = EventCSVManager.loadEventsFromCSV();
            eventList.clear();
            eventList.addAll(loadedEvents);
            Collections.sort(eventList, Comparator.comparing(EventData::getDate).reversed());
            
            System.out.println("Loaded " + eventList.size() + " events from CSV");
        } catch (Exception e) {
            System.err.println("Error loading events: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading events from CSV: " + e.getMessage(), 
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // Auto-save current data before logout
            try {
                EventCSVManager.saveEventsToCSV(eventList);
                System.out.println("Events saved before logout");
            } catch (Exception e) {
                System.err.println("Error saving events during logout: " + e.getMessage());
            }
            
            // Logout user - TODO: integrate with login system
            // LoginRegisterWindow.logoutUser();
            
            // Close current window
            this.dispose();
            
            // Show login window - TODO: integrate with login system
            // new LoginRegisterWindow(null).setVisible(true);
            
            // For now, just exit the application
            System.exit(0);
        }
    }
}