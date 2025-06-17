import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class EventManagementApp extends JFrame {
    private List<EventData> eventList = new ArrayList<>();
    private JPanel eventListPanel;
    private JScrollPane eventListScroll;
    private JButton createEventButton;

    public EventManagementApp() {
        setTitle("Event List");
        setSize(700, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize GUI components first!
        eventListPanel = new JPanel();
        eventListPanel.setLayout(new BoxLayout(eventListPanel, BoxLayout.Y_AXIS));
        eventListScroll = new JScrollPane(eventListPanel);
        eventListScroll.setBorder(BorderFactory.createEmptyBorder());
        add(eventListScroll, BorderLayout.CENTER);

        createEventButton = new JButton("Create Event");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createEventButton);
        add(buttonPanel, BorderLayout.SOUTH);

        createEventButton.addActionListener(e -> showCreateEventWindow());
        refreshEventList();
    }

    private void showCreateEventWindow() {
        CreateEventWindow createEventWindow = new CreateEventWindow(this);
        createEventWindow.setVisible(true);
    }

    public void addEvent(EventData event) {
        eventList.add(event);
        Collections.sort(eventList, Comparator.comparing(EventData::getDate).reversed());
        refreshEventList();
    }

    private void refreshEventList() {
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
        EventDetailsWindow detailsWindow = new EventDetailsWindow(event);
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
            new EventManagementApp().setVisible(true);
        });
    }

    public void deleteEvent(EventData event) {
        eventList.remove(event);
        refreshEventList();
    }
}

// EventData class to hold event info
class EventData {
    private String name, organiser, eventType, venue;
    private int capacity;
    private Date date;
    private double fee;
    public EventData(String name, String organiser, String eventType, String venue, int capacity, Date date, double fee) {
        this.name = name;
        this.organiser = organiser;
        this.eventType = eventType;
        this.venue = venue;
        this.capacity = capacity;
        this.date = date;
        this.fee = fee;
    }
    public String getName() { return name; }
    public String getOrganiser() { return organiser; }
    public String getEventType() { return eventType; }
    public String getVenue() { return venue; }
    public int getCapacity() { return capacity; }
    public Date getDate() { return date; }
    public double getFee() { return fee; }
}

// CreateEventWindow class for event creation
class CreateEventWindow extends JFrame {
    protected JTextField eventNameField;
    protected JComboBox<String> organiserBox;
    protected JTextArea descriptionArea;
    protected JSpinner dateSpinner;
    protected JComboBox<String> venueBox;
    protected JComboBox<String> facultyBox;
    protected JComboBox<String> classBox;
    protected JComboBox<String> lectureHallBox;
    protected JComboBox<String> eventTypeBox;
    protected JSpinner capacitySpinner;
    protected JTextField feeField;
    protected JButton createButton;
    protected JLabel feedbackLabel;
    protected EventManagementApp mainApp;
    protected JComboBox<String> hourBox;
    protected JComboBox<String> minuteBox;

    public CreateEventWindow(EventManagementApp mainApp) {
        this.mainApp = mainApp;
        setTitle("Create Event");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Event"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Event Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Event Name:"), gbc);
        gbc.gridx = 1;
        eventNameField = new JTextField(20);
        formPanel.add(eventNameField, gbc);

        // Organiser (dropdown)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Organiser:"), gbc);
        gbc.gridx = 1;
        String[] organisers = {"", "Aikido Club", "Archery Club", "Arabian Cultural Society", "Badminton Club", "Basketball Club", "Buddhist Society", "Bursa Young Investors Club", "Career Connect Club", "Chess Club", "Chinese Language Society", "Communication Society", "Creative Multimedia Club", "Cyberjaya Accounting Club", "Dive Into Creative Entertainment", "Ebee Vocal", "Engineering Society", "Girl In Tech MMU", "Google Developer Student Club", "Indian Cultural Society", "Information Technology Society", "Institusi Usrah", "International Student Society", "Japanese Cultural Society", "Korean Language Society", "MMU Batting", "MMU E-Sports", "Multimedia University Christian Society", "Multimedia University Cinematics Arts Society", "Multimedia University Deejay Club", "Multimedia University Game Developers Club", "Multimedia University IEEE Club", "Multimedia University IEM Club", "Multimedia University IET", "Multimedia University Management Society", "Multimedia University Music Club", "Multimedia University Superheroes Club", "Netball Club", "Outdoor Activities & Recreational Society", "Pentas", "Persatuan Silat Cekak Pusaka Hanafi", "Playventure Society", "Postgraduate Society", "Rentak Dance Club", "Rugby Club", "Sekretariat Rakan Muda", "Sekretariat Sekolah @ MMU", "Siti Hasmah Symphonic Orchestra", "Soccer Club", "Student College Committee", "Swimming Club", "Tabletop Card Guild (TCG)", "The Voice's", "Theater At MMU", "Unitle Peter Group", "Volleyball Club", "Water Sports Club"};
        organiserBox = new JComboBox<>(organisers);
        organiserBox.setEditable(false);
        organiserBox.setSelectedIndex(0);
        formPanel.add(organiserBox, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Date (date picker)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        formPanel.add(dateSpinner, gbc);

        // Time selection (hour and minute)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        String[] hours = new String[17];
        for (int i = 0; i <= 16; i++) hours[i] = String.format("%02d", i + 8);
        hourBox = new JComboBox<>(hours);
        String[] minutes = {"00", "30"};
        minuteBox = new JComboBox<>(minutes);
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.add(hourBox);
        timePanel.add(new JLabel(":"));
        timePanel.add(minuteBox);
        formPanel.add(timePanel, gbc);

        // Venue (dropdown)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 1;
        String[] venues = {"", "Multipurpose Hall (MPH)", "Dewan Tun Canselor (DTC)", "Stadium", "Central Plaza", "Lecture Hall", "Faculty"};
        venueBox = new JComboBox<>(venues);
        venueBox.setEditable(false);
        venueBox.setSelectedIndex(0);
        formPanel.add(venueBox, gbc);

        // Faculty dropdown (hidden by default)
        gbc.gridx = 0; gbc.gridy++;
        JLabel facultyLabel = new JLabel("Faculty:");
        formPanel.add(facultyLabel, gbc);
        gbc.gridx = 1;
        String[] faculties = {"Faculty", "FCI", "FOM", "FOE", "FCM", "FAC"};
        facultyBox = new JComboBox<>(faculties);
        facultyBox.setEditable(false);
        facultyBox.setSelectedIndex(0);
        formPanel.add(facultyBox, gbc);
        facultyLabel.setVisible(false);
        facultyBox.setVisible(false);

        // Class dropdown (hidden by default)
        gbc.gridx = 0; gbc.gridy++;
        JLabel classLabel = new JLabel("Class:");
        formPanel.add(classLabel, gbc);
        gbc.gridx = 1;
        classBox = new JComboBox<>();
        classBox.setEditable(false);
        classBox.addItem("Class");
        classBox.setSelectedIndex(0);
        formPanel.add(classBox, gbc);
        classLabel.setVisible(false);
        classBox.setVisible(false);

        // Lecture Hall dropdown (hidden by default)
        gbc.gridx = 0; gbc.gridy++;
        JLabel lectureHallLabel = new JLabel("Lecture Hall:");
        formPanel.add(lectureHallLabel, gbc);
        gbc.gridx = 1;
        String[] lectureHalls = {"Lecture Hall", "CQMX1001", "CQMX1002", "CQMX1003", "CQMX1004", "CQMX1005"};
        lectureHallBox = new JComboBox<>(lectureHalls);
        lectureHallBox.setEditable(false);
        lectureHallBox.setSelectedIndex(0);
        formPanel.add(lectureHallBox, gbc);
        lectureHallLabel.setVisible(false);
        lectureHallBox.setVisible(false);

        // Venue selection logic (update)
        venueBox.addActionListener(e -> {
            String selectedVenue = (String) venueBox.getSelectedItem();
            boolean isFaculty = "Faculty".equals(selectedVenue);
            boolean isLectureHall = "Lecture Hall".equals(selectedVenue);
            facultyLabel.setVisible(isFaculty);
            facultyBox.setVisible(isFaculty);
            classLabel.setVisible(false);
            classBox.setVisible(false);
            facultyBox.setSelectedIndex(0);
            classBox.removeAllItems();
            classBox.addItem("Class");
            classBox.setSelectedIndex(0);
            lectureHallLabel.setVisible(isLectureHall);
            lectureHallBox.setVisible(isLectureHall);
            lectureHallBox.setSelectedIndex(0);
        });
        facultyBox.addActionListener(e -> {
            String selectedFaculty = (String) facultyBox.getSelectedItem();
            classBox.removeAllItems();
            classBox.addItem("Class");
            if (selectedFaculty == null || selectedFaculty.equals("Faculty")) {
                classLabel.setVisible(false);
                classBox.setVisible(false);
                return;
            }
            String[] classes = {};
            switch (selectedFaculty) {
                case "FCI":
                    classes = new String[] {
                        "CQAR0001", "CQAR0002", "CQAR0003", "CQAR0004", "CQAR0005", 
                        "CQAR1001", "CQAR1002", "CQAR1003", "CQAR1004", "CQAR1005", "CQAR1006", "CQAR1007", "CQAR1008", "CQAR1009", 
                        "CQAR2001", "CQAR2002", "CQAR2003", "CQAR2004", "CQAR2005", "CQAR2006", "CQAR2007", "CQAR2008", "CQAR2009", 
                        "CQAR3001", "CQAR3002", "CQAR3003", "CQAR3004", "CQAR3005", "CQAR3006", "CQAR3007", "CQAR3008", "CQAR3009", 
                        "CQAR4001", "CQAR4002", "CQAR4003", "CQAR4004", "CQAR4005", "CQAR4006", "CQAR4007", "CQAR4008", "CQAR4009",
                        "CQCR1001", "CQCR1002", "CQCR1003", "CQCR1004", 
                        "CQCR2001", "CQCR2002", "CQCR2003", "CQCR2004", 
                        "CQCR3001", "CQCR3002", "CQCR3003", "CQCR3004"
                    };
                    break;
                case "FOM":
                    classes = new String[] {
                        "CQAR0001", "CQAR0002", "CQAR0003", "CQAR0004", "CQAR0005", 
                        "CQAR1001", "CQAR1002", "CQAR1003", "CQAR1004", "CQAR1005", "CQAR1006", "CQAR1007", "CQAR1008", "CQAR1009", 
                        "CQAR2001", "CQAR2002", "CQAR2003", "CQAR2004", "CQAR2005", "CQAR2006", "CQAR2007", "CQAR2008", "CQAR2009", 
                        "CQAR3001", "CQAR3002", "CQAR3003", "CQAR3004", "CQAR3005", "CQAR3006", "CQAR3007", "CQAR3008", "CQAR3009", 
                        "CQAR4001", "CQAR4002", "CQAR4003", "CQAR4004", "CQAR4005", "CQAR4006", "CQAR4007", "CQAR4008", "CQAR4009",
                        "CQCR1001", "CQCR1002", "CQCR1003", "CQCR1004", 
                        "CQCR2001", "CQCR2002", "CQCR2003", "CQCR2004", 
                        "CQCR3001", "CQCR3002", "CQCR3003", "CQCR3004"
                    };
                    break;
                case "FOE":
                    classes = new String[] {
                        "FOEAR0001", "FOEAR0002", "FOEAR0003", "FOEAR0004", "FOEAR0005", "FOEAR0006", "FOEAR0007", "FOEAR0008", "FOEAR0009", "FOEAR0010", "FOEAR0011", "FOEAR0012", "FOEAR0013", "FOEAR0014", "FOEAR0015", "FOEAR0016", "FOEAR0017", "FOEAR0018", "FOEAR0019", "FOEAR0020", "FOEAR0021", "FOEAR0022", "FOEAR0023", "FOEAR0024", "FOEAR0025", "FOEAR0026", "FOEAR0027", "FOEAR0028", "FOEAR0029", "FOEAR0030", 
                        "FOEAR1001", "FOEAR1002", "FOEAR1003", "FOEAR1004", "FOEAR1005", "FOEAR1006", "FOEAR1007", "FOEAR1008", "FOEAR1009", 
                        "FOEAR2001", "FOEAR2002", "FOEAR2003", "FOEAR2004", "FOEAR2005", "FOEAR2006", "FOEAR2007", "FOEAR2008", "FOEAR2009", 
                        "FOEAR3001", "FOEAR3002", "FOEAR3003", "FOEAR3004", "FOEAR3005", "FOEAR3006", "FOEAR3007", "FOEAR3008", "FOEAR3009", 
                        "FOEBR1001", "FOEBR1002", "FOEBR1003", "FOEBR1004", "FOEBR1005", "FOEBR1006", "FOEBR1007", "FOEBR1008", "FOEBR1009", 
                        "FOEBR2001", "FOEBR2002", "FOEBR2003", "FOEBR2004", "FOEBR2005", "FOEBR2006", "FOEBR2007", "FOEBR2008", "FOEBR2009", 
                        "FOEBR3001", "FOEBR3002", "FOEBR3003", "FOEBR3004", "FOEBR3005", "FOEBR3006", "FOEBR3007", "FOEBR3008", "FOEBR3009", 
                        "FOEBR4001", "FOEBR4002", "FOEBR4003", "FOEBR4004", "FOEBR4005", "FOEBR4006", "FOEBR4007", "FOEBR4008", "FOEBR4009"
                    };
                    break;
                case "FCM":
                    classes = new String[] {
                        "FCM0001", "FCM0002", "FCM0003", "FCM0004", "FCM0005", "FCM0006", "FCM0007", "FCM0008", "FCM0009", "FCM0010", 
                        "FCM1001", "FCM1002", "FCM1003", "FCM1004", "FCM1005", "FCM1006", "FCM1007", "FCM1008", "FCM1009", 
                        "FCM2001", "FCM2002", "FCM2003", "FCM2004", "FCM2005", "FCM2006", "FCM2007", "FCM2008", "FCM2009", 
                        "FCM3001", "FCM3002", "FCM3003", "FCM3004", "FCM3005", "FCM3006", "FCM3007", "FCM3008", "FCM3009", 
                        "FCM4001", "FCM4002", "FCM4003", "FCM4004", "FCM4005", "FCM4006", "FCM4007", "FCM4008", "FCM4009"
                    };
                    break;
                case "FAC":
                    classes = new String[] {
                        "FAC0001", "FAC0002", "FAC0003", "FAC0004", "FAC0005", "FAC0006", "FAC0007", "FAC0008", "FAC0009"
                    };
                    break;
            }
            for (String c : classes) classBox.addItem(c);
            classLabel.setVisible(true);
            classBox.setVisible(true);
            classBox.setSelectedIndex(0);
        });

        // Event Type (dropdown, extended)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Event Type:"), gbc);
        gbc.gridx = 1;
        String[] eventTypes = {"", "Seminar", "Workshop", "Cultural Event", "Sports Event", "Class/Tutorial", "Club Activity/Event", "Examination", "External Event", "Meeting/Discussion", "Presentation", "Training/Conference", "Others"};
        eventTypeBox = new JComboBox<>(eventTypes);
        eventTypeBox.setEditable(false);
        eventTypeBox.setSelectedIndex(0);
        formPanel.add(eventTypeBox, gbc);

        // Capacity
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1;
        capacitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
        formPanel.add(capacitySpinner, gbc);

        // Registration Fee
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Registration Fee (RM):"), gbc);
        gbc.gridx = 1;
        feeField = new JTextField(20);
        formPanel.add(feeField, gbc);

        // Create Button
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        createButton = new JButton("Create Event");
        formPanel.add(createButton, gbc);

        // Feedback Label
        gbc.gridy++;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setForeground(Color.BLUE);
        formPanel.add(feedbackLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Action
        createButton.addActionListener(e -> handleCreateEvent());
    }

    private void handleCreateEvent() {
        String name = eventNameField.getText().trim();
        String organiser = (String) organiserBox.getSelectedItem();
        String desc = descriptionArea.getText().trim();
        Date date = (Date) dateSpinner.getValue();
        String hourStr = (String) hourBox.getSelectedItem();
        String minStr = (String) minuteBox.getSelectedItem();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStr));
        cal.set(Calendar.MINUTE, Integer.parseInt(minStr));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date eventDateTime = cal.getTime();
        String venue = (String) venueBox.getSelectedItem();
        String faculty = (facultyBox.isVisible()) ? (String) facultyBox.getSelectedItem() : null;
        String classRoom = (classBox.isVisible()) ? (String) classBox.getSelectedItem() : null;
        String lectureHall = (lectureHallBox.isVisible()) ? (String) lectureHallBox.getSelectedItem() : null;
        String eventType = (String) eventTypeBox.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        String feeStr = feeField.getText().trim();

        // Input validation
        if (name.isEmpty() || organiser == null || organiser.isEmpty() || desc.isEmpty() || date == null || venue == null || venue.isEmpty() || eventType == null || eventType.isEmpty() || feeStr.isEmpty()) {
            feedbackLabel.setText("Please fill in all fields and make selections.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        if ("Faculty".equals(venue)) {
            if (faculty == null || faculty.equals("Faculty") || classRoom == null || classRoom.equals("Class")) {
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
        double fee = 0;
        try {
            fee = Double.parseDouble(feeStr);
            if (fee < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Invalid registration fee.");
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
        // Add event to main app and close window
        EventData event = new EventData(name, organiser, eventType, fullVenue, capacity, eventDateTime, fee);
        mainApp.addEvent(event);
        dispose();
    }
}

// Add EventDetailsWindow class
class EventDetailsWindow extends JFrame {
    private EventData event;
    public EventDetailsWindow(EventData event) {
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
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
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

        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                // Remove event from main list
                Window[] windows = Window.getWindows();
                for (Window w : windows) {
                    if (w instanceof EventManagementApp) {
                        ((EventManagementApp) w).deleteEvent(event);
                        break;
                    }
                }
                this.dispose();
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
    private double earlyBirdDiscount = 0.10; // 10%
    private double groupDiscount = 0.15; // 15%
    private int confirmedPax = 1;

    public PaymentPage(EventData event) {
        this.event = event;
        setTitle("Event Registration & Payment");
        setSize(400, 420);
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
            FeeBreakdownPage breakdownPage = new FeeBreakdownPage(event, confirmedPax, cateringBox.isSelected(), transportBox.isSelected());
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
        // Early bird: 10% off if registering 7+ days before event
        long now = System.currentTimeMillis();
        long eventTime = event.getDate().getTime();
        long daysDiff = (eventTime - now) / (1000 * 60 * 60 * 24);
        if (daysDiff >= 7) {
            discount += totalBeforeDiscount * earlyBirdDiscount;
            discountDetails.append(String.format("Early Bird (10%%): -RM %.2f\n", totalBeforeDiscount * earlyBirdDiscount));
        }
        // Group discount: 15% off if group size >= 5
        if (groupSize >= 5) {
            discount += totalBeforeDiscount * groupDiscount;
            discountDetails.append(String.format("Group (15%%): -RM %.2f\n", totalBeforeDiscount * groupDiscount));
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

// Add FeeBreakdownPage class
class FeeBreakdownPage extends JFrame {
    public FeeBreakdownPage(EventData event, int pax, boolean catering, boolean transport) {
        setTitle("Fee Breakdown");
        setSize(400, 450);
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
        double earlyBirdDiscount = 0.10;
        double groupDiscount = 0.15;
        double discount = 0;
        StringBuilder discountDetails = new StringBuilder();
        long now = System.currentTimeMillis();
        long eventTime = event.getDate().getTime();
        long daysDiff = (eventTime - now) / (1000 * 60 * 60 * 24);
        if (daysDiff >= 7) {
            discount += totalBeforeDiscount * earlyBirdDiscount;
            discountDetails.append(String.format("Early Bird (10%%): -RM %.2f\n", totalBeforeDiscount * earlyBirdDiscount));
        }
        if (pax >= 5) {
            discount += totalBeforeDiscount * groupDiscount;
            discountDetails.append(String.format("Group (15%%): -RM %.2f\n", totalBeforeDiscount * groupDiscount));
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

// Add UpdateEventWindow class
class UpdateEventWindow extends CreateEventWindow {
    private EventData originalEvent;
    private JFrame parentWindow;
    public UpdateEventWindow(EventData event, JFrame parentWindow) {
        super(null); // We'll handle addEvent manually
        this.originalEvent = event;
        this.parentWindow = parentWindow;
        setTitle("Update Event");
        // Pre-fill fields
        eventNameField.setText(event.getName());
        organiserBox.setSelectedItem(event.getOrganiser());
        descriptionArea.setText(""); // No description in EventData, leave blank
        dateSpinner.setValue(event.getDate());
        hourBox.setSelectedItem(new SimpleDateFormat("HH").format(event.getDate()));
        minuteBox.setSelectedItem(new SimpleDateFormat("mm").format(event.getDate()));
        venueBox.setSelectedItem(event.getVenue());
        eventTypeBox.setSelectedItem(event.getEventType());
        capacitySpinner.setValue(event.getCapacity());
        feeField.setText(String.format("%.2f", event.getFee()));
        // Hide faculty/class/lecture hall for simplicity (could be improved)
        createButton.setText("Update");
        for (ActionListener al : createButton.getActionListeners()) createButton.removeActionListener(al);
        createButton.addActionListener(e -> handleUpdateEvent());
    }
    private void handleUpdateEvent() {
        String name = eventNameField.getText().trim();
        String organiser = (String) organiserBox.getSelectedItem();
        String desc = descriptionArea.getText().trim();
        Date date = (Date) dateSpinner.getValue();
        String hourStr = (String) hourBox.getSelectedItem();
        String minStr = (String) minuteBox.getSelectedItem();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourStr));
        cal.set(Calendar.MINUTE, Integer.parseInt(minStr));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date eventDateTime = cal.getTime();
        String venue = (String) venueBox.getSelectedItem();
        String eventType = (String) eventTypeBox.getSelectedItem();
        int capacity = (Integer) capacitySpinner.getValue();
        String feeStr = feeField.getText().trim();
        double fee = 0;
        try {
            fee = Double.parseDouble(feeStr);
            if (fee < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Invalid registration fee.");
            feedbackLabel.setForeground(Color.RED);
            return;
        }
        // Remove old event, add new event
        Window[] windows = Window.getWindows();
        for (Window w : windows) {
            if (w instanceof EventManagementApp) {
                ((EventManagementApp) w).deleteEvent(originalEvent);
                ((EventManagementApp) w).addEvent(new EventData(name, organiser, eventType, venue, capacity, eventDateTime, fee));
                break;
            }
        }
        this.dispose();
        if (parentWindow != null) parentWindow.dispose();
        // Show new event details
        EventDetailsWindow detailsWindow = new EventDetailsWindow(new EventData(name, organiser, eventType, venue, capacity, eventDateTime, fee));
        detailsWindow.setVisible(true);
    }
} 