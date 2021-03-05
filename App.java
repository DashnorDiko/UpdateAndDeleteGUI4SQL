package GUI.src;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    public static void main(String[] args) {
        LoginFrame frame=new LoginFrame();
        frame.setTitle("Sql Login und Management GUI");
        frame.setBounds(10,10,1200,600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);        
        frame.setResizable(true);
        frame.setVisible(true);
    }
}