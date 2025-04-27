package util;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PasswordResetManager {
    private static PasswordResetManager instance;
    private final ConcurrentHashMap<String, PasswordResetInfo> resetRequests;
    private final ScheduledExecutorService scheduler;
    private static final long EXPIRATION_MINUTES = 30;

    private PasswordResetManager() {
        resetRequests = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(1);

        // Định kỳ kiểm tra và xóa các yêu cầu đặt lại mật khẩu đã hết hạn
        scheduler.scheduleAtFixedRate(this::cleanupExpiredRequests, 1, 1, TimeUnit.MINUTES);
    }

    public static synchronized PasswordResetManager getInstance() {
        if (instance == null) {
            instance = new PasswordResetManager();
        }
        return instance;
    }

    public void addResetRequest(String email, String tempPassword) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
        resetRequests.put(email, new PasswordResetInfo(tempPassword, expirationTime));
    }

    public boolean isValidResetPassword(String email, String password) {
        if (email == null || password == null) {
            return false;
        }

        PasswordResetInfo info = resetRequests.get(email);
        return info != null && !info.isExpired() && info.getTempPassword().equals(password);
    }

    public void removeResetRequest(String email) {
        resetRequests.remove(email);
    }

    private void cleanupExpiredRequests() {
        LocalDateTime now = LocalDateTime.now();
        resetRequests.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private static class PasswordResetInfo {
        private final String tempPassword;
        private final LocalDateTime expirationTime;

        public PasswordResetInfo(String tempPassword, LocalDateTime expirationTime) {
            this.tempPassword = tempPassword;
            this.expirationTime = expirationTime;
        }

        public String getTempPassword() {
            return tempPassword;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }
}
