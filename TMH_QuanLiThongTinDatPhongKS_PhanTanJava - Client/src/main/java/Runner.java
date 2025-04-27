//
//import commom.Common_Function;
//
//import dao.*;
//import entity.*;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityTransaction;
//import jakarta.persistence.Persistence;
//import jakarta.persistence.criteria.CriteriaBuilder;
//import jakarta.persistence.criteria.CriteriaQuery;
//import jakarta.persistence.criteria.Root;
//import net.datafaker.Faker;
//
//import java.time.LocalDate;
//
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//import java.util.Random;
//import java.util.concurrent.ThreadLocalRandom;
//
//import static dao.Phong_DAO.findPhongTrongThoiGianDat;
//
//
//public class Runner {
//
////    public static void main(String[] args) {
////
////        EntityManager em = Persistence.createEntityManagerFactory("publish").createEntityManager();
////        EntityTransaction tr = em.getTransaction();
////
////        // Test KhachHang
////        KhachHang khachHang = new KhachHang();
////        khachHang.setTenKhachHang("Nguyen Thi Khue Minh");
////        khachHang.setGioiTinh(false);
////        khachHang.setCccd("123456789012");
////        khachHang.setNgaySinh(LocalDate.of(2001, 3, 29));
////        khachHang.setSoDienThoai("0948139271");
////        khachHang.setLoaiKhachHang(true);
////        KhachHang_DAO.createKhachHang(em, tr, khachHang);
////
////        KhachHang_DAO.updateKhachHang(em, tr, "KH0016", "Nguyen Thi Khue Minh", "0968160856");
////        String soDienThoai = "0968160856";
////        KhachHang_DAO.findKhachHangBySoDienThoai(em, soDienThoai);
//        //testHoaDon(em, tr);
//        //testChiTietHoaDon(em, tr);
////        testDoAnThucUong(em, tr);
//
//
//        //testHoaDon(em, tr);
////        testChiTietHoaDon(em, tr);
////        testDoAnThucUong(em, tr);
//
////        delete("NV20230000", em, tr);
////        NhanVien_DAO.createRandomNhanVien(em,tr);
////        update("NV20230001", em, tr);
////        findEmployeeByName(em, tr, "Nguyen");
////        findEmployeeByName(em, tr, "Paris");
//        //---------------------------
////          KHACH HANG
////        KhachHang_DAO.createRandomKhachHang(em, tr);
////        KhachHang_DAO.updateKhachHang(em,tr,"KH0001","Huynh Le Hoan","0931481507");
////        String soDienThoai= "(660) 360-2942";
////        KhachHang_DAO.findKhachHangBySoDienThoai(em, soDienThoai);
//        //-------------------------------------
//        // Phieu dat phong
////        PhieuDatPhong_DAO.createRandomPhieuDatPhong(em,tr);
//        //------------------------------
//        // CHI TIET PHIEU DAT PHONG
////        ChiTietPhieuDat_DAO.taoChiTietPhieuDatMoi(em,tr);
////        ChiTietPhieuDat_DAO.timChiTietPhieuDatTheoTrangThai(em,true);
////         ChiTietPhieuDat_DAO.timPhieuDatTheoPhongVaTrangThai(em,"P205", TrangThaiPhong.TRONG);
//////          update chi tiet phieu dat
////        boolean result = ChiTietPhieuDat_DAO.updateChiTietPhieuDatPhong(em, tr, "P205",
////                LocalDate.of(2023, 12, 25), // Cập nhật ngày nhận phòng
////                LocalDate.of(2023, 12, 26), //Cập nhật ngày trả phòng
////                LocalTime.of(14, 0), // Cập nhật giờ nhận phòng
////                null, // Không cập nhật giờ trả phòng
////                true // Cập nhật trạng thái chi tiết
////        );
//        //---------------------------------
//        //PHONG
////        //Tao phong moi
////        Phong_DAO.createRandomPhong(em,tr);
////        Phong_DAO.updatePhong(em,tr,"P101",350000.0,TrangThaiPhong.DANG_BAO_TRI);
////    //tim phong theo trang thai phong
////        // Gọi hàm tìm danh sách các phòng trống
////        TrangThaiPhong trangThaiPhong = TrangThaiPhong.TRONG; // Giả sử enum có trạng thái TRONG là phòng trống
////        List<Phong> danhSachPhongTrong = Phong_DAO.findPhongByTrangThai(em, tr, trangThaiPhong);
////
////        // Hiển thị kết quả trên console
////        if (danhSachPhongTrong == null || danhSachPhongTrong.isEmpty()) {
////            System.out.println("Không có phòng nào đang trống.");
////        } else {
////            System.out.println("So luong phong theo trang thai nay:"+danhSachPhongTrong.size());
////            System.out.println("DS phong theo trang thai:"+TrangThaiPhong.TRONG);
////            for (Phong phong : danhSachPhongTrong) {
////                System.out.println("Ma Phong: "+phong.getMaPhong());
////                System.out.println("So Phong: " + phong.getSoPhong());
////                System.out.println("Gia Phong: "+phong.getGiaPhong());
////                System.out.println("Hinh anh phong"+phong.getHinhAnhPhong());
////                System.out.println("Loai phong"+phong.getLoaiPhong());
////                System.out.println("So giuong:"+phong.getSoGiuong());
////                System.out.println("Trang Thai Phong: " + phong.getTrangThaiPhong());
////
////                System.out.println("---------------------------");
////
////            }
////        }
////        // Tim phong theo loai phong
////
////        List<Phong> danhSachPhongTheoLoaiPhong = Phong_DAO.findPhongByLoaiPhong(em, tr, LoaiPhong.HAI_GIUONG_DON);
////
////        // Hiển thị kết quả trên console
////        if (danhSachPhongTheoLoaiPhong == null || danhSachPhongTheoLoaiPhong.isEmpty()) {
////            System.out.println("Không có phòng nào thuoc loai phong nay.");
////        } else {
////            System.out.println("So luong phong theo loai phong nay:"+danhSachPhongTheoLoaiPhong.size());
////            System.out.println("Danh sach cac phong theo loai phong:"+LoaiPhong.HAI_GIUONG_DON);
////            for (Phong phong : danhSachPhongTheoLoaiPhong) {
////                System.out.println("Ma Phong: "+phong.getMaPhong());
////                System.out.println("So Phong: " + phong.getSoPhong());
////                System.out.println("Gia Phong: "+phong.getGiaPhong());
////                System.out.println("Hinh anh phong"+phong.getHinhAnhPhong());
////                System.out.println("Loai phong"+phong.getLoaiPhong());
////                System.out.println("So giuong:"+phong.getSoGiuong());
////                System.out.println("Trang Thai Phong: " + phong.getTrangThaiPhong());
////                System.out.println("-------------------------");
////            }
////        }
////        // Tim phong trong thoi gian dat
////                // Tạo ngày bắt đầu và kết thúc
////        LocalDate startDate = LocalDate.of(2025, 1, 1);  // Ví dụ: 1 tháng 1 năm 2025
////        LocalDate endDate = LocalDate.of(2025, 1, 31);   // Ví dụ: 31 tháng 1 năm 2025
////
////        // Gọi hàm tìm phòng trong thời gian đặt
////        List<Phong> danhSachPhong = findPhongTrongThoiGianDat(em, tr, startDate, endDate);
////
////        // Hiển thị kết quả
////        if (danhSachPhong != null) {
////            System.out.println("\nDanh sach cac phong duoc tim theo khoang thoi gian: "+startDate + " - " + endDate);
////            for (Phong phong : danhSachPhong) {
////
////                System.out.println("Ma Phong: "+phong.getMaPhong());
////                System.out.println("So Phong: " + phong.getSoPhong());
////                System.out.println("Gia Phong: "+phong.getGiaPhong());
////                System.out.println("Hinh anh phong"+phong.getHinhAnhPhong());
////                System.out.println("Loai phong"+phong.getLoaiPhong());
////                System.out.println("So giuong:"+phong.getSoGiuong());
////                System.out.println("Trang Thai Phong: " + phong.getTrangThaiPhong());
////                System.out.println("-------------------------");
////            }
////        }
////        // Tim phong theo sdt khach hang
////        // Tìm phòng theo số điện thoại khách hàng
////
////        List<Phong> danhSachPhongTheoSDT = Phong_DAO.findPhongBySoDienThoaiKhachHang(em,soDienThoai);
////
////        // In danh sách phòng ra console
////        if (danhSachPhong != null && !danhSachPhong.isEmpty()) {
////            System.out.println("Danh sach cac phong da dat boi khach hang co so dien thoai: " + soDienThoai + ":");
////            for (Phong p : danhSachPhong) {
////                System.out.println("Ma Phong: " + p.getMaPhong());
////                System.out.println("So Phong: " + p.getSoPhong());
////                System.out.println("Gia Phong: " + p.getGiaPhong());
////                System.out.println("Hinh anh phong: " + p.getHinhAnhPhong());
////                System.out.println("Loai Phong: " + p.getLoaiPhong());
////                System.out.println("soGiuong: " + p.getSoGiuong());
////                System.out.println("Trang Thai Phong: " + p.getTrangThaiPhong());
////
////                System.out.println("---------------------------");
////            }
////        } else {
////            System.out.println("Không có phòng nào được đặt bởi khách hàng với số điện thoại " + soDienThoai);
////        }
//
//        // Gọi từng hàm test theo nhu cầu
////        testPhieuDatPhong(em, tr);
////        testDoAnThucUong(em, tr);
////        testHoaDon(em, tr);
////        testChiTietHoaDon(em, tr);
////        testTimPhongTheoTrangThai(em, tr);
////        testTimPhongTheoLoaiPhong(em, tr);
////        testTimPhongTrongThoiGian(em, tr);
////        String soDienThoai= "(660) 360-2942";
////        testTimPhongTheoSoDienThoaiKhachHang(em, soDienThoai);
//
//
////        testNhanVien(em, tr);
//       // testPhieuDatPhong(em, tr);
//    }
//
//    public static void testPhieuDatPhong(EntityManager em, EntityTransaction tr) {
////        PhieuDatPhong_DAO.createRandomPhieuDatPhong(em, tr);
////        PhieuDatPhong_DAO.findPhieuDatPhongByNhanVien("NV2025011721234408", em);
////        PhieuDatPhong_DAO.findPhieuDatPhongByMa("PD2025012318074906", em);
////        PhieuDatPhong_DAO.findPhieuDatPhongByKhachHang("KH0019", em);
////        PhieuDatPhong_DAO.deletePhieuDatPhongByMa("PD2025012318074908", em, tr);
////        PhieuDatPhong_DAO.updatePhieuDatPhong("PD2025011807451203", em, tr);
//    }
//
//    public static void testNhanVien(EntityManager em, EntityTransaction tr) {
////        NhanVien_DAO.createRandomNhanVien(em, tr);
////        NhanVien_DAO.findEmployeeByName(em,tr, "Trang");
////        NhanVien_DAO.delete("NV2025012317550709", em, tr); // không thể xóa vì liên kết với tài khoản
////        NhanVien_DAO.update("NV2025011721313900", em, tr);
////        TaiKhoan_DAO.updateMatKhau("NV2025011721313900", em, tr);
//    }
//
//    public static void testDoAnThucUong(EntityManager em, EntityTransaction tr) {
//       // DoAnThucUong_DAO.createRandomDoAnThucUong(em, tr);
//
////        try {
////            DoAnThucUong sp = new DoAnThucUong();
////            sp.setMaSanPham("DV31052415");
////            sp.setGiaBan(120000.0);
////            sp.setGiaNhap(80000.0);
////            sp.setHanSuDung(LocalDate.of(2026, 1, 1));
////            sp.setNgaySanXuat(LocalDate.now());
////            sp.setTenSanPham("Nước ngọt Coca Cola");
////            sp.setLoaiSanPham(false);
////            sp.setSoLuong(50);
//
////            boolean resultUpdate = DoAnThucUong_DAO.updateDoAnThucUong(em, tr, sp);
////            System.out.println(resultUpdate ? "Cập nhật sản phẩm thành công!" : "Cập nhật sản phẩm thất bại.");
//
////            boolean resultDelete = DoAnThucUong_DAO.deleteDoAnThucUong(em, tr, sp.getMaSanPham());
////            System.out.println(resultDelete ? "Xóa sản phẩm thành công!" : "Xóa sản phẩm thất bại hoặc không tìm thấy mã sản phẩm.");
////
////            DoAnThucUong_DAO.findDoAnThucUongByMa(em, tr, "DV04052441");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//    public static void testHoaDon(EntityManager em, EntityTransaction tr) {
//       // HoaDon_DAO.createRandomHoaDon(em, tr);
//        //HoaDon_DAO.findHoaDonByMa(em, tr, "HD010223295");
////        HoaDon_DAO.findHoaDonByNgayLap(em, tr, LocalDate.parse("2023-11-06"));
//    }
//
//    public static void testChiTietHoaDon(EntityManager em, EntityTransaction tr) {
////        ChiTietHoaDon_DAO.createRandomChiTietHoaDon(em, tr);
//    }
//
////    public static void testTimPhongTheoTrangThai(EntityManager em, EntityTransaction tr) {
////        TrangThaiPhong trangThaiPhong = TrangThaiPhong.TRONG;
////        List<Phong> danhSachPhongTrong = Phong_DAO.findPhongByTrangThai(em, tr, trangThaiPhong);
////
////        if (danhSachPhongTrong == null || danhSachPhongTrong.isEmpty()) {
////            System.out.println("Không có phòng nào đang trống.");
////        } else {
////            System.out.println("Danh sách phòng trống:");
////            for (Phong phong : danhSachPhongTrong) {
////                System.out.println(phong);
////            }
////        }
////    }
////
////    public static void testTimPhongTheoLoaiPhong(EntityManager em, EntityTransaction tr) {
////        List<Phong> danhSachPhongTheoLoaiPhong = Phong_DAO.findPhongByLoaiPhong(em, tr, LoaiPhong.HAI_GIUONG_DON);
////
////        if (danhSachPhongTheoLoaiPhong == null || danhSachPhongTheoLoaiPhong.isEmpty()) {
////            System.out.println("Không có phòng nào thuộc loại phòng này.");
////        } else {
////            System.out.println("Danh sách phòng thuộc loại hai giường đơn:");
////            for (Phong phong : danhSachPhongTheoLoaiPhong) {
////                System.out.println(phong);
////            }
////        }
////    }
////
////    public static void testTimPhongTrongThoiGian(EntityManager em, EntityTransaction tr) {
////        LocalDate startDate = LocalDate.of(2025, 1, 1);
////        LocalDate endDate = LocalDate.of(2025, 1, 31);
////
////        List<Phong> danhSachPhong = findPhongTrongThoiGianDat(em, tr, startDate, endDate);
////
////        if (danhSachPhong != null && !danhSachPhong.isEmpty()) {
////            System.out.println("Danh sách các phòng trống từ " + startDate + " đến " + endDate + ":");
////            for (Phong phong : danhSachPhong) {
////                System.out.println(phong);
////            }
////        } else {
////            System.out.println("Không có phòng nào trống trong khoảng thời gian này.");
////        }
////    }
////
////    public static void testTimPhongTheoSoDienThoaiKhachHang(EntityManager em, String soDienThoai) {
////        List<Phong> danhSachPhongTheoSDT = Phong_DAO.findPhongBySoDienThoaiKhachHang(em, soDienThoai);
////
////        if (danhSachPhongTheoSDT != null && !danhSachPhongTheoSDT.isEmpty()) {
////            System.out.println("Danh sách các phòng đã đặt bởi khách hàng có số điện thoại " + soDienThoai + ":");
////            for (Phong phong : danhSachPhongTheoSDT) {
////                System.out.println(phong);
////            }
////        } else {
////            System.out.println("Không có phòng nào được đặt bởi khách hàng với số điện thoại " + soDienThoai);
////        }
////
////    }
//}
