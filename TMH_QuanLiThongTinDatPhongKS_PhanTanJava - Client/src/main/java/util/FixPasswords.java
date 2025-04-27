package util;

import entity.TaiKhoan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class FixPasswords {
    public static void main(String[] args) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tr = em.getTransaction();

        try {
            tr.begin();
            TypedQuery<TaiKhoan> query = em.createQuery("SELECT tk FROM TaiKhoan tk", TaiKhoan.class);
            List<TaiKhoan> taiKhoans = query.getResultList();

            for (TaiKhoan tk : taiKhoans) {
                String currentPassword = tk.getMatKhau();
                // Kiểm tra nếu mật khẩu chưa được băm (giả sử mật khẩu băm có định dạng của BCrypt bắt đầu bằng "$2a$")
                if (currentPassword != null && !currentPassword.startsWith("$2a$")) {
                    System.out.println("Đang băm mật khẩu cho tài khoản: " + tk.getUserName());
                    String hashedPassword = PasswordUtils.hashPassword(currentPassword);
                    tk.setMatKhau(hashedPassword);
                    em.merge(tk);
                }
            }

            tr.commit();
            System.out.println("Đã cập nhật tất cả mật khẩu thành công!");
        } catch (Exception e) {
            if (tr.isActive()) {
                tr.rollback();
            }
            System.err.println("Lỗi khi cập nhật mật khẩu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
