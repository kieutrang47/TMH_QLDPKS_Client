package rmi;

import entity.DoAnThucUong;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DoAnThucUongService extends Remote {
    boolean createRandomDoAnThucUong(int quantity) throws RemoteException;
    boolean updateDoAnThucUong(DoAnThucUong sp) throws RemoteException;
    boolean deleteDoAnThucUong(String maSanPham) throws RemoteException;
    DoAnThucUong findDoAnThucUongByMa(String maSanPham) throws RemoteException;
    List<DoAnThucUong> getAllDoAnThucUong() throws RemoteException; // Thêm phương thức mới
}