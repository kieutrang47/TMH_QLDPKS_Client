package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static volatile EntityManagerFactory emf;
    private static final Object lock = new Object();

    public static EntityManager getEntityManager() {
        if (emf == null || !emf.isOpen()) {
            synchronized (lock) {
                if (emf == null || !emf.isOpen()) {
                    System.out.println("==> Tạo mới EntityManagerFactory...");
                    try {
                        emf = Persistence.createEntityManagerFactory("publish");
                        System.out.println("==> EntityManagerFactory tạo thành công");
                    } catch (Exception e) {
                        System.err.println("==> Lỗi khi tạo EntityManagerFactory: " + e.getMessage());
                        e.printStackTrace();
                        throw new RuntimeException("Không thể khởi tạo EntityManagerFactory", e);
                    }
                }
            }
        }
        try {
            EntityManager em = emf.createEntityManager();
            System.out.println("==> EntityManager tạo thành công");
            return em;
        } catch (Exception e) {
            System.err.println("==> Lỗi khi tạo EntityManager: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể tạo EntityManager", e);
        }
    }

    public static void resetEntityManagerFactory() {
        synchronized (lock) {
            System.out.println("==> Đang reset EntityManagerFactory...");
            if (emf != null && emf.isOpen()) {
                try {
                    emf.close();
                    System.out.println("==> EntityManagerFactory cũ đã đóng");
                } catch (Exception e) {
                    System.err.println("==> Lỗi khi đóng EntityManagerFactory: " + e.getMessage());
                }
            }
            emf = null;
            try {
                emf = Persistence.createEntityManagerFactory("publish");
                System.out.println("==> EntityManagerFactory đã được reset thành công");
            } catch (Exception e) {
                System.err.println("==> Lỗi khi reset EntityManagerFactory: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Không thể reset EntityManagerFactory", e);
            }
        }
    }

    public static void shutdown() {
        synchronized (lock) {
            if (emf != null && emf.isOpen()) {
                try {
                    emf.close();
                    System.out.println("==> EntityManagerFactory đã được đóng");
                } catch (Exception e) {
                    System.err.println("==> Lỗi khi đóng EntityManagerFactory: " + e.getMessage());
                }
                emf = null;
            }
        }
    }
}