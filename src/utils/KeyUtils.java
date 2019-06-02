package utils;

import keykeeper.gui.MainWindow;
import sqlite.SQLiteManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class KeyUtils {

    private final String[] columnNames = {
            "ID",
            "User ID",
            "Service",
            "Login",
            "Password"
    };
    private SQLiteManager sqLiteManager;
    private Integer userId;
    private JTable keyTable;
    private MainWindow mainWindow;

    public KeyUtils(MainWindow mainWindow, SQLiteManager sqLiteManager, Integer userId, JTable keyTable) {
        this.sqLiteManager = sqLiteManager;
        this.userId = userId;
        this.keyTable = keyTable;
        this.mainWindow = mainWindow;
    }

    public void getKeys() {
        Object[][] result = sqLiteManager.selectFromTableWithCondition("KEYS", "USER_ID = '" + userId + "'");
        DefaultTableModel defaultTableModel = new DefaultTableModel(result, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        keyTable.setCellSelectionEnabled(true);

        keyTable.setModel(defaultTableModel);

        keyTable.getColumn("ID").setMaxWidth(20);
        keyTable.getColumn("ID").setWidth(20);


        keyTable.removeColumn(keyTable.getColumn("User ID"));

        mainWindow.setPasswordColumn(keyTable.getColumn("Password"));
    }

    public void addKey(String login, String password, String service) {
        String[] values = {login, password, service, String.valueOf(userId)};

        if (login.isEmpty() && password.isEmpty() && service.isEmpty()) {
            mainWindow.showMessageDialog("Please enter values!");
            return;
        }

        sqLiteManager.insertIntoTable("KEYS", "(login, password, service, user_id)", values);
        getKeys();
        mainWindow.fixPasswordColumnVisibility();
    }

    public void editKey(int id, String login, String password, String service) {
        Map<String, Object> values = new HashMap<>();
        String keyNameCondition = "ID = '" + id + "' AND USER_ID";

        if (login.isEmpty() && password.isEmpty() && service.isEmpty()) {
            mainWindow.showMessageDialog("Please enter values!");
            return;
        }

        if (!login.isEmpty()) {
            values.put("login", login);
        }
        if (!password.isEmpty()) {
            values.put("password", password);
        }
        if (!service.isEmpty()) {
            values.put("service", service);
        }

        sqLiteManager.updateTable("KEYS", keyNameCondition, userId, values);
        getKeys();
        mainWindow.fixPasswordColumnVisibility();
    }

    public void deleteKey(int id) {
        String keyNameCondition = "ID = '" + id + "' AND USER_ID";

        sqLiteManager.deleteFromTable("KEYS", keyNameCondition, userId);
        getKeys();
        mainWindow.fixPasswordColumnVisibility();
    }
}
