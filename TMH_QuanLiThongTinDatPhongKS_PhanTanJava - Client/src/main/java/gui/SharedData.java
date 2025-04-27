package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class SharedData {
    // Lớp để lưu thông tin maPhieuDat và tiền trả trước
    public static class PhieuDatInfo {
        public String maPhieuDat;
        public double tienTraTruoc;
        public String maKhachHang;
        public String tenphong;

        public PhieuDatInfo(String maPhieuDat, double tienTraTruoc, String maKhachHang, String tenphong) {
            this.maPhieuDat = maPhieuDat;
            this.tienTraTruoc = tienTraTruoc;
            this.maKhachHang = maKhachHang;
            this.tenphong = tenphong;
        }
    }

    // Danh sách lưu trữ thông tin
    private static List<PhieuDatInfo> phieuDatList = new ArrayList<>();

    // Phương thức để lấy danh sách
    public static List<PhieuDatInfo> getPhieuDatList() {
        return phieuDatList;
    }

    // Phương thức để thêm thông tin
    public static void addPhieuDatInfo(String maPhieuDat, double tienTraTruoc, String maKhachHang, String tenphong) {
        phieuDatList.add(new PhieuDatInfo(maPhieuDat, tienTraTruoc, maKhachHang, tenphong));
    }

    // Phương thức để tải dữ liệu từ SQL và thêm vào phieuDatList
//    public static void loadPhieuDatFromDatabase() {
//        String sql = "SELECT ctpd.maPhieuDat, p.maPhong, pd.maKhachHang "
//                   + "FROM ChiTietPhieuDatPhong ctpd "
//                   + "JOIN Phong p ON ctpd.maPhong = p.maPhong "
//                   + "JOIN PhieuDatPhong pd ON ctpd.maPhieuDat = pd.maPhieuDat";
//
//        try (Connection conn = ConnectDB.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                String maPhieuDat = rs.getString("maPhieuDat");
//                String tenPhong = rs.getString("maPhong");
//                String maKhachHang = rs.getString("maKhachHang");
//                double tienTraTruoc = 0;
//
//                // Thêm thông tin vào danh sách
//                addPhieuDatInfo(maPhieuDat, tienTraTruoc, maKhachHang, tenPhong);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public static void deletePhieuDatInfo(String maPhieuDat) {
        phieuDatList.removeIf(phieuDatInfo -> phieuDatInfo.maPhieuDat.equals(maPhieuDat));
    }
    
}

