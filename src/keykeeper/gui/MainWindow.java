package keykeeper.gui;

import org.apache.commons.codec.binary.Hex;
import sqlite.SQLiteManager;
import utils.*;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class MainWindow implements ActionListener {

    private SQLiteManager sqLiteManager;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel appPanel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JTable keyTable;
    private TableColumn passwordColumn;
    private JButton togglePasswordButton;
    private JTabbedPane modifyPanel;
    private JPanel addPanel;
    private JPanel editPanel;
    private JPanel deletePanel;
    private JTextField addServiceField;
    private JTextField addLoginField;
    private JTextField addPasswordField;
    private JSpinner editIdField;
    private JTextField editServiceField;
    private JButton editButton;
    private JButton addButton;
    private JSpinner removeIdField;
    private JButton deleteButton;
    private JTextField editLoginField;
    private JTextField editPasswordField;
    private boolean isPasswordShown;
    private KeyUtils keyUtils;

    public MainWindow() {
        frame = new JFrame("Key Keeper");
        sqLiteManager = SQLiteManager.getInstance();
        setListeners();
    }

    public void openWindow() {
        setWindowConfig();
        sqLiteManager.createAppTables();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == loginButton) {
            login();
        } else if (source == registerButton) {
            register();
        } else if (source == togglePasswordButton) {
            setPasswordColumnVisibility();
        } else if (source == addButton) {
            keyUtils.addKey(addLoginField.getText(), addPasswordField.getText(), addServiceField.getText());
        } else if (source == editButton) {
            keyUtils.editKey((int) editIdField.getValue(), editLoginField.getText(), editPasswordField.getText(), editServiceField.getText());
        } else if (source == deleteButton) {
            keyUtils.deleteKey((int) removeIdField.getValue());
        }
    }

    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(frame, message);
    }

    public void setPasswordColumn(TableColumn passwordColumn) {
        try {
            keyTable.removeColumn(this.passwordColumn);
            keyTable.removeColumn(passwordColumn);
        } catch (Exception ignored) {

        }

        this.passwordColumn = passwordColumn;
    }

    public void setPasswordColumnVisibility() {
        if (!isPasswordShown) {
            keyTable.addColumn(passwordColumn);
            isPasswordShown = true;
        } else {
            keyTable.removeColumn(passwordColumn);
            isPasswordShown = false;
        }
    }

    public void fixPasswordColumnVisibility() {
        if (isPasswordShown) {
            keyTable.addColumn(passwordColumn);
        } else {
            keyTable.removeColumn(passwordColumn);
        }
    }

    private void setWindowConfig() {
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setListeners() {
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
        togglePasswordButton.addActionListener(this);
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        deleteButton.addActionListener(this);
    }

    private void login() {
        String login = loginField.getText();
        char[] password = passwordField.getPassword();
        String hashedPassword = Hex.encodeHexString(hashPassword(password));

        Arrays.fill(password, '0');

        Integer userId = new AccountUtils(sqLiteManager, login, hashedPassword).login();

        if (userId != 0) {
            loginPanel.setVisible(false);
            appPanel.setVisible(true);

            keyUtils = new KeyUtils(this, sqLiteManager, userId, keyTable);

            keyUtils.getKeys();

            isPasswordShown = false;
            fixPasswordColumnVisibility();
        } else {
            showMessageDialog("Login failed");
        }
    }

    private void register() {
        if (validateForm()) {
            String login = loginField.getText();
            char[] password = passwordField.getPassword();
            String hashedPassword = Hex.encodeHexString(hashPassword(password));

            Arrays.fill(password, '0');

            new AccountUtils(sqLiteManager, login, hashedPassword).register();
        } else {
            showMessageDialog("Login and password are required!");
        }
    }

    private boolean validateForm() {
        return (!loginField.getText().isEmpty() && passwordField.getPassword().length != 0);
    }

    private byte[] hashPassword(char[] password) {
        byte[] hashedPassword;

        try {
            hashedPassword = tryToHashPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new KeyKeeperException("An error has occurred during password hashing!", ex);
        }

        return hashedPassword;
    }

    private byte[] tryToHashPassword(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, 10000, 512);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        return secretKey.getEncoded();
    }
}
