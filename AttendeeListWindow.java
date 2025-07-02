import java.awt.*;
import java.util.List;
import javax.swing.*;

class AttendeeListWindow extends JFrame {
    public AttendeeListWindow(EventData event) {
        setTitle("Attendees for " + event.getName());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table columns
        String[] columns = {"Username", "Email", "Pax"};
        // Fetch registrations for this event
        List<RegistrationData> registrations = RegistrationCSVManager.loadRegistrationsFromCSV();
        List<UserData> users = UserCSVManager.loadUsersFromCSV();
        java.util.List<Object[]> rowList = new java.util.ArrayList<>();
        for (RegistrationData reg : registrations) {
            if (reg.getEventId().equals(event.getEventId())) {
                // Find user info
                String username = reg.getUserId();
                String email = "";
                for (UserData user : users) {
                    if (user.getUserId().equals(reg.getUserId())) {
                        username = user.getUserName();
                        email = user.getEmail();
                        break;
                    }
                }
                rowList.add(new Object[]{username, email, reg.getPax()});
            }
        }
        Object[][] data = rowList.toArray(new Object[0][]);
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
} 