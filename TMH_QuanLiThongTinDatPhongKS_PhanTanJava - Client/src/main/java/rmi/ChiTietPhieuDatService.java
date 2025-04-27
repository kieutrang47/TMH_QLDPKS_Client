package rmi;

import dto.ChiTietPhieuDatDTO;
import entity.ChiTietPhieuDat;
import entity.PhieuDatPhong;
import entity.TrangThaiPhong;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ChiTietPhieuDatService extends Remote {
    boolean taoChiTietPhieuDatMoi() throws RemoteException;
    boolean createCTPhieuDat(ChiTietPhieuDat CTPD) throws RemoteException;


    boolean createCTPhieuDat(ChiTietPhieuDatDTO CTPD) throws RemoteException;


    List<PhieuDatPhong> timPhieuDatTheoPhongVaTrangThai(String maPhong, TrangThaiPhong trangThaiPhong) throws RemoteException;
    List<ChiTietPhieuDat> timChiTietPhieuDatTheoTrangThai(boolean trangThaiChiTiet) throws RemoteException;
    ChiTietPhieuDat findChiTietPhieuDatDangO(String maPhong) throws RemoteException;


//    boolean updateChiTietPhieuDatPhong(String maPhong, LocalDate ngayNhanPhong, LocalDate ngayTraPhong, LocalTime gioNhanPhong, LocalTime gioTraPhong, Boolean trangThaiChiTiet) throws RemoteException;
boolean updateChiTietPhieuDatPhong(
        Long maChiTietPhieuDat,
        LocalDate ngayTraPhong,
        LocalTime gioTraPhong,
        Boolean trangThaiChiTiet
) throws RemoteException;

//     boolean updateChiTietPhieuDatPhong(ChiTietPhieuDat chiTietPhieuDat) throws RemoteException;

    List<ChiTietPhieuDat> findPhongDaDat() throws RemoteException;

    List<ChiTietPhieuDat> findPhongTheoTrangThaiDaDat() throws RemoteException;

    List<ChiTietPhieuDat> findPhongTrongThoiGianDat() throws RemoteException;

    List<ChiTietPhieuDat> findPhongTheoTrangThaiDaDatQuaHan() throws RemoteException;
}