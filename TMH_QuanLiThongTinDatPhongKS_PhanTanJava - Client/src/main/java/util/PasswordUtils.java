package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            System.out.println("Mật khẩu hoặc mã hóa đang rỗng...!!! ");
            throw new IllegalArgumentException("Password or hashed password cannot be null");
        }
        System.out.println("Kiểm tra mật khẩu...");
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}