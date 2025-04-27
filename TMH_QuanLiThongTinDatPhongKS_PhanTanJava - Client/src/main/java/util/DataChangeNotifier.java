package util;

import java.util.ArrayList;
import java.util.List;

public class DataChangeNotifier {
    private static DataChangeNotifier instance;
    private List<Runnable> listeners = new ArrayList<>();

    private DataChangeNotifier() {}

    public static DataChangeNotifier getInstance() {
        if (instance == null) {
            instance = new DataChangeNotifier();
        }
        return instance;
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        System.out.println("DataChangeNotifier: Thông báo thay đổi dữ liệu...");
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}