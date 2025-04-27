package rmi;

import entity.TaiKhoan;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaiKhoanService extends Remote {
    boolean checkAvailability(String username) throws RemoteException;
    boolean taoTK(TaiKhoan taiKhoan) throws RemoteException;
    boolean updateMatKhau(String maNhanVien, String newMatKhau) throws RemoteException;
    String getHashedPassword(String username) throws RemoteException;
    String getEmailByUsername(String username) throws RemoteException;
    boolean kiemTraEmailTonTai(String email) throws RemoteException;
    boolean capNhatMatKhauTheoEmail(String email, String newPassword) throws RemoteException;
    TaiKhoan layTKTheoUsername(String username) throws RemoteException;
    boolean updatePassword(String username, String hashedPassword) throws RemoteException;
}