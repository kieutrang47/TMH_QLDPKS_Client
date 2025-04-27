package rmi;

import entity.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PhongService extends Remote {
    List<Phong> loadAllPhong() throws RemoteException;
    Phong findPhongByMaPhong(String maPhong) throws RemoteException;

    List<Phong> findPhongByTrangThai(TrangThaiPhong trangThai) throws RemoteException;
    List<Phong> findPhongByLoaiPhong(LoaiPhong loaiPhong) throws RemoteException;

    List<Phong> timPhongTheoTTPhong(LoaiPhong loaiPhong, TrangThaiPhong trangThai,
                                    String ngayNhanStr, int soNgay) throws RemoteException;

    boolean updatePhong(String maPhong, Double giaPhongMoi, TrangThaiPhong trangThaiMoi) throws RemoteException;
    List<Phong> findPhongTrongThoiGianDat(LocalDate startDate, LocalDate endDate) throws RemoteException;
    List<Phong> findPhongByLoaiVaTrangThai(LoaiPhong loaiPhong, TrangThaiPhong trangThai) throws RemoteException;

    List<Phong> findPhongBySoDienThoaiKhachHang(String soDienThoai) throws RemoteException;
}
