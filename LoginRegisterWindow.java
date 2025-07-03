import java.awt.*;
import java.util.Random;
import javax.swing.*;

public class LoginRegisterWindow extends JFrame {
    private static UserData loggedInUser = null;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTextField loginEmailField;
    private JLabel loginFeedbackLabel;
    private JTextField regUserNameField, regStudentIdField, regEmailField, regPhoneField;
    private JComboBox<String> regFacultyCombo, regUserTypeCombo;
    private JLabel regFeedbackLabel;
    private String currentOTP = null;
    private long otpGeneratedTime = 0;
    private String otpEmail = null;
    private EventManagementApp mainApp;
    private LoginSuccessListener loginSuccessListener;

    public interface LoginSuccessListener {
        void onLoginSuccess(UserData user);
    }

    public LoginRegisterWindow(LoginSuccessListener listener) {
        this.loginSuccessListener = listener;
        setTitle("Event Management System");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initializeComponents();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createRegistrationPanel(), "REGISTER");
        add(cardPanel);
        cardLayout.show(cardPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Login to Your Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        loginEmailField = new JTextField(25);
        panel.add(loginEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JButton sendOtpAndLoginBtn = new JButton("Login via OTP");
        panel.add(sendOtpAndLoginBtn, gbc);
        gbc.gridx = 1;

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginFeedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        loginFeedbackLabel.setForeground(Color.GRAY);
        panel.add(loginFeedbackLabel, gbc);

        gbc.gridy++;
        JButton switchToRegister = new JButton("Don't have an account? Register here");
        switchToRegister.setBorderPainted(false);
        switchToRegister.setContentAreaFilled(false);
        switchToRegister.setForeground(Color.BLUE);
        panel.add(switchToRegister, gbc);

        sendOtpAndLoginBtn.addActionListener(e -> handleSendOtpAndLogin());
        switchToRegister.addActionListener(e -> cardLayout.show(cardPanel, "REGISTER"));
        return panel;
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Create New Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        regUserNameField = new JTextField(20);
        panel.add(regUserNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("User Type:"), gbc);
        gbc.gridx = 1;
        regUserTypeCombo = new JComboBox<>(new String[] {"Student", "Staff"});
        panel.add(regUserTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel idLabel = new JLabel("Student ID:");
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        regStudentIdField = new JTextField(20);
        panel.add(regStudentIdField, gbc);

        // Add listener to user type combo to toggle ID label and field
        regUserTypeCombo.addActionListener(e -> {
            String selected = (String) regUserTypeCombo.getSelectedItem();
            if ("Student".equals(selected)) {
                idLabel.setText("Student ID:");
                regStudentIdField.setVisible(true);
            } else if ("Staff".equals(selected)) {
                idLabel.setText("Staff ID:");
                regStudentIdField.setVisible(true);
            }
        });
        // Set initial state
        regUserTypeCombo.setSelectedIndex(0);
        idLabel.setText("Student ID:");
        regStudentIdField.setVisible(true);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Faculty:"), gbc);
        gbc.gridx = 1;
        regFacultyCombo = new JComboBox<>(new String[] {
            "Faculty of Computing and Informatics (FCI)",
            "Faculty of Engineering and Technology (FET)",
            "Faculty of Business and Law (FBL)",
            "Faculty of Creative Arts (FCA)",
            "Faculty of Applied Sciences (FAS)",
            "Faculty of Management (FOM)",
            "Other"
        });
        panel.add(regFacultyCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        regEmailField = new JTextField(20);
        panel.add(regEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        regPhoneField = new JTextField(20);
        panel.add(regPhoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton createAccountBtn = new JButton("Create Account");
        panel.add(createAccountBtn, gbc);

        gbc.gridy++;
        regFeedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        regFeedbackLabel.setForeground(Color.GRAY);
        panel.add(regFeedbackLabel, gbc);

        gbc.gridy++;
        JButton switchToLogin = new JButton("Already have an account? Login here");
        switchToLogin.setBorderPainted(false);
        switchToLogin.setContentAreaFilled(false);
        switchToLogin.setForeground(Color.BLUE);
        panel.add(switchToLogin, gbc);

        createAccountBtn.addActionListener(e -> handleRegistration());
        switchToLogin.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));
        return panel;
    }

    private void handleSendOtpAndLogin() {
        String email = loginEmailField.getText().trim();
        if (email.isEmpty()) {
            showLoginFeedback("Please enter your email address", Color.RED);
            return;
        }
        if (!isValidEmail(email)) {
            showLoginFeedback("Please enter a valid email address", Color.RED);
            return;
        }
        UserData user = UserCSVManager.findUserByEmail(email);
        if (user == null) {
            showLoginFeedback("Account not found. Please register first.", Color.RED);
            return;
        }
        currentOTP = generateOTP();
        otpGeneratedTime = System.currentTimeMillis();
        otpEmail = email;
        showLoginFeedback("Sending OTP...", Color.BLUE);
        boolean sent = EmailSender.sendOTPEmail(email, currentOTP, null);
        if (sent) {
            showLoginFeedback("OTP sent to: " + email, Color.GREEN);
            String otp = JOptionPane.showInputDialog(this, "Enter OTP sent to your email:");
            if (otp == null) return;
            if (otp.equals(currentOTP)) {
                showLoginFeedback("Login successful! Welcome " + user.getUserName(), Color.GREEN);
                loggedInUser = user;
                if (loginSuccessListener != null) {
                    dispose();
                    loginSuccessListener.onLoginSuccess(user);
                }
            } else {
                showLoginFeedback("Invalid OTP. Please try again.", Color.RED);
            }
        } else {
            showLoginFeedback("Failed to send OTP. Please try again.", Color.RED);
        }
    }

    private void handleRegistration() {
        String name = regUserNameField.getText().trim();
        String studentId = regStudentIdField.getText().trim();
        String faculty = (String) regFacultyCombo.getSelectedItem();
        String email = regEmailField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String userType = (String) regUserTypeCombo.getSelectedItem();
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || userType.isEmpty()) {
            showRegFeedback("Please fill in all required fields", Color.RED);
            return;
        }
        if (!isValidEmail(email)) {
            showRegFeedback("Please enter a valid email address", Color.RED);
            return;
        }
        if (UserCSVManager.findUserByEmail(email) != null) {
            showRegFeedback("An account with this email already exists", Color.RED);
            return;
        }
        if (userType.equals("Student") && studentId.isEmpty()) {
            showRegFeedback("Student ID is required for students", Color.RED);
            return;
        }
        String otp = generateOTP();
        showRegFeedback("Sending OTP for verification...", Color.BLUE);
        boolean sent = EmailSender.sendOTPEmail(email, otp, name);
        if (!sent) {
            showRegFeedback("Failed to send OTP. Please try again.", Color.RED);
            return;
        }
        String enteredOtp = JOptionPane.showInputDialog(this, "Enter OTP sent to your email:");
        if (enteredOtp == null) return;
        if (enteredOtp.equals(otp)) {
            String userId = UserCSVManager.generateNextUserId();
            UserData newUser = new UserData(
                userId, name, studentId, faculty, email, userType, phone
            );
            UserCSVManager.addUserToCSV(newUser);
            showRegFeedback("Registration completed successfully!", Color.GREEN);
            clearRegistrationForm();
            cardLayout.show(cardPanel, "LOGIN");
        } else {
            showRegFeedback("Invalid OTP. Please try again.", Color.RED);
        }
    }

    private void showLoginFeedback(String msg, Color color) {
        loginFeedbackLabel.setText(msg);
        loginFeedbackLabel.setForeground(color);
    }
    private void showRegFeedback(String msg, Color color) {
        regFeedbackLabel.setText(msg);
        regFeedbackLabel.setForeground(color);
    }
    private void clearRegistrationForm() {
        regUserNameField.setText("");
        regStudentIdField.setText("");
        regEmailField.setText("");
        regPhoneField.setText("");
        regFacultyCombo.setSelectedIndex(0);
        regUserTypeCombo.setSelectedIndex(0);
    }
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
    private String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }
    // Static methods for login state
    public static boolean isUserLoggedIn() {
        return loggedInUser != null;
    }
    public static UserData getLoggedInUser() {
        return loggedInUser;
    }
    public static void logoutUser() {
        loggedInUser = null;
    }
} 