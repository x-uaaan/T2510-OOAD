import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    private static final String EMAIL_USERNAME = "universityeventmanagementoffic@gmail.com";
    private static final String EMAIL_PASSWORD = "spof wykl igbh ksyf"; // Gmail App Password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    /**
     * Sends an OTP email to the specified recipient using Jakarta Mail API.
     * Implementation follows Mailtrap best practices for Gmail SMTP.
     *
     * @param toEmail The recipient's email address.
     * @param otp The One-Time Password to send.
     * @param userName The name of the user (can be null).
     * @return true if the email was sent successfully, false otherwise.
     */
    public static boolean sendOTPEmail(String toEmail, String otp, String userName) {
        // Configure Gmail SMTP properties following Mailtrap recommendations
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        // Create Session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            // Create MimeMessage object
            Message message = new MimeMessage(session);
            
            // Set sender address with display name
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "University Event Management Office"));
            
            // Set recipient address
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            
            // Set email subject
            message.setSubject("Your One-Time Password (OTP) for University Event Management Office Verification");
            
            // Create HTML content
            String htmlContent = createEmailTemplate(otp, userName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send the email
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("❌ MessagingException occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates professional HTML email template for OTP delivery.
     * Template follows modern email design best practices.
     */
    private static String createEmailTemplate(String otp, String userName) {
        return "<!DOCTYPE html>" +
               "<html lang='en'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>OTP Verification</title>" +
               "<style>" +
               "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }" +
               ".container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden; }" +
               ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-align: center; padding: 40px 20px; }" +
               ".header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
               ".header h2 { margin: 10px 0 0 0; font-size: 18px; font-weight: 300; opacity: 0.9; }" +
               ".content { padding: 40px 30px; }" +
               ".otp-container { background: #f8fafc; border: 2px solid #e2e8f0; border-radius: 12px; padding: 30px; text-align: center; margin: 30px 0; }" +
               ".otp-label { font-size: 16px; color: #64748b; margin-bottom: 15px; }" +
               ".otp-code { font-size: 36px; font-weight: bold; color: #1e293b; letter-spacing: 8px; font-family: 'Courier New', monospace; }" +
               ".warning { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; margin: 25px 0; border-radius: 6px; }" +
               ".warning-text { color: #92400e; font-weight: 500; }" +
               ".footer { background: #f8fafc; padding: 30px; text-align: center; border-top: 1px solid #e2e8f0; color: #64748b; }" +
               ".footer strong { color: #1e293b; }" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "<div class='header'>" +
               "<h1>University Event Management Office</h1>" +
               "<h2>Email Verification Required</h2>" +
               "</div>" +
               "<div class='content'>" +
               "<p style='font-size: 18px; color: #1e293b; margin-bottom: 10px;'>Dear " + (userName != null ? userName : "User") + ",</p>" +
               "<p style='color: #64748b; line-height: 1.6;'>Thank you for using our University Event Management System. To complete your verification, please use the One-Time Password below:</p>" +
               "<div class='otp-container'>" +
               "<div class='otp-label'>Your Verification Code</div>" +
               "<div class='otp-code'>" + otp + "</div>" +
               "</div>" +
               "<div class='warning'>" +
               "<div class='warning-text'>⏰ This OTP is valid for 5 minutes only. Please do not share this code with anyone.</div>" +
               "</div>" +
               "<p style='color: #64748b; line-height: 1.6;'>Enter this code in the verification field of our application to continue. If you didn't request this verification, please ignore this email.</p>" +
               "</div>" +
               "<div class='footer'>" +
               "<p><strong>University Event Management Office</strong></p>" +
               "<p>Multimedia University<br>Email: support@mmu.edu.my</p>" +
               "<p style='font-size: 14px; margin-top: 20px;'>This is an automated message. Please do not reply to this email.</p>" +
               "</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Test method for debugging email functionality.
     * Use this to verify your email configuration works.
     */
    public static void testEmailConfiguration() {
        System.out.println("🔧 Testing Email Configuration...");
        System.out.println("📧 SMTP Host: " + SMTP_HOST);
        System.out.println("🔌 SMTP Port: " + SMTP_PORT);
        System.out.println("👤 Username: " + EMAIL_USERNAME);
        System.out.println("🔑 Password: " + (EMAIL_PASSWORD.length() > 0 ? "Configured (" + EMAIL_PASSWORD.length() + " chars)" : "NOT SET"));
        
        // Send a test email
        boolean result = sendOTPEmail("test@example.com", "123456", "Test User");
        System.out.println("✅ Test Result: " + (result ? "SUCCESS" : "FAILED"));
    }
} 