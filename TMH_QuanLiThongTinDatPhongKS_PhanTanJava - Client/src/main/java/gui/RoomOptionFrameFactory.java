package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import application.form.MainForm;
import client.RMIClient;

import entity.Phong;
import rmi.ChiTietPhieuDatService;
import rmi.PhongService;

public class RoomOptionFrameFactory {
	private static RMIClient rmiClient;
	private PhongService phongService;
	private ChiTietPhieuDatService chiTietPhieuDatService;
	private Phong phong = new Phong();
	public static JFrame createFrame(String option, String room, MainForm mainForm, GUI_QuanLiDatPhong quanLiDatPhong, FormThongTinHoaDon formTTHoaDon) throws Exception {
	    JFrame frame = new JFrame();
	    frame.setTitle(option + " - " + room);
	    frame.setSize(750, 740);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	    frame.getContentPane().removeAll(); // Đảm bảo nội dung cũ bị xóa

	    switch (option) {
	        case "Đặt phòng":
	            addDatPhongComponents(frame, room, mainForm, quanLiDatPhong);
	            break;
	        case "Nhận phòng":
	            addNhanPhongComponents(frame, room, mainForm, quanLiDatPhong);
	            break;
	        case "Trả phòng":
	            addTraPhongComponents(frame, room, quanLiDatPhong, formTTHoaDon);
	            break;
	        case "Đổi phòng":
	            addDoiPhongComponents(frame, room, mainForm, quanLiDatPhong);
	            break;
	        case "Gia hạn phòng":
	            addGiaHanPhongComponents(frame, room, mainForm, quanLiDatPhong);
	            break;
	        default:
	            addDefaultComponents(frame, room);
	    }

	    if (frame.getContentPane().getComponentCount() == 0) { // Không có thành phần nào hợp lệ
	        return null; // Trả về null nếu không có nội dung
	    }

	    return frame;
	}


    // Các phương thức bổ sung cho từng loại JFrame
	private static void addDatPhongComponents(JFrame frame, String room, MainForm mainForm, GUI_QuanLiDatPhong quanLiDatPhong) throws SQLException {
		rmiClient = new RMIClient();
	   	Phong phongKT = rmiClient.getPhongByMaPhong(room);
    	if(phongKT.getTrangThaiPhong().getMaTrangThai()==2 ) {
    		JPanel panel = new FormDatPhong(room,mainForm, quanLiDatPhong); // Panel chứa các thành phần
            // Thêm panel và button vào frame
            frame.add(panel);
    	}else {
    		JOptionPane.showMessageDialog(quanLiDatPhong, "Phòng phải ở trạng thái trống!!");
    		return;
    	}
        
        
    }

	private static void addNhanPhongComponents(JFrame frame, String room, MainForm mainForm, GUI_QuanLiDatPhong quanLiDatPhong) throws Exception {
		rmiClient = new RMIClient();
		Phong phongKT = rmiClient.getPhongByMaPhong(room);

		// Chỉ cho phép nhận phòng nếu trạng thái là "đã đặt trước" (mã trạng thái == 2) 2 hay 4
		if (phongKT.getTrangThaiPhong().getMaTrangThai() == 4) {
			JPanel panel = new FormNhanPhong(room, mainForm, quanLiDatPhong); // Panel chứa các thành phần
			frame.add(panel);
		} else {
			JOptionPane.showMessageDialog(quanLiDatPhong, "Phòng phải ở trạng thái đã đặt trước mới có thể nhận phòng!");
			return;
		}
	}

	private static void addTraPhongComponents(JFrame frame, String room, GUI_QuanLiDatPhong quanLiDatPhong, FormThongTinHoaDon formTTHoaDon) {
		try {
			// Khởi tạo RMIClient nếu chưa khởi tạo
			if (rmiClient == null) {
				rmiClient = new RMIClient();
			}

			// Tìm phòng theo mã phòng sử dụng PhongService từ RMIClient
			Phong phongKT = rmiClient.getPhongByMaPhong(room);
			if (phongKT == null) {
				JOptionPane.showMessageDialog(quanLiDatPhong, "Không tìm thấy phòng với mã: " + room, "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Kiểm tra trạng thái phòng (1 = Đang ở)
			if (phongKT.getTrangThaiPhong().getMaTrangThai() != 1) {
				JOptionPane.showMessageDialog(quanLiDatPhong, "Phòng phải ở trạng thái đã nhận!!", "Thông báo", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Tạo panel FormTraPhong và thêm vào frame
			JPanel panel = new FormTraPhong(room, quanLiDatPhong, formTTHoaDon);
			frame.add(panel);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(quanLiDatPhong, "Lỗi khi mở form trả phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}
    private static void addGiaHanPhongComponents(JFrame frame, String room,MainForm mainForm, GUI_QuanLiDatPhong quanLiDatPhong) throws SQLException {
//    	Phong phongKT = phong_DAO.timPhongTheoMaPhong(room);
//    	if(phongKT.getTrangThaiPhong().getMaTrangThai()!=1) {
//    		JOptionPane.showMessageDialog(quanLiDatPhong, "Phòng phải ở trạng thái đã nhận!!");
//    		return;
//    	}
//    	JPanel panel = new FormGiaHanPhong(room ,mainForm, quanLiDatPhong); // Panel chứa các thành phần
//        // Thêm panel và button vào frame
//        frame.add(panel);
    }
    public static void addDoiPhongComponents(JFrame frame, String room, MainForm mainForm, GUI_QuanLiDatPhong quanLiDatPhong) throws SQLException {
//        Phong phongKT = phong_DAO.timPhongTheoMaPhong(room);
//        JPanel panel = null;
//
//        if (phongKT.getTrangThaiPhong().getMaTrangThai() != 1) {
//            JOptionPane.showMessageDialog(quanLiDatPhong, "Phòng phải ở trạng thái đã nhận!!");
//        } else {
//            panel = new FormDoiPhong(room, mainForm, quanLiDatPhong);
//        }
//
//        if (panel != null) { // Chỉ thêm panel nếu nó không null
//            frame.setSize(380, 180);
//            frame.setLocationRelativeTo(null);
//            frame.add(panel);
//        }
    }


    private static void addCapNhatTTPhongComponents(JFrame frame, String room) {
    	JLabel label = new JLabel("Thực hiện thao tác Cập nhật trạng thái phòng " + room);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label);
    }

    // Trường hợp mặc định khi không có option đặc biệt
    private static void addDefaultComponents(JFrame frame, String room) {
        JLabel label = new JLabel("Thực hiện thao tác xem thông tin phòng " + room);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(label);
    }
}
