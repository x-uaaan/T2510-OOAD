// EventDetailsWindow.java
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;

// Add EventDetailsWindow class
class EventDetailsWindow extends JFrame {
    private EventData event;
    private EventManagementApp mainApp;
    public EventDetailsWindow(EventData event, EventManagementApp mainApp) {
        this.mainApp = mainApp;
        this.event = event;
        setTitle("Event Details");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        detailsPanel.setBackground(Color.WHITE);

        detailsPanel.add(makeDetailLabel("Event Title: ", event.getName()));
        detailsPanel.add(makeDetailLabel("Organiser: ", event.getOrganiser()));
        detailsPanel.add(makeDetailLabel("Event Type: ", event.getEventType()));
        detailsPanel.add(makeDetailLabel("Venue: ", event.getVenue()));
        detailsPanel.add(makeDetailLabel("Capacity: ", String.valueOf(event.getCapacity())));
        detailsPanel.add(makeDetailLabel("Fee: ", String.format("RM %.2f", event.getFee())));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
        detailsPanel.add(makeDetailLabel("Date: ", sdf.format(event.getDate())));
        detailsPanel.add(makeDetailLabel("Time: ", stf.format(event.getDate())));

        add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        JButton registerButton = new JButton("Register");
        JButton updateButton = new JButton("Update");
        JButton attendeesButton = new JButton("Attendees");
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(attendeesButton);
        add(buttonPanel, BorderLayout.SOUTH);

        registerButton.addActionListener(e -> {
            PaymentPage paymentPage = new PaymentPage(event);
            paymentPage.setVisible(true);
        });

        updateButton.addActionListener(e -> {
            UpdateEventWindow updateWindow = new UpdateEventWindow(event, this);
            updateWindow.setVisible(true);
            this.setVisible(false);
        });

        attendeesButton.addActionListener(e -> {
            AttendeeListWindow attendeeListWindow = new AttendeeListWindow(event);
            attendeeListWindow.setVisible(true);
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