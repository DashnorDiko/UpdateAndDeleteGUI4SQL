package GUI.src;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginFrame extends JFrame implements ActionListener{
    Container container= getContentPane();
    JLabel serverLabel = new JLabel("Server :");
    JLabel userLabel = new JLabel("User :");
    JLabel databaseLabel = new JLabel("Database :");
    JLabel portLabel = new JLabel("Port :");
    JLabel passwordLabel = new JLabel("Password :");
    JLabel statusLabel = new JLabel("Please Fill all the fields");

    JTextField serverField = new JTextField("localhost");
    JTextField userField = new JTextField("root");
    JTextField databaseField = new JTextField("");
    JTextField portField = new JTextField("3306");
    JPasswordField passwordField = new JPasswordField();

    
    JButton connect = new JButton("Connect");
    JButton disconnect = new JButton("Disconnect");
    JButton delete = new JButton("Delete Selected Row");
    JComboBox  tableList=new JComboBox(); 
    JCheckBox showPassword = new JCheckBox("Show Password");
    
    DefaultTableModel model = new DefaultTableModel();
    JTable table = new JTable();
    JScrollPane tblJScrollPane = new JScrollPane(table);
    private Database db=new Database();
    LoginFrame () {
        
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        
    }
    
    private void addActionEvent() {
        connect.addActionListener(this);
        disconnect.addActionListener(this);
        showPassword.addActionListener(this);
        tableList.addActionListener(this);
        delete.addActionListener(this);
       
    }

    private void addComponentsToContainer() {
        container.add(serverLabel);
        container.add(userLabel);
        container.add(databaseLabel);
        container.add(portLabel);
        container.add(passwordLabel);

        container.add(serverField);
        container.add(userField);
        container.add(databaseField);
        container.add(portField);
        container.add(passwordField);

        container.add(connect);
        container.add(disconnect);
        container.add(tableList);

        container.add(showPassword);
        container.add(statusLabel);
        container.add(tblJScrollPane);  
        container.add(delete);
    }

    private void setLocationAndSize() {
        serverLabel.setBounds(10,10,100,30);
        serverField.setBounds(70,10,100,30);
        userLabel.setBounds(170+20,10,100,30);
        userField.setBounds(170+70,10,100,30);
        databaseLabel.setBounds(170+70+100+20,10,100,30);
        databaseField.setBounds(370+70+10,10,100,30);
        connect.setBounds(800,10,140,30);
        delete.setBounds(1000,10,240,30);
        portLabel.setBounds(10,60,100,30);
        portField.setBounds(70,60,100,30);
        passwordLabel.setBounds(170+20,60,100,30);
        passwordField.setBounds(170+100,60,100,30);
        tableList.setBounds(370+70+10,60,130,30);
        disconnect.setBounds(800,60,140,30);
        showPassword.setBounds(170+100,100,200,30);
        statusLabel.setBounds(470,100,400,30);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.GREEN);
        tblJScrollPane.setBounds(10,200,1050,350);
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == disconnect){
            statusLabel.setText(db.closeConnection());
        }
        if(e.getSource() == connect){
            
            String url="jdbc:mysql://"+serverField.getText()+":";
            url=url+portField.getText()+"/"+databaseField.getText(); 
            String password=new String(passwordField.getPassword());
            String userName=userField.getText();
            db.url=url;
            db.password=password;
            db.username=userName;
            statusLabel.setText(db.performConn());
            addItemsToTableList(db.getMetaData());
            db.fetchColumns(tableList.getSelectedItem());
            
        }
        if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            } 
        }
        if(e.getSource() == tableList ){
            model=db.fetchTable(tableList.getSelectedItem());
            table.setModel(model);
            
        }
        if(e.getSource()  == delete){
            db.deleteRow(tableList.getSelectedItem().toString(),table.getSelectedRows(),model);
        }
        model.addTableModelListener(
            new TableModelListener() {
                public void tableChanged(TableModelEvent evt) {
                    int row=table.getSelectedRow();
                    int col=table.getSelectedColumn();
                    db.updateTable(model,row,col,(String)table.getModel().getValueAt(row, col)
                    ,table.getModel().getColumnName(col),tableList.getSelectedItem());
                }
        });
    }
    private void addItemsToTableList(ResultSet rs) {
        try {
            // shto nji if qe shef a jan njisoj si ato qe jan nqs po bani exit
            ArrayList<String> a= new ArrayList<String>();
            while(rs.next()){
                a.add(rs.getString(3));
            }
            tableList.removeAllItems();
            for(int i = 0; i< a.size();i++){
                System.out.println(a.get(i));
                System.out.println(tableList.getItemAt(i));
                if(a.get(i) != tableList.getItemAt(i)){
                    tableList.addItem(a.get(i));
                }
            }
        } catch (SQLException ex){
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}