package rmi;

import entity.PhieuDatPhong;
import entity.KhachHang;
import entity.NhanVien;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface PhieuDatService extends Remote {
    boolean createRandomPhieuDatPhong(int quantity) throws RemoteException;

    PhieuDatPhong createPhieuDatPhong(PhieuDatPhong pd) throws RemoteException;
    PhieuDatPhong layPhieuDatTheoMa(String maPhieuDat) throws RemoteException;
    List<PhieuDatPhong> findPhieuDatPhongByKhachHang(String maKhachHang) throws RemoteException;
    List<PhieuDatPhong> findPhieuDatPhongByNhanVien(String maNhanVien) throws RemoteException;
    boolean updatePhieuDatPhong(String maPhieuDat, boolean kieuDat, LocalDate ngayDatPhong, int soLuongNguoi, double traTruoc, NhanVien nhanVien, KhachHang khachHang) throws RemoteException;
    PhieuDatPhong findPhieuDatPhongByMa(String maPhieuDat) throws RemoteException;
//    boolean deletePhieuDatPhongByMa(String maPhieuDat) throws RemoteException;
    List<PhieuDatPhong> getAllPhieuDatPhong() throws RemoteException;

    PhieuDatPhong getPhieuDatPhongByMa(String maPhieuDat) throws RemoteException;

    PhieuDatPhong layPhieuDatTheoMaKhachHangVaNgayDat(String maKH, String ngayDat) throws RemoteException;

}