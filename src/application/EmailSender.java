package application;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    // Thông tin email gửi đi
    private static final String FROM_EMAIL = "lamtruongnguyen2004@gmail.com";
    private static final String PASSWORD = "ehkh bmxh nbtn zpir"; 

    public static void sendOTPEmail(String toEmail, String otp) throws MessagingException {
        // Cấu hình thuộc tính SMTP cho Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");   // Yêu cầu xác thực (username/password)
        props.put("mail.smtp.starttls.enable", "true");   // Kích hoạt STARTTLS (mã hóa kết nối)
        props.put("mail.smtp.host", "smtp.gmail.com");     // Máy chủ SMTP của Gmail
        props.put("mail.smtp.port", "587");     // Cổng TLS mặc định của Gmail

        // Tạo một Session với thông tin xác thực (từ địa chỉ email và mật khẩu ứng dụng)
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
            	// Trả về thông tin đăng nhập: FROM_EMAIL và PASSWORD 
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        // Tạo email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));  // Thiết lập người gửi
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));  // Thiết lập người nhận (toEmail)
        message.setSubject("Mã OTP xác thực");
        message.setText("Mã OTP của bạn là: " + otp + "\nMã này có hiệu lực trong 60s.");
        
        try {
            Transport.send(message);
            System.out.println("Đã gửi mã OTP đến " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void sendOrderConfirmation(String toEmail, String orderDetails) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Thông tin đơn hàng của bạn");
        message.setText(orderDetails);

        Transport.send(message);
        System.out.println("Đã gửi email xác nhận đến " + toEmail);
    }
    
    
}