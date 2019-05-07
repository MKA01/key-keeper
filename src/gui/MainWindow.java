package gui;

import javax.swing.*;

public class MainWindow {

    private JFrame frame;
    private JButton button1;
    private JPanel panel1;

    public MainWindow() {
        frame = new JFrame("Key Keeper");
    }

    public void openWindow() {
        setWindowConfig();
    }

    private void setWindowConfig(){
        frame.setContentPane(new MainWindow().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
