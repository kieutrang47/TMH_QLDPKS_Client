package rmi;

import dto.HoaDonDTO;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatPhong;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HoaDonService extends Remote {
    HoaDon themHoaDon(HoaDon hoaDon) throws RemoteException;

    boolean createRandomHoaDon(int quantity) throws RemoteException;
    boolean updateHoaDon(String maHoaDon, LocalDate ngayLapHoaDon, double thue, KhachHang khachHang, NhanVien nhanVien, PhieuDatPhong phieuDatPhong) throws RemoteException;
    boolean deleteHoaDon(String maHoaDon) throws RemoteException;
    HoaDon findHoaDonByMa(String maHoaDon) throws RemoteException;
    List<HoaDon> getAllHoaDon() throws RemoteException;
    List<HoaDon> findHoaDonByNgayLap(LocalDate ngayLapHoaDon) throws RemoteException;
    double calculateTotalAmount(String maHoaDon) throws RemoteException;

    List<HoaDonDTO> timKiemHoaDonTheoTuKhoa(String keyword) throws RemoteException;

    List<HoaDon> layDanhSachHoaDonBangJPA() throws RemoteException;

    List<HoaDonDTO> findHoaDonByNgayLapBetween(LocalDate from, LocalDate to) throws RemoteException;


    Map<String, Object> loadThongTinHoaDon(String maHoaDon) throws RemoteException;

    List<Map<String, Object>> loadDichVuHoaDon(String maHoaDon) throws RemoteException;
}