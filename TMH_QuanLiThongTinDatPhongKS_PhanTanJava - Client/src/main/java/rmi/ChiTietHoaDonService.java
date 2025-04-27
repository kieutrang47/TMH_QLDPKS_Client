package rmi;

import entity.ChiTietHoaDon;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChiTietHoaDonService extends Remote {
    boolean createRandomChiTietHoaDon(int quantity) throws RemoteException;

    List<ChiTietHoaDon> getAllChiTietHoaDon() throws RemoteException;

    boolean createChiTietHoaDon(ChiTietHoaDon ctHoaDon) throws RemoteException;
}