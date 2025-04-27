package rmi;

import entity.KhachHang;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface KhachHangService extends Remote {
    void createKhachHang(KhachHang khachHang) throws RemoteException;
    void updateKhachHang(String maKhachHang, String tenMoi, String soDienThoaiMoi) throws RemoteException;
    List<KhachHang> findKhachHangBySoDienThoai(String soDienThoai) throws RemoteException;
    KhachHang findKhachHangByMa(String maKhachHang) throws RemoteException;
    List<KhachHang> getAllKhachHang() throws RemoteException;
    void themKhachHang(String tenKhachHang, boolean gioiTinh, String cccd, LocalDate ngaySinh, String soDienThoai, boolean loaiKhachHang) throws RemoteException;
    void capNhatTTKhachHang(String maKhachHang, String tenKhachHang, LocalDate ngaySinh, boolean gioiTinh, String cccd, String soDienThoai, boolean loaiKhachHang) throws RemoteException;
    void createRandomKhachHang(int quantity) throws RemoteException; // Phương thức mới
}