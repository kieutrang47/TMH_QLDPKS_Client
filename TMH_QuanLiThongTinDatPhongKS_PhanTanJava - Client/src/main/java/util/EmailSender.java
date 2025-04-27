package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.regex.Pattern;

public class EmailSender {
    // Cấu hình SMTP server
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "kieutrang060811@gmail.com";
    private static final String PASSWORD = "obsh qtmb memy bltx";

    /**
     * Gửi email
     * @param recipientEmail - Email người nhận
     * @param subject - Tiêu đề email
     * @param content - Nội dung email
     * @return true nếu gửi email thành công, false nếu thất bại
     */
    public static boolean sendEmail(String recipientEmail, String subject, String content) {
        // Kiểm tra email người nhận hợp lệ
        if (recipientEmail == null || !isValidEmail(recipientEmail)) {
            System.err.println("Email người nhận không hợp lệ: " + recipientEmail);
            return false;
        }

        // Thiết lập các thuộc tính SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true"); // Thêm chế độ debug để có thông tin chi tiết

        // Tạo phiên gửi email với thông tin xác thực
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            // Tạo email
            Message message = new MimeMessage(session);

            // Đặt người gửi
            message.setFrom(new InternetAddress("no-reply@themoonhotel.vn", "The Moon Hotel"));

            // Đặt người nhận
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Đặt tiêu đề
            message.setSubject(subject);

            // Đặt nội dung
            message.setText(content);

            // Gửi email
            Transport.send(message);

            System.out.println("Email đã được gửi thành công đến " + recipientEmail);
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            // In ra thông tin lỗi chi tiết
            System.err.println("Lỗi khi gửi email:");
            e.printStackTrace();

            // Hiển thị thông báo lỗi cụ thể
            if (e instanceof MessagingException) {
                MessagingException me = (MessagingException) e;
                Exception nextException = me.getNextException();
                if (nextException != null) {
                    System.err.println("Chi tiết lỗi bổ sung: " + nextException.getMessage());
                }
            }

            return false;
        }
    }

    /**
     * Kiểm tra email có hợp lệ không
     * @param email - Địa chỉ email cần kiểm tra
     * @return true nếu email hợp lệ, false nếu không
     */
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    // Hàm kiểm tra hoạt động
    public static void main(String[] args) {
        String recipientEmail = "cahniu520@gmail.com";
        String subject = "Kiểm tra kết nối Email";
        String content = "Đây là email kiểm tra từ hệ thống The Moon Hotel.\nNếu bạn nhận được email này, kết nối đã thành công.";

        boolean ketQua = sendEmail(recipientEmail, subject, content);
        System.out.println("Kết quả gửi email: " + (ketQua ? "Thành công" : "Thất bại"));
    }
}