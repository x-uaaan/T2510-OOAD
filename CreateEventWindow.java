// CreateEventWindow.java
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

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
    protected JTextField fixedCostField;
    protected JTextField variableCostField;
    protected JButton calcFeeButton;
    protected JCheckBox earlyBirdCheckBox;
    protected JSpinner earlyBirdEndDateSpinner;
    protected JComboBox<String> earlyBirdHourBox;
    protected JComboBox<String> earlyBirdMinuteBox;
    protected JComboBox<String> earlyBirdDiscountTypeBox;
    protected JTextField earlyBirdDiscountValueField;
    protected JCheckBox promoCodeCheckBox;
    protected JTextField promoCodeField;
    protected JComboBox<String> promoDiscountTypeBox;
    protected JTextField promoDiscountField;
    protected JPanel earlyBirdDiscountRowPanel;
    protected JPanel promoDiscountRowPanel;

    public CreateEventWindow(EventManagementApp mainApp) {
        this.mainApp = mainApp;
        setTitle("Create Event");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 800);
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

        // Capacity (no unlimited, min 1)
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Capacity (min 1):"), gbc);
        gbc.gridx = 1;
        capacitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
        formPanel.add(capacitySpinner, gbc);

        // --- Fee Calculation Section ---
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Total Fixed Costs (RM):"), gbc);
        gbc.gridx = 1;
        fixedCostField = new JTextField(10);
        formPanel.add(fixedCostField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Variable Cost per Attendee (RM):"), gbc);
        gbc.gridx = 1;
        variableCostField = new JTextField(10);
        formPanel.add(variableCostField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        calcFeeButton = new JButton("Calculate Base Fee");
        formPanel.add(calcFeeButton, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("Registration Fee (RM):"), gbc);
        gbc.gridx = 1;
        feeField = new JTextField(20);
        feeField.setEditable(false);
        formPanel.add(feeField, gbc);

        // --- Early Bird Section ---
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        earlyBirdCheckBox = new JCheckBox("Enable Early Bird Discount");
        formPanel.add(earlyBirdCheckBox, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel earlyBirdRightPanel = new JPanel();
        earlyBirdRightPanel.setLayout(new BoxLayout(earlyBirdRightPanel, BoxLayout.X_AXIS));
        earlyBirdRightPanel.add(Box.createHorizontalGlue());
        earlyBirdRightPanel.add(new JLabel("Until: "));
        SpinnerDateModel earlyBirdDateModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        earlyBirdEndDateSpinner = new JSpinner(earlyBirdDateModel);
        JSpinner.DateEditor earlyBirdDateEditor = new JSpinner.DateEditor(earlyBirdEndDateSpinner, "yyyy-MM-dd");
        earlyBirdEndDateSpinner.setEditor(earlyBirdDateEditor);
        String[] ebHours = new String[17];
        for (int i = 0; i <= 16; i++) ebHours[i] = String.format("%02d", i + 8);
        earlyBirdHourBox = new JComboBox<>(ebHours);
        earlyBirdMinuteBox = new JComboBox<>(minutes);
        earlyBirdRightPanel.add(earlyBirdEndDateSpinner);
        earlyBirdRightPanel.add(new JLabel(" "));
        earlyBirdRightPanel.add(earlyBirdHourBox);
        earlyBirdRightPanel.add(new JLabel(":"));
        earlyBirdRightPanel.add(earlyBirdMinuteBox);
        formPanel.add(earlyBirdRightPanel, gbc);
        // Early Bird Discount Configuration
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        JPanel earlyBirdDiscountRowPanel = new JPanel(new BorderLayout());
        JLabel earlyBirdDiscountLabel = new JLabel("Early Bird Discount: ");
        earlyBirdDiscountRowPanel.add(earlyBirdDiscountLabel, BorderLayout.WEST);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel earlyBirdDiscountPanel = new JPanel();
        earlyBirdDiscountPanel.setLayout(new BoxLayout(earlyBirdDiscountPanel, BoxLayout.X_AXIS));
        earlyBirdDiscountPanel.add(Box.createHorizontalGlue());
        String[] discountTypes = {"PERCENTAGE", "FIXED_AMOUNT"};
        earlyBirdDiscountTypeBox = new JComboBox<>(discountTypes);
        earlyBirdDiscountValueField = new JTextField(6);
        earlyBirdDiscountPanel.add(earlyBirdDiscountTypeBox);
        earlyBirdDiscountPanel.add(Box.createHorizontalStrut(8));
        earlyBirdDiscountPanel.add(earlyBirdDiscountValueField);
        JLabel discountUnitLabel = new JLabel("%");
        earlyBirdDiscountPanel.add(Box.createHorizontalStrut(4));
        earlyBirdDiscountPanel.add(discountUnitLabel);
        earlyBirdDiscountRowPanel.add(earlyBirdDiscountPanel, BorderLayout.CENTER);
        formPanel.add(earlyBirdDiscountRowPanel, gbc);
        // Hide early bird panels initially
        earlyBirdRightPanel.setVisible(false);
        earlyBirdDiscountRowPanel.setVisible(false);
        // Update discount unit label based on type selection
        earlyBirdDiscountTypeBox.addActionListener(e -> {
            String selectedType = (String) earlyBirdDiscountTypeBox.getSelectedItem();
            if ("PERCENTAGE".equals(selectedType)) {
                discountUnitLabel.setText("%");
            } else {
                discountUnitLabel.setText("RM");
            }
        });
        earlyBirdCheckBox.addActionListener(e -> {
            boolean isSelected = earlyBirdCheckBox.isSelected();
            earlyBirdRightPanel.setVisible(isSelected);
            earlyBirdDiscountRowPanel.setVisible(isSelected);
        });
        // --- Promotion Code Section ---
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        promoCodeCheckBox = new JCheckBox("Enable Promotion Code");
        formPanel.add(promoCodeCheckBox, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel promoRightPanel = new JPanel();
        promoRightPanel.setLayout(new BoxLayout(promoRightPanel, BoxLayout.X_AXIS));
        promoRightPanel.add(Box.createHorizontalGlue());
        promoRightPanel.add(new JLabel("Code: "));
        promoCodeField = new JTextField(8);
        promoRightPanel.add(promoCodeField);
        formPanel.add(promoRightPanel, gbc);
        // Promo Discount Configuration
        gbc.gridx = 0; gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        JPanel promoDiscountRowPanel = new JPanel(new BorderLayout());
        JLabel promoDiscountLabel = new JLabel("Promo Discount: ");
        promoDiscountRowPanel.add(promoDiscountLabel, BorderLayout.WEST);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel promoDiscountPanel = new JPanel();
        promoDiscountPanel.setLayout(new BoxLayout(promoDiscountPanel, BoxLayout.X_AXIS));
        promoDiscountPanel.add(Box.createHorizontalGlue());
        String[] promoDiscountTypes = {"PERCENTAGE", "FIXED_AMOUNT"};
        promoDiscountTypeBox = new JComboBox<>(promoDiscountTypes);
        promoDiscountField = new JTextField(6);
        promoDiscountPanel.add(promoDiscountTypeBox);
        promoDiscountPanel.add(Box.createHorizontalStrut(8));
        promoDiscountPanel.add(promoDiscountField);
        JLabel promoDiscountUnitLabel = new JLabel("%");
        promoDiscountPanel.add(Box.createHorizontalStrut(4));
        promoDiscountPanel.add(promoDiscountUnitLabel);
        promoDiscountRowPanel.add(promoDiscountPanel, BorderLayout.CENTER);
        formPanel.add(promoDiscountRowPanel, gbc);
        // Hide promo panels initially
        promoRightPanel.setVisible(false);
        promoDiscountRowPanel.setVisible(false);
        // Update discount unit label based on type selection
        promoDiscountTypeBox.addActionListener(e -> {
            String selectedType = (String) promoDiscountTypeBox.getSelectedItem();
            if ("PERCENTAGE".equals(selectedType)) {
                promoDiscountUnitLabel.setText("%");
            } else {
                promoDiscountUnitLabel.setText("RM");
            }
        });
        promoCodeCheckBox.addActionListener(e -> {
            boolean isSelected = promoCodeCheckBox.isSelected();
            promoRightPanel.setVisible(isSelected);
            promoDiscountRowPanel.setVisible(isSelected);
        });
        // Make row panels accessible for UpdateEventWindow
        this.earlyBirdDiscountRowPanel = earlyBirdDiscountRowPanel;
        this.promoDiscountRowPanel = promoDiscountRowPanel;

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

        // --- Fee Calculation Logic ---
        calcFeeButton.addActionListener(e -> {
            String fixedStr = fixedCostField.getText().trim();
            String varStr = variableCostField.getText().trim();
            // Estimated attendees = capacity
            int est = (Integer) capacitySpinner.getValue();
            double fixed = 0, var = 0;
            try {
                fixed = Double.parseDouble(fixedStr);
                var = Double.parseDouble(varStr);
                if (fixed < 0 || var < 0 || est <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                feedbackLabel.setText("Invalid cost or capacity.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            double baseFee = (fixed + (var * est)) / est;
            if (baseFee <= 0) {
                feedbackLabel.setText("Base fee must be greater than 0.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            feeField.setText(String.format("%.2f", baseFee));
            feedbackLabel.setText("Base fee calculated.");
            feedbackLabel.setForeground(Color.BLUE);
        });

        // Button Action
        createButton.addActionListener(e -> handleCreateEvent());
    }

    private void handleCreateEvent() {
        String name = eventNameField.getText().trim();
        String organiser = (String) organiserBox.getSelectedItem();
        String desc = descriptionArea.getText().trim();
        String fixedStr = fixedCostField.getText().trim();
        String varStr = variableCostField.getText().trim();
        double fixedCost = 0, variableCost = 0;
        try {
            fixedCost = Double.parseDouble(fixedStr);
        } catch (Exception e) { fixedCost = 0; }
        try {
            variableCost = Double.parseDouble(varStr);
        } catch (Exception e) { variableCost = 0; }
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
        if (capacity <= 0) {
            feedbackLabel.setText("Capacity must be at least 1.");
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
            if (fee <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            feedbackLabel.setText("Invalid registration fee. Must be > 0.");
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
        // Early bird
        boolean earlyBirdEnabled = earlyBirdCheckBox.isSelected();
        Date earlyBirdEnd = null;
        String earlyBirdDiscountType = null;
        double earlyBirdDiscountValue = 0;
        if (earlyBirdEnabled) {
            Date ebDate = (Date) earlyBirdEndDateSpinner.getValue();
            String ebHour = (String) earlyBirdHourBox.getSelectedItem();
            String ebMin = (String) earlyBirdMinuteBox.getSelectedItem();
            Calendar ebCal = Calendar.getInstance();
            ebCal.setTime(ebDate);
            ebCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(ebHour));
            ebCal.set(Calendar.MINUTE, Integer.parseInt(ebMin));
            ebCal.set(Calendar.SECOND, 0);
            ebCal.set(Calendar.MILLISECOND, 0);
            earlyBirdEnd = ebCal.getTime();
            
            // Early bird discount validation
            earlyBirdDiscountType = (String) earlyBirdDiscountTypeBox.getSelectedItem();
            String discountValueStr = earlyBirdDiscountValueField.getText().trim();
            if (discountValueStr.isEmpty()) {
                feedbackLabel.setText("Please enter early bird discount value.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            try {
                earlyBirdDiscountValue = Double.parseDouble(discountValueStr);
                if (earlyBirdDiscountValue <= 0) throw new NumberFormatException();
                if ("PERCENTAGE".equals(earlyBirdDiscountType) && earlyBirdDiscountValue >= 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                String message = "PERCENTAGE".equals(earlyBirdDiscountType) ? 
                    "Early bird discount percentage must be between 0 and 100." : 
                    "Early bird discount amount must be greater than 0.";
                feedbackLabel.setText(message);
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        // Promo code
        boolean promoEnabled = promoCodeCheckBox.isSelected();
        String promoCode = null;
        String promoDiscountType = null;
        double promoDiscount = 0;
        if (promoEnabled) {
            promoCode = promoCodeField.getText().trim();
            promoDiscountType = (String) promoDiscountTypeBox.getSelectedItem();
            String promoDiscStr = promoDiscountField.getText().trim();
            if (promoCode.isEmpty() || promoDiscStr.isEmpty()) {
                feedbackLabel.setText("Please enter promo code and discount value.");
                feedbackLabel.setForeground(Color.RED);
                return;
            }
            try {
                promoDiscount = Double.parseDouble(promoDiscStr);
                if (promoDiscount <= 0) throw new NumberFormatException();
                if ("PERCENTAGE".equals(promoDiscountType) && promoDiscount >= 100) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                String message = "PERCENTAGE".equals(promoDiscountType) ? 
                    "Promo discount percentage must be between 0 and 100." : 
                    "Promo discount amount must be greater than 0.";
                feedbackLabel.setText(message);
                feedbackLabel.setForeground(Color.RED);
                return;
            }
        }
        // Add event to main app and close window
        EventData event = new EventData(name, organiser, eventType, fullVenue, capacity, eventDateTime, fee, desc, fixedCost, variableCost, 
            earlyBirdEnabled, earlyBirdEnd, earlyBirdDiscountType, earlyBirdDiscountValue, 
            promoEnabled, promoCode, promoDiscountType, promoDiscount, "Active");
        mainApp.addEvent(event);
        dispose();
    }
}