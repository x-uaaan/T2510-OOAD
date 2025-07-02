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
    private JButton saveButton;
    private JButton loadButton;
    private JButton csvInfoButton;

    public EventManagementApp() {
        // Set window title with user info
        String title = "Event Management System - CSV Storage";
        if (LoginRegisterWindow.isUserLoggedIn()) {
            UserData user = LoginRegisterWindow.getLoggedInUser();
            title += " - Welcome, " + user.getUserName();
        }
        
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

        // Create button panel with CSV management buttons
        JPanel buttonPanel = new JPanel();
        createEventButton = new JButton("Create Event");
        saveButton = new JButton("Save to CSV");
        loadButton = new JButton("Reload from CSV");
        csvInfoButton = new JButton("CSV Info");
        JButton logoutButton = new JButton("Logout");
        
        buttonPanel.add(createEventButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(csvInfoButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        createEventButton.addActionListener(e -> showCreateEventWindow());
        saveButton.addActionListener(e -> saveEventsToCSV());
        loadButton.addActionListener(e -> reloadEventsFromCSV());
        csvInfoButton.addActionListener(e -> showCSVInfo());
        logoutButton.addActionListener(e -> handleLogout());
        
        // Add window closing listener to auto-save
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveEventsToCSV();
                System.out.println("Application closing - events saved to CSV");
                System.exit(0);
            }
        });

        refreshEventList();
    }

    private void showCreateEventWindow() {
        CreateEventWindow createEventWindow = new CreateEventWindow(this);
        createEventWindow.setVisible(true);
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
        EventDetailsWindow detailsWindow = new EventDetailsWindow(event, this);
        detailsWindow.setVisible(true);
    }

    private JPanel createEventCard(EventData event) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(220, 220, 220));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 5));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

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

        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Start with login window first
            new LoginRegisterWindow(null).setVisible(true);
        });
    }

    public void deleteEvent(EventData event) {
        // Delete from CSV directly
        EventCSVManager.deleteEventFromCSV(event);
        // Reload from CSV to sync memory
        loadEventsFromCSV();
        refreshEventList();
    }
    
    // CSV Operations
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
    
    private void saveEventsToCSV() {
        try {
            EventCSVManager.saveEventsToCSV(eventList);
            JOptionPane.showMessageDialog(this, 
                "Events saved successfully to CSV!", 
                "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error saving events: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error saving events to CSV: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void reloadEventsFromCSV() {
        int result = JOptionPane.showConfirmDialog(this,
            "This will reload events from CSV and discard any unsaved changes.\nContinue?",
            "Reload Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            loadEventsFromCSV();
            refreshEventList();
            JOptionPane.showMessageDialog(this, 
                "Events reloaded from CSV successfully!", 
                "Reload Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // Save current data
            saveEventsToCSV();
            
            // Logout user
            LoginRegisterWindow.logoutUser();
            
            // Close current window
            this.dispose();
            
            // Show login window
            new LoginRegisterWindow(null).setVisible(true);
        }
    }
    
    private void showCSVInfo() {
        String info = EventCSVManager.getCSVInfo();
        int eventCount = eventList.size();
        String message = info + "\nEvents in memory: " + eventCount;
        
        JOptionPane.showMessageDialog(this, message, 
            "CSV Information", JOptionPane.INFORMATION_MESSAGE);
    }
}