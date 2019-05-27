package utils;

import keykeeper.gui.MainWindow;
import sqlite.SQLiteManager;

public class AccountUtils {

    private SQLiteManager sqLiteManager;
    private String login;
    private String password;

    public AccountUtils(SQLiteManager sqLiteManager, String login, String password) {
        this.sqLiteManager = sqLiteManager;
        this.login = login;
        this.password = password;
    }

    public Integer login() {
        Object[][] result = sqLiteManager.selectFromTableWithCondition("USERS", "login = '" + login + "'");

        if (result.length == 1 && result[0][2].toString().equals(this.password)) {
            return Integer.valueOf(result[0][0].toString());
        } else {
            return 0;
        }
    }

    public void register() {
        if (checkIfUserExists()) {
            new MainWindow().showMessageDialog("User already exists!");
        } else {
            String tableName = "USERS";
            String tableColumns = "(login, password)";
            String[] values = {login, password};

            sqLiteManager.insertIntoTable(tableName, tableColumns, values);

            new MainWindow().showMessageDialog("User created!");
        }
    }

    private boolean checkIfUserExists() {
        Object[][] result = sqLiteManager.selectFromTableWithCondition("USERS", "login = '" + login + "'");

        return result.length > 0;
    }
}
