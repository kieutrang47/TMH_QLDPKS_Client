package dao;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatPhong;
import gui.FormThongTinHoaDon;

public class HoaDon_DAO {
	private Connection con;
	private Statement st;
	private ResultSet rs;
	private nhanVien_DAO nhanVien_DAO;
	private KhachHang_DAO khachHang_DAO;
	private ChiTietHoaDon_DAO chiTietHoaDon_DAO;

	public ArrayList<HoaDon> getAllTableHoaDon() throws java.sql.SQLException {
        ArrayList<HoaDon> dsHoaDon = new ArrayList<>();

        ConnectDB.getInstance();
        java.sql.Connection con = ConnectDB.getConnection();
        String sql = "SELECT * FROM HoaDon";
        java.sql.Statement statement = con.createStatement();
        java.sql.ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            String maHoaDon = rs.getString("maHoaDon");
            String maKhachHang = rs.getString("khachHang");
            
            String maNhanVien = rs.getString("nhanVien");
            LocalDate ngayLapHoaDon = rs.getDate("ngayLapHoaDon").toLocalDate();
            double thue = rs.getDouble("thue");
            String maPhieuDatPhong = rs.getString("phieuDatPhong");

            // Assuming you have appropriate constructors for KhachHang, KhuyenMai, and NhanVien
            KhachHang khachHang = new KhachHang(maKhachHang); // Replace with correct instantiation
            PhieuDatPhong phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong); // Replace with correct instantiation
            NhanVien nhanVien = new NhanVien(maNhanVien); // Replace with correct instantiation

            HoaDon hoaDon = new HoaDon(maHoaDon, ngayLapHoaDon, thue, nhanVien, khachHang, phieuDatPhong);
            dsHoaDon.add(hoaDon);
        }
        return dsHoaDon;
    }
	
	//tt hoa don
	
	public Map<String, Object> loadThongTinHoaDon(String maHoaDon) {
	    Map<String, Object> thongTinHoaDon = new HashMap<>();
	    String sql = "SELECT hd.maHoaDon, kh.tenKhachHang, kh.loaiKhachHang, kh.cccd, p.loaiPhong, p.giaPhong, p.maPhong, " +
	             "ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong, ct_pdp.gioNhanPhong, ct_pdp.gioTraPhong, " +
	             "pdp.soLuongNguoi, ct_pdp.soGio, " +
	             "SUM(datu.giaBan * cthd.soLuong) AS tongGiaBan, cthd.phuThu, cthd.ghiChu, cthd.soPhongDat, " +
	             "nv.hoTenNhanVien, " +
	             "CASE " +
	             "    WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
	             "    WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
	             "    WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
	             "    WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
	             "    ELSE CASE " +
	             "        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
	             "        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
	             "        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
	             "        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
	             "    END " +
	             "END AS tienThuePhong, " +
	             "CASE " +
	             "    WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "    WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "    WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "    WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "    ELSE CASE " +
	             "        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 + SUM(datu.giaBan * cthd.soLuong) + cthd.phuThu " +
	             "    END " +
	             "END AS tongTienPhaiTra " +
	             "FROM HoaDon hd " +
	             "JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
	             "JOIN PhieuDatPhong pdp ON hd.maPhieuDat = pdp.maPhieuDat " +
	             "JOIN ChiTietPhieuDatPhong ct_pdp ON pdp.maPhieuDat = ct_pdp.maPhieuDat " +
	             "JOIN Phong p ON ct_pdp.maPhong = p.maPhong " +
	             "JOIN KhachHang kh ON pdp.maKhachHang = kh.maKhachHang " +
	             "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien " +
	             "LEFT JOIN DoAnThucUong datu ON cthd.maDoAnThucUong = datu.maSanPham " +
	             "WHERE hd.maHoaDon = ? " +
	             "GROUP BY hd.maHoaDon, kh.tenKhachHang, kh.loaiKhachHang, kh.cccd, p.loaiPhong, p.giaPhong, p.maPhong, " +
	             "ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong, ct_pdp.gioNhanPhong, ct_pdp.gioTraPhong, " +
	             "pdp.soLuongNguoi, ct_pdp.soGio, cthd.phuThu, cthd.ghiChu, cthd.soPhongDat, nv.hoTenNhanVien;";


	    try (Connection con = ConnectDB.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, maHoaDon);
	        
	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                thongTinHoaDon.put("maHoaDon", rs.getString("maHoaDon"));
	                thongTinHoaDon.put("maPhong", rs.getString("maPhong"));
	                thongTinHoaDon.put("tenKhachHang", rs.getString("tenKhachHang"));
	                thongTinHoaDon.put("hoTenNhanVien", rs.getString("hoTenNhanVien"));
	                thongTinHoaDon.put("loaiKhachHang", rs.getString("loaiKhachHang"));
	                thongTinHoaDon.put("cccd", rs.getString("cccd"));
	                thongTinHoaDon.put("loaiPhong", rs.getString("loaiPhong"));
	                thongTinHoaDon.put("giaPhong", rs.getDouble("giaPhong"));
	                thongTinHoaDon.put("ngayNhanPhong", rs.getDate("ngayNhanPhong"));
	                thongTinHoaDon.put("ngayTraPhong", rs.getDate("ngayTraPhong"));
	                thongTinHoaDon.put("gioNhanPhong", rs.getTime("gioNhanPhong"));
	                thongTinHoaDon.put("gioTraPhong", rs.getTime("gioTraPhong"));
	                thongTinHoaDon.put("soLuongNguoi", rs.getInt("soLuongNguoi"));
	                thongTinHoaDon.put("soGio", rs.getInt("soGio"));
	                thongTinHoaDon.put("phuThu", rs.getDouble("phuThu"));
	                thongTinHoaDon.put("ghiChu", rs.getString("ghiChu"));
	                thongTinHoaDon.put("tongGiaBan", rs.getDouble("tongGiaBan"));
	                thongTinHoaDon.put("tienThuePhong", rs.getDouble("tienThuePhong")); // tiền thuê phòng bao gồm thuế
	                thongTinHoaDon.put("tongTienPhaiTra", rs.getDouble("tongTienPhaiTra")); // tổng tiền bao gồm tiền thuê phòng đã tính thuế, tiền dịch vụ và phụ thu không tính thuế
	            }
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	    return thongTinHoaDon;
	}



	public List<Map<String, Object>> loadDichVuHoaDon(String maHoaDon) {
	    List<Map<String, Object>> danhSachDichVu = new ArrayList<>();
	    String sql = "SELECT datu.tenSanPham, datu.giaBan, cthd.soLuong, (cthd.soLuong * datu.giaBan) AS tongGiaBan " +
	                 "FROM ChiTietHoaDon cthd " +
	                 "JOIN DoAnThucUong datu ON cthd.maDoAnThucUong = datu.maSanPham " +
	                 "WHERE cthd.maHoaDon = ?";
	    
	    try (Connection con = ConnectDB.getConnection();
	         PreparedStatement statement = con.prepareStatement(sql)) {
	        
	        statement.setString(1, maHoaDon);
	        
	        try (ResultSet rs = statement.executeQuery()) {
	            while (rs.next()) {
	                Map<String, Object> dichVu = new HashMap<>();
	                dichVu.put("tenSanPham", rs.getString("tenSanPham"));
	                dichVu.put("giaBan", rs.getDouble("giaBan"));
	                dichVu.put("soLuong", rs.getInt("soLuong"));
	                dichVu.put("tongGiaBan", rs.getDouble("tongGiaBan"));
	                danhSachDichVu.add(dichVu);
	            }
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	    return danhSachDichVu;
	}




	
	public ArrayList<HoaDon> getHoaDonTheoMaHoaDon(String maHoaDon) throws IOException, java.sql.SQLException {
        ArrayList<HoaDon> dsHoaDon = new ArrayList<>();

        ConnectDB.getInstance();
        java.sql.Connection con = ConnectDB.getConnection();
        PreparedStatement statement = null;

        String sql = "SELECT hd.*, nv.hoTenNhanVien, kh.tenKhachHang " +
                "FROM HoaDon hd " +
                "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien " +
                "JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang " +
                "WHERE hd.maHoaDon LIKE ? ";
        statement = con.prepareStatement(sql);
        statement.setString(1, maHoaDon);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
        	
        	
        	
            String maKhachHang = rs.getString(5);
            String maNhanVien = rs.getString(4);
            LocalDate ngayLapHoaDon = rs.getDate(2).toLocalDate();
            double thue = rs.getDouble(3);
            String maPhieuDatPhong = rs.getString(6);
            String tenNhanVien = rs.getString("hoTenNhanVien"); // Lấy tên nhân viên
            String tenKhachHang = rs.getString("tenKhachHang"); // Lấy tên khách hàng

            KhachHang khachHang = new KhachHang(maKhachHang, tenKhachHang);
            PhieuDatPhong phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong);
            NhanVien nhanVien = new NhanVien(maNhanVien, tenNhanVien);

            HoaDon hoaDon = new HoaDon(maHoaDon, ngayLapHoaDon, thue, nhanVien, khachHang, phieuDatPhong);
            dsHoaDon.add(hoaDon);

        }

        return dsHoaDon;
    }


	public boolean createHoaDon(HoaDon hd) {
	    boolean result = false;
	    Connection con = null;
	    CallableStatement stmt = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        con = ConnectDB.getConnection();

	        // Gá»i stored procedure sp_InsertHoaDon
	        String sql = "{call sp_InsertHoaDon(?, ?, ?, ?, ?)}";
	        stmt = con.prepareCall(sql);

	        // Set cÃ¡c tham sá»‘ cho stored procedure
	        stmt.setDate(1, java.sql.Date.valueOf(hd.getNgayLapHoaDon()));  // @ngayLapHoaDon
	        stmt.setDouble(2, hd.getThue());                               // @thue
	        stmt.setString(3, hd.getNhanVien().getMaNhanVien());           // @maNhanVien
	        stmt.setString(4, hd.getKhachHang().getMaKhachHang());         // @maKhachHang
	        stmt.setString(5, hd.getPhieuDatPhong().getMaPhieuDat());      // @maPhieuDat

	        // Thá»±c thi stored procedure
	        stmt.execute();

	        // Truy váº¥n láº¡i Ä‘á»ƒ láº¥y mÃ£ hÃ³a Ä‘Æ¡n vá»«a Ä‘Æ°á»£c táº¡o
	        String query = "SELECT TOP 1 maHoaDon FROM HoaDon WHERE maKhachHang = ? AND ngayLapHoaDon = ? ORDER BY maHoaDon DESC";
	        pstmt = con.prepareStatement(query);
	        pstmt.setString(1, hd.getKhachHang().getMaKhachHang());
	        pstmt.setDate(2, java.sql.Date.valueOf(hd.getNgayLapHoaDon()));

	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            String maHoaDon = rs.getString(1);
	            hd.setMaHoaDon(maHoaDon); // GÃ¡n mÃ£ hÃ³a Ä‘Æ¡n cho Ä‘á»‘i tÆ°á»£ng HoaDon
	        }

	        result = true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (stmt != null) stmt.close();
	            if (con != null) con.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return result;
	}

    public ArrayList<HoaDon> timKiemHoaDonTheoTuKhoa(String keyword) { 
        ArrayList<HoaDon> result = new ArrayList<>();
        
        // SQL query với JOIN và tìm kiếm theo cả mã và tên
        String sql = "SELECT hd.*, nv.hoTenNhanVien, kh.tenKhachHang " +
                     "FROM HoaDon hd " +
                     "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien " +
                     "JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang " +
                     "WHERE hd.maHoaDon LIKE ? " +
                     "OR CONVERT(VARCHAR, hd.ngayLapHoaDon, 23) LIKE ? " +
                     "OR hd.maNhanVien LIKE ? " +
                     "OR hd.maKhachHang LIKE ? " +
                     "OR nv.hoTenNhanVien LIKE ? " +
                     "OR kh.tenKhachHang LIKE ?";

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            String searchKeyword = "%" + keyword + "%";
            
            // Thiết lập từ khóa cho từng tham số tìm kiếm
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            stmt.setString(3, searchKeyword);
            stmt.setString(4, searchKeyword);
            stmt.setString(5, searchKeyword);
            stmt.setString(6, searchKeyword);

            ResultSet rs = stmt.executeQuery();

            // Xử lý dữ liệu trả về từ câu truy vấn
            while (rs.next()) {
                String maHoaDon = rs.getString(1);
                String maKhachHang = rs.getString(5);
                String maNhanVien = rs.getString(4);
                LocalDate ngayLapHoaDon = rs.getDate(2).toLocalDate();
                double thue = rs.getDouble(3);
                String maPhieuDatPhong = rs.getString(6);
                
                // Lấy tên nhân viên và khách hàng từ kết quả truy vấn
                String tenNhanVien = rs.getString("hoTenNhanVien");
                String tenKhachHang = rs.getString("tenKhachHang");

                KhachHang khachHang = new KhachHang(maKhachHang, tenKhachHang);
                PhieuDatPhong phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong);
                NhanVien nhanVien = new NhanVien(maNhanVien, tenNhanVien);

                HoaDon hoaDon = new HoaDon(maHoaDon, ngayLapHoaDon, thue, nhanVien, khachHang, phieuDatPhong);
                result.add(hoaDon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



 // Hàm tìm kiếm hóa đơn theo ngày lập
    public ArrayList<HoaDon> timKiemHoaDonTheoNgayLap(LocalDate fromDate, LocalDate toDate) {
        ArrayList<HoaDon> result = new ArrayList<>();
        
        // Đảm bảo fromDate <= toDate
        if (fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
        
        String sql = "SELECT hd.*, nv.hoTenNhanVien AS tenNhanVien, kh.tenKhachHang AS tenKhachHang " +
                "FROM HoaDon hd " +
                "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien " +
                "JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang " +
                "WHERE hd.ngayLapHoaDon BETWEEN ? AND ?";


        System.out.println("Querying with From Date: " + fromDate + " To Date: " + toDate); // Debug print

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDate));
            stmt.setDate(2, Date.valueOf(toDate));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String maHoaDon = rs.getString(1);
                String maKhachHang = rs.getString(5);
                String maNhanVien = rs.getString(4);
                LocalDate ngayLapHoaDon = rs.getDate(2).toLocalDate();
                double thue = rs.getDouble(3);
                String maPhieuDatPhong = rs.getString(6);
                String tenNhanVien = rs.getString("tenNhanVien"); // Lấy tên nhân viên
                String tenKhachHang = rs.getString("tenKhachHang"); // Lấy tên khách hàng

                KhachHang khachHang = new KhachHang(maKhachHang, tenKhachHang);
                PhieuDatPhong phieuDatPhong = new PhieuDatPhong(maPhieuDatPhong);
                NhanVien nhanVien = new NhanVien(maNhanVien, tenNhanVien);

                HoaDon hoaDon = new HoaDon(maHoaDon, ngayLapHoaDon, thue, nhanVien, khachHang, phieuDatPhong);
                result.add(hoaDon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Số lượng hóa đơn tìm thấy: " + result.size()); // Debug print
        return result;
    }


    
    //Thong ke
    public Map<String, Map<String, Object>> tinhDoanhThuTheoNgay(LocalDate ngay, String maNhanVien) throws SQLException {
        Map<String, Map<String, Object>> thongKe = new HashMap<>();
        String sql = "SELECT hd.maHoaDon, " +
                     "SUM((cthd.soLuong * datu.giaBan) + cthd.phuThu) AS doanhThuDichVu, " +
                     "SUM(CASE " +
                     "    WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "    WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "    WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "    WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "    ELSE CASE " +
                     "        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "    END " +
                     "END) AS doanhThuDatPhong, " +
                     "SUM((cthd.soLuong * datu.giaBan)) + " +
                     "SUM(CASE " +
                     "    WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "    WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "    WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "    WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "    ELSE CASE " +
                     "        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "    END " +
                     "END) AS tongDoanhThu " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "JOIN PhieuDatPhong pdp ON hd.maPhieuDat = pdp.maPhieuDat " +
                     "JOIN ChiTietPhieuDatPhong ct_pdp ON pdp.maPhieuDat = ct_pdp.maPhieuDat " +
                     "JOIN Phong p ON ct_pdp.maPhong = p.maPhong " +
                     "LEFT JOIN DoAnThucUong datu ON cthd.maDoAnThucUong = datu.maSanPham " +
                     "WHERE hd.ngayLapHoaDon = ? AND hd.maNhanVien = ? " +
                     "GROUP BY hd.maHoaDon";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            
            statement.setDate(1, java.sql.Date.valueOf(ngay));
            statement.setString(2, maNhanVien);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> doanhThu = new HashMap<>();
                    doanhThu.put("doanhThuDichVu", rs.getDouble("doanhThuDichVu"));
                    doanhThu.put("doanhThuDatPhong", rs.getDouble("doanhThuDatPhong"));
                    doanhThu.put("tongDoanhThu", rs.getDouble("tongDoanhThu"));
                    thongKe.put(rs.getString("maHoaDon"), doanhThu);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return thongKe;
    }


    public List<Object[]> thongKeDoanhThuTheoNam(int nam) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "WITH DoanhThuDichVuCTE AS (" +
                     "    SELECT " +
                     "        YEAR(hd.ngayLapHoaDon) AS Nam, " +
                     "        MONTH(hd.ngayLapHoaDon) AS Thang, " +
                     "        SUM(COALESCE(datu.giaBan * cthd.soLuong, 0)) AS DoanhThuDichVu, " +
                     "        SUM(COALESCE(cthd.phuThu, 0)) AS PhuThu " +
                     "    FROM HoaDon hd " +
                     "    JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "    LEFT JOIN DoAnThucUong datu ON cthd.maDoAnThucUong = datu.maSanPham " +
                     "    GROUP BY " +
                     "        YEAR(hd.ngayLapHoaDon), " +
                     "        MONTH(hd.ngayLapHoaDon) " +
                     ") " +
                     "SELECT " +
                     "    dtv.Nam, " +
                     "    dtv.Thang, " +
                     "    SUM(CASE " +
                     "        WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "        WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "        WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "        WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "        ELSE CASE " +
                     "            WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        END " +
                     "    END) AS DoanhThuLoaiPhong, " +
                     "    COALESCE(dtv.DoanhThuDichVu, 0) + COALESCE(dtv.PhuThu, 0) AS DoanhThuDichVu, " +
                     "    SUM(CASE " +
                     "        WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "        WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "        WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "        WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "        ELSE CASE " +
                     "            WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "            ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "        END " +
                     "    END) + COALESCE(dtv.DoanhThuDichVu, 0) + COALESCE(dtv.PhuThu, 0) AS TongDoanhThu " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "JOIN PhieuDatPhong pdp ON hd.maPhieuDat = pdp.maPhieuDat " +
                     "JOIN ChiTietPhieuDatPhong ct_pdp ON pdp.maPhieuDat = ct_pdp.maPhieuDat " +
                     "JOIN Phong p ON ct_pdp.maPhong = p.maPhong " +
                     "LEFT JOIN DoanhThuDichVuCTE dtv ON YEAR(hd.ngayLapHoaDon) = dtv.Nam AND MONTH(hd.ngayLapHoaDon) = dtv.Thang " +
                     "WHERE YEAR(hd.ngayLapHoaDon) = ? " +
                     "GROUP BY " +
                     "    dtv.Nam, " +
                     "    dtv.Thang, " +
                     "    dtv.DoanhThuDichVu, " +
                     "    dtv.PhuThu " +
                     "ORDER BY " +
                     "    dtv.Nam, dtv.Thang";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int thang = rs.getInt("Thang");
                    double doanhThuLoaiPhong = rs.getDouble("DoanhThuLoaiPhong");
                    double doanhThuDichVu = rs.getDouble("DoanhThuDichVu");
                    double tongDoanhThu = rs.getDouble("TongDoanhThu");
                    data.add(new Object[]{thang, doanhThuLoaiPhong, doanhThuDichVu, tongDoanhThu});
                }
            }
        }
        return data;
    }

    public List<Object[]> thongKeDoanhThuTheoNgay(int thang, int nam) throws SQLException { 
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT hd.maHoaDon, " +
                     "       hd.ngayLapHoaDon, " +
                     "       nv.hoTenNhanVien AS nhanVienPhuTrach, " +
                     "       SUM((cthd.soLuong * datu.giaBan) + cthd.phuThu) AS doanhThuDichVu, " +
                     "       SUM(CASE " +
                     "               WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "               WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "               WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "               WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "               ELSE CASE " +
                     "                        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                    END " +
                     "           END) AS doanhThuDatPhong, " +
                     "       SUM((cthd.soLuong * datu.giaBan)) + " +
                     "       SUM(CASE " +
                     "               WHEN ct_pdp.soGio <= 1 THEN p.giaPhong * ct_pdp.soGio * 1.08 " +
                     "               WHEN ct_pdp.soGio > 1 AND ct_pdp.soGio <= 2 THEN p.giaPhong * ct_pdp.soGio * 0.8 * 1.08 " +
                     "               WHEN ct_pdp.soGio > 2 AND ct_pdp.soGio <= 3 THEN p.giaPhong * ct_pdp.soGio * 0.7 * 1.08 " +
                     "               WHEN ct_pdp.soGio > 3 AND ct_pdp.soGio <= 12 THEN p.giaPhong * ct_pdp.soGio * 0.6 * 1.08 " +
                     "               ELSE CASE " +
                     "                        WHEN p.loaiPhong = 1 THEN 2400000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        WHEN p.loaiPhong = 2 THEN 4200000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        WHEN p.loaiPhong = 3 THEN 6000000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                        ELSE 3600000 * DATEDIFF(DAY, ct_pdp.ngayNhanPhong, ct_pdp.ngayTraPhong) * 1.08 " +
                     "                    END " +
                     "           END) AS tongDoanhThu " +
                     "FROM HoaDon hd " +
                     "JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "JOIN PhieuDatPhong pdp ON hd.maPhieuDat = pdp.maPhieuDat " +
                     "JOIN ChiTietPhieuDatPhong ct_pdp ON pdp.maPhieuDat = ct_pdp.maPhieuDat " +
                     "JOIN Phong p ON ct_pdp.maPhong = p.maPhong " +
                     "LEFT JOIN DoAnThucUong datu ON cthd.maDoAnThucUong = datu.maSanPham " +
                     "JOIN NhanVien nv ON hd.maNhanVien = nv.maNhanVien " +
                     "WHERE YEAR(hd.ngayLapHoaDon) = ? AND MONTH(hd.ngayLapHoaDon) = ? " +
                     "GROUP BY hd.maHoaDon, hd.ngayLapHoaDon, nv.hoTenNhanVien";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nam);  // năm
            ps.setInt(2, thang);  // tháng
            DecimalFormat df = new DecimalFormat("#,###.##");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maHoaDon = rs.getString("maHoaDon");
                    Date ngayLapHoaDon = rs.getDate("ngayLapHoaDon");
                    String nhanVienPhuTrach = rs.getString("nhanVienPhuTrach");
                    double doanhThuDichVu = rs.getDouble("doanhThuDichVu");
                    double doanhThuDatPhong = rs.getDouble("doanhThuDatPhong");
                    double tongDoanhThu = rs.getDouble("tongDoanhThu");

                    data.add(new Object[]{
                            maHoaDon,
                            ngayLapHoaDon,
                           
                            df.format(doanhThuDichVu),
                            df.format(doanhThuDatPhong),
                            df.format(tongDoanhThu),
                            nhanVienPhuTrach
                    });
                }
            }
        }
        return data;
    }






    public List<Integer> layNamCoHoaDon() throws SQLException {
        List<Integer> namList = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(ngayLapHoaDon) AS nam FROM HoaDon ORDER BY nam";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                namList.add(rs.getInt("nam"));
            }
        }
        return namList;
    }

    // Hàm lấy số lượng hóa đơn theo từng tháng cho một năm cụ thể
    public List<Object[]> thongKeSoLuongHoaDonTheoNam(int nam) throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT MONTH(ngayLapHoaDon) AS thang, COUNT(*) AS soLuongHD " +
                     "FROM HoaDon " +
                     "WHERE YEAR(ngayLapHoaDon) = ? " +
                     "GROUP BY MONTH(ngayLapHoaDon) " +
                     "ORDER BY thang";

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, nam);
            ResultSet rs = stmt.executeQuery();
            int previousMonthCount = 0;

            while (rs.next()) {
                int thang = rs.getInt("thang");
                int soLuongHD = rs.getInt("soLuongHD");

                // Tính phần trăm so với tháng trước
                double soSanhThangTruoc = previousMonthCount > 0
                        ? ((soLuongHD - previousMonthCount) / (double) previousMonthCount) * 100
                        : 0;

                result.add(new Object[]{thang, soLuongHD, soSanhThangTruoc});
                previousMonthCount = soLuongHD; // Cập nhật số lượng của tháng hiện tại
            }
        }
        return result;
    }
    
    public List<Object[]> thongKeChiTietTheoThang(int nam) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "WITH ThongKe AS (" +
                     "    SELECT FORMAT(hd.ngayLapHoaDon, 'yyyy-MM') AS ThangNam, " +
                     "           COUNT(DISTINCT hd.maHoaDon) AS SoLuongHoaDon, " +
                     "           lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(DISTINCT kh.maKhachHang) AS SoLuongKhachHang, " +
                     "           COUNT(CASE WHEN ctp.trangThaiChiTiet = 0 THEN 1 END) AS SoLuongHuy " +
                     "    FROM HoaDon hd " +
                     "    JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "    JOIN Phong p ON cthd.maPhong = p.maPhong " +
                     "    JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang " +
                     "    LEFT JOIN ChiTietPhieuDatPhong ctp ON ctp.maPhong = p.maPhong " +
                     "    WHERE YEAR(hd.ngayLapHoaDon) = ? " +
                     "    GROUP BY FORMAT(hd.ngayLapHoaDon, 'yyyy-MM'), lp.tenLoaiPhong " +
                     "), " +
                     "TongSoPhong AS (" +
                     "    SELECT lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(p.maPhong) AS TongSoPhong " +
                     "    FROM Phong p " +
                     "    LEFT JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    GROUP BY lp.tenLoaiPhong " +
                     "), " +
                     "SoLuongPhongDaDat AS (" +
                     "    SELECT FORMAT(hd.ngayLapHoaDon, 'yyyy-MM') AS ThangNam, " +
                     "           lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(DISTINCT cthd.maPhong) AS SoLuongPhongDaDat " +
                     "    FROM HoaDon hd " +
                     "    JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "    JOIN Phong p ON cthd.maPhong = p.maPhong " +
                     "    JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    WHERE YEAR(hd.ngayLapHoaDon) = ? " +
                     "    GROUP BY FORMAT(hd.ngayLapHoaDon, 'yyyy-MM'), lp.tenLoaiPhong " +
                     "), " +
                     "LoaiPhongPhoBien AS (" +
                     "    SELECT ThangNam, LoaiPhong, " +
                     "           RANK() OVER (PARTITION BY ThangNam ORDER BY COUNT(*) DESC) AS RankLoaiPhong " +
                     "    FROM ThongKe " +
                     "    GROUP BY ThangNam, LoaiPhong " +
                     ") " +
                     "SELECT tk.ThangNam, tk.SoLuongHoaDon, tk.LoaiPhong, tk.SoLuongKhachHang, tk.SoLuongHuy, " +
                     "       tsp.TongSoPhong, tsp.TongSoPhong - ISNULL(slpd.SoLuongPhongDaDat, 0) AS SoLuongPhongTrong, " +
                     "       CASE WHEN lpp.RankLoaiPhong = 1 THEN N'Phổ biến nhất' ELSE '' END AS DanhGia " +
                     "FROM ThongKe tk " +
                     "JOIN LoaiPhongPhoBien lpp ON tk.ThangNam = lpp.ThangNam AND tk.LoaiPhong = lpp.LoaiPhong " +
                     "LEFT JOIN TongSoPhong tsp ON tk.LoaiPhong = tsp.LoaiPhong " +
                     "LEFT JOIN SoLuongPhongDaDat slpd ON tk.ThangNam = slpd.ThangNam AND tk.LoaiPhong = slpd.LoaiPhong " +
                     "ORDER BY tk.ThangNam, tk.LoaiPhong";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ps.setInt(2, nam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String thangNam = rs.getString("ThangNam");
                    int soLuongHoaDon = rs.getInt("SoLuongHoaDon");
                    String loaiPhong = rs.getString("LoaiPhong");
                    int soLuongKhachHang = rs.getInt("SoLuongKhachHang");
                    int soLuongHuy = rs.getInt("SoLuongHuy");
                    int tongSoPhong = rs.getInt("TongSoPhong");
                    int soLuongPhongTrong = rs.getInt("SoLuongPhongTrong");
                    String danhGia = rs.getString("DanhGia");

                    data.add(new Object[]{thangNam, soLuongHoaDon, loaiPhong, soLuongKhachHang, soLuongHuy, tongSoPhong, soLuongPhongTrong, danhGia});
                }
            }
        }
        return data;
    }

    public List<Object[]> thongKeChiTietTheoNgay(int thang, int nam) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "WITH ThongKe AS (" +
                     "    SELECT FORMAT(hd.ngayLapHoaDon, 'yyyy-MM-dd') AS Ngay, " +
                     "           COUNT(DISTINCT hd.maHoaDon) AS SoLuongHoaDon, " +
                     "           lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(DISTINCT kh.maKhachHang) AS SoLuongKhachHang, " +
                     "           COUNT(CASE WHEN ctp.trangThaiChiTiet = 0 THEN 1 END) AS SoLuongHuy " +
                     "    FROM HoaDon hd " +
                     "    JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "    JOIN Phong p ON cthd.maPhong = p.maPhong " +
                     "    JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    JOIN KhachHang kh ON hd.maKhachHang = kh.maKhachHang " +
                     "    LEFT JOIN ChiTietPhieuDatPhong ctp ON ctp.maPhong = p.maPhong " +
                     "    WHERE MONTH(hd.ngayLapHoaDon) = ? AND YEAR(hd.ngayLapHoaDon) = ? " +
                     "    GROUP BY FORMAT(hd.ngayLapHoaDon, 'yyyy-MM-dd'), lp.tenLoaiPhong " +
                     "), " +
                     "TongSoPhong AS (" +
                     "    SELECT lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(p.maPhong) AS TongSoPhong " +
                     "    FROM Phong p " +
                     "    LEFT JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    GROUP BY lp.tenLoaiPhong " +
                     "), " +
                     "SoLuongPhongDaDat AS (" +
                     "    SELECT FORMAT(hd.ngayLapHoaDon, 'yyyy-MM-dd') AS Ngay, " +
                     "           lp.tenLoaiPhong AS LoaiPhong, " +
                     "           COUNT(DISTINCT cthd.maPhong) AS SoLuongPhongDaDat " +
                     "    FROM HoaDon hd " +
                     "    JOIN ChiTietHoaDon cthd ON hd.maHoaDon = cthd.maHoaDon " +
                     "    JOIN Phong p ON cthd.maPhong = p.maPhong " +
                     "    JOIN LoaiPhong lp ON p.loaiPhong = lp.loaiPhong " +
                     "    WHERE MONTH(hd.ngayLapHoaDon) = ? AND YEAR(hd.ngayLapHoaDon) = ? " +
                     "    GROUP BY FORMAT(hd.ngayLapHoaDon, 'yyyy-MM-dd'), lp.tenLoaiPhong " +
                     ") " +
                     "SELECT tk.Ngay, tk.SoLuongHoaDon, tk.LoaiPhong, tk.SoLuongKhachHang, tk.SoLuongHuy, " +
                     "       tsp.TongSoPhong, tsp.TongSoPhong - ISNULL(slpd.SoLuongPhongDaDat, 0) AS SoLuongPhongTrong " +
                     "FROM ThongKe tk " +
                     "LEFT JOIN TongSoPhong tsp ON tk.LoaiPhong = tsp.LoaiPhong " +
                     "LEFT JOIN SoLuongPhongDaDat slpd ON tk.Ngay = slpd.Ngay AND tk.LoaiPhong = slpd.LoaiPhong " +
                     "ORDER BY tk.Ngay, tk.LoaiPhong";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.setInt(3, thang);
            ps.setInt(4, nam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ngay = rs.getString("Ngay");
                    int soLuongHoaDon = rs.getInt("SoLuongHoaDon");
                    String loaiPhong = rs.getString("LoaiPhong");
                    int soLuongKhachHang = rs.getInt("SoLuongKhachHang");
                    int soLuongHuy = rs.getInt("SoLuongHuy");
                    int tongSoPhong = rs.getInt("TongSoPhong");
                    int soLuongPhongTrong = rs.getInt("SoLuongPhongTrong");

                    data.add(new Object[]{ngay, soLuongHoaDon, loaiPhong, soLuongKhachHang, soLuongHuy, tongSoPhong, soLuongPhongTrong});
                }
            }
        }
        return data;
    }








//    public String[] getAllThangLapHD() throws SQLException {
//		String[] list = new String[12 * getAllNamLapHD().length];
//		ConnectDB.getInstance();
//		java.sql.Connection con = ConnectDB.getConnection();
//		PreparedStatement statement = null;
//
//		try {
//			String sql = "SELECT DISTINCT \r\n"
//					+ "CONCAT(MONTH(ngayLapHoaDon), '/', YEAR(ngayLapHoaDon)) AS ThangNam , MONTH(ngayLapHoaDon), YEAR(ngayLapHoaDon)\r\n"
//					+ "FROM HoaDon \r\n" + "ORDER BY YEAR(ngayLapHoaDon) DESC, MONTH(ngayLapHoaDon) ASC";
//			statement = con.prepareStatement(sql);
//			// Thá»±c hiá»‡n cÃ¢u lá»‡nh sql tráº£ vá» Ä‘á»‘i tÆ°á»£ng ResultSet
//			ResultSet rs = statement.executeQuery();
//			// Duyá»‡t káº¿t quáº£ tráº£ vá»
//			int i = 0;
//			while (rs.next()) {// Di chuyá»ƒn con trá» xuá»‘ng báº£n ghi káº¿ tiáº¿p
//				list[i] = rs.getString("ThangNam");
//				i++;
//			}
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return list;
//	}
//    
//    public int[] getAllNamLapHD() throws SQLException {
//		int[] list = new int[5]; // Khoáº£ng 5 nÄƒm
//		ConnectDB.getInstance();
//		java.sql.Connection con = ConnectDB.getConnection();
//		PreparedStatement statement = null;
//
//		try {
//			String sql = "SELECT DISTINCT\r\n" + "    YEAR(ngayLapHoaDon) AS Thang\r\n" + "FROM\r\n" + "    HoaDon\r\n"
//					+ "ORDER BY\r\n" + "    Thang;";
//			statement = con.prepareStatement(sql);
//			
//			ResultSet rs = statement.executeQuery();
//			// Duyá»‡t káº¿t quáº£ tráº£ vá»
//			int i = 0;
//			while (rs.next()) {// Di chuyá»ƒn con trá» xuá»‘ng báº£n ghi káº¿ tiáº¿p
//				list[i] = rs.getInt(1);
//				i++;
//			}
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		return list;
//	}
//    public ArrayList<HoaDonThongKe> thongKeDoanhThuTheoNgay(Date ngay) {
//		ConnectDB.getInstance();
//		java.sql.Connection con = ConnectDB.getConnection();
//		ArrayList<HoaDonThongKe> dsHDTK = new ArrayList<HoaDonThongKe>();
//		PreparedStatement statement = null;
//		ResultSet rs;
//
//		try {
//			String sql = "SELECT \r\n"
//					+ "    HD.maHoaDon, HD.khachHang, HD.nhanVien, HD.NgayLapHoaDon, HD.tongThanhTienPhaiTra,\r\n"
//					+ "    SUM(CTHD.tongTienDichVu) AS TongTienDV,\r\n"
//					+ "    SUM(CTHD.tongTienThuePhong) AS TongTienSDPhong\r\n"
//					+ "FROM \r\n" + "    HoaDon HD\r\n" + "LEFT JOIN \r\n"
//					+ "    ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.hoaDon\r\n" + "WHERE\r\n"
//					+ "    ngayLapHoaDon = ?\r\n" + "GROUP BY \r\n"
//					+ "    HD.maHoaDon, HD.khachHang, HD.nhanVien, HD.ngayLapHoaDon, HD.tongThanhTienPhaiTra;";
//			statement = con.prepareStatement(sql);
//			statement.setDate(1, new java.sql.Date(ngay.getTime()));
//			// Thá»±c hiá»‡n cÃ¢u lá»‡nh sql tráº£ vá» Ä‘á»‘i tÆ°á»£ng ResultSet
//			rs = statement.executeQuery();
//			// Duyá»‡t káº¿t quáº£ tráº£ vá»
//			while (rs.next()) {// Di chuyá»ƒn con trá» xuá»‘ng báº£n ghi káº¿ tiáº¿p
//				String maHD = rs.getString(1);
//                                KhachHang kh = new KhachHang(rs.getString(2));
//				NhanVien nv = new NhanVien(rs.getString(3));
//                                LocalDate ngayLapHoaDon = rs.getDate(4).toLocalDate();
//                                double tongThanhTienPhaiTra = rs.getDouble(5);
//				double tongTienDV = rs.getDouble(6);
//				double tongTienPhong = rs.getDouble(7);
//
//				HoaDon hoaDon = new HoaDon(maHD, kh, nv, ngayLapHoaDon, tongThanhTienPhaiTra);
//				HoaDonThongKe hdtk = new HoaDonThongKe(hoaDon, tongTienDV, tongTienPhong);
//				dsHDTK.add(hdtk);
//			}
//		} catch (SQLException e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		} finally {
//			// ÄÃ³ng connection, statement vÃ  resultSet á»Ÿ Ä‘Ã¢y
//		}
//		return dsHDTK;
//	}

    public Double[] thongKeBieuDoLineTongDoanhThu(int namTK) throws SQLException {
		Double[] thongKeList = new Double[12];
		for (int i = 0; i < thongKeList.length; i++) {
			thongKeList[i] = (double) 0;
		}
		ConnectDB.getInstance();
		java.sql.Connection con = ConnectDB.getConnection();
		PreparedStatement statement = null;
		ResultSet rs;
		try {
//			String sql = "SELECT \r\n" + "	MONTH(ngayLapHoaDon) as Thang, YEAR(ngayLapHoaDon) as Nam,\r\n"
//					+ "    SUM(CTHD.tongTienDichVu) AS TongTienDV,\r\n"
//					+ "    SUM(CTHD.tongTienThuePhong) AS TongTienSDPhong\r\n"
//					+ "FROM \r\n" + "    HoaDon HD\r\n" + "LEFT JOIN \r\n"
//					+ "    ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.hoaDon\r\n" + "WHERE\r\n"
//					+ "   YEAR(ngayLapHoaDon) = ?\r\n" + "Group by\r\n" + "	MONTH(ngayLapHoaDon), YEAR(ngayLapHoaDon)\r\n"
//					+ "Order by\r\n" + "	YEAR(ngayLapHoaDon) DESC, MONTH(ngayLapHoaDon) DESC";
                        String sql = "SELECT \r\n" + "	MONTH(ngayLapHoaDon) as Thang, YEAR(ngayLapHoaDon) as Nam,\r\n"
					+ "    SUM(HD.tongThanhTienPhaiTra) AS TongTienDV,\r\n"
					+ "    SUM(HD.tongThanhTienPhaiTra) AS TongTienSDPhong\r\n"
					+ "FROM \r\n" + "    HoaDon HD\r\n" + "LEFT JOIN \r\n"
					+ "    ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.hoaDon\r\n" + "WHERE\r\n"
					+ "   YEAR(ngayLapHoaDon) = ?\r\n" + "Group by\r\n" + "	MONTH(ngayLapHoaDon), YEAR(ngayLapHoaDon)\r\n"
					+ "Order by\r\n" + "	YEAR(ngayLapHoaDon) DESC, MONTH(ngayLapHoaDon) DESC";
                        
                        
			statement = con.prepareStatement(sql);
			statement.setInt(1, namTK);
			// Thá»±c hiá»‡n cÃ¢u lá»‡nh sql tráº£ vá» Ä‘á»‘i tÆ°á»£ng ResultSet
			rs = statement.executeQuery();
			// Duyá»‡t káº¿t quáº£ tráº£ vá»
			while (rs.next()) {// Di chuyá»ƒn con trá» xuá»‘ng báº£n ghi káº¿ tiáº¿p
				thongKeList[rs.getInt(1) - 1] = rs.getDouble(3) + rs.getDouble(4);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return thongKeList;
	}
    
    public int[] thongKeBieuDoLineTongHoaDon(int namTK) throws SQLException {
		int[] thongKeList = new int[12];
		for (int i = 0; i < thongKeList.length; i++) {
			thongKeList[i] = 0;
		}
		ConnectDB.getInstance();
		java.sql.Connection con = ConnectDB.getConnection();
		PreparedStatement statement = null;
		ResultSet rs;
		try {
			String sql = "SELECT \r\n" + "    MONTH(ngayLapHoaDon) as Thang, \r\n" + "    YEAR(ngayLapHoaDon) as Nam,\r\n"
					+ "    SUM(CTHD.tongTienDichVu) AS TongTienDV,\r\n"
					+ "    SUM(CTHD.tongTienThuePhong) AS TongTienSDPhong,\r\n"
					+ "    COUNT(HD.maHoaDon) as SoHoaDon\r\n" + "FROM \r\n" + "    HoaDon HD\r\n" + "LEFT JOIN \r\n"
					+ "    ChiTietHoaDon CTHD ON HD.maHoaDon = CTHD.hoaDon\r\n" + "WHERE\r\n"
					+ "    YEAR(ngayLapHoaDon) = ?\r\n" + "GROUP BY\r\n" + "    MONTH(ngayLapHoaDon), YEAR(ngayLapHoaDon)\r\n"
					+ "ORDER BY\r\n" + "    YEAR(ngayLapHoaDon) DESC, MONTH(ngayLapHoaDon) DESC;";
                        
                        
			statement = con.prepareStatement(sql);
			statement.setInt(1, namTK);
			// Thá»±c hiá»‡n cÃ¢u lá»‡nh sql tráº£ vá» Ä‘á»‘i tÆ°á»£ng ResultSet
			rs = statement.executeQuery();
			// Duyá»‡t káº¿t quáº£ tráº£ vá»
			while (rs.next()) {// Di chuyá»ƒn con trá» xuá»‘ng báº£n ghi káº¿ tiáº¿p
				thongKeList[rs.getInt(1) - 1] = rs.getInt(5);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return thongKeList;
	}



}