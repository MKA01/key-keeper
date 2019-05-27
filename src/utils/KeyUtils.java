package utils;

import sqlite.SQLiteManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    public KeyUtils(SQLiteManager sqLiteManager, Integer userId, JTable keyTable) {
        this.sqLiteManager = sqLiteManager;
        this.userId = userId;
        this.keyTable = keyTable;
    }

    public void getKeys() {
        Object[][] result = sqLiteManager.selectFromTable("KEYS");
        DefaultTableModel defaultTableModel = new DefaultTableModel(result, columnNames);

        keyTable.setModel(defaultTableModel);

        keyTable.getColumn("ID").setMaxWidth(2);
        keyTable.getColumn("User ID").setMaxWidth(4);

        keyTable.removeColumn(keyTable.getColumn("User ID"));
    }


}
