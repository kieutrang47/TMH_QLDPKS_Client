package rmi;

import entity.NhanVien;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NhanVienService extends Remote {
    String addNhanVien(NhanVien nhanVien) throws RemoteException;
    boolean updateNhanVien(NhanVien nhanVien) throws RemoteException;
    boolean deleteNhanVien(String maNhanVien) throws RemoteException;
    NhanVien findNhanVienByMa(String maNhanVien) throws RemoteException;
    List<NhanVien> getAllNhanVien() throws RemoteException;
    List<NhanVien> findNhanVienByName(String name) throws RemoteException;
    List<NhanVien> findNhanVienBySdt(String sdt) throws RemoteException;
}