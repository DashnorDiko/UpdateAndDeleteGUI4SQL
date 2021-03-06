package GUI.src;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

import com.mysql.jdbc.ResultSetMetaData;

import java.util.*;
import java.io.*; 

public class Database {
    protected String url;
    protected String username;
    protected String password;
    private Connection con;
    private Statement stmt;
    ResultSetMetaData rsmd;
    public Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public Database(){

    }
    
    public String performConn(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Fehler beim Herstellung 0");
        }
        try {
            con = DriverManager.getConnection(this.url, this.username, this.password);
            return "Connection Succesfull";
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return "Connection Failed";
        }
    }
    public ResultSet getMetaData(){
        try{
            DatabaseMetaData md = con.getMetaData();
            ResultSet rs = md.getTables(null,null,"%",null);
            return rs;
        } catch (SQLException ex){
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
        
    }
    public void fetchColumns(Object table){
        try {
            DatabaseMetaData md = con.getMetaData();
            ResultSet rs = md.getColumns(null, null, table.toString(), null);
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResultSet rs = null;
    }
    public DefaultTableModel fetchTable(Object TableName){
        try {
            stmt = con.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
                ResultSet rs = stmt.executeQuery("SELECT * FROM "+TableName.toString());
            return buildTableModel(rs);
            }
        catch (SQLException ex){
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static DefaultTableModel buildTableModel(ResultSet rs)
        throws SQLException {

    ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();

    Vector<String> columnNames = new Vector<String>();
    int columnCount = metaData.getColumnCount();
    for (int column = 1; column <= columnCount; column++) {
        columnNames.add(metaData.getColumnName(column));
    }
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    while (rs.next()) {
        Vector<Object> vector = new Vector<Object>();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            vector.add(rs.getObject(columnIndex));
        }
        data.add(vector);
    }
    return new DefaultTableModel(data, columnNames);
    }

    public void deleteRow(String tableName,int[] rows,DefaultTableModel model) {
        try{
        stmt = con.createStatement();
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
        String a = "null";
        String query=null;
        Vector<Vector> where = new Vector<Vector>();
        Integer pk_number=0;
        while (rs.next()){
            a=rs.getString("COLUMN_NAME"); 
        }
        for (int i = 0; i<rows.length;i++){
            where.add(model.getDataVector().elementAt(rows[i]));
        }
        pk_number=getPrimaryKey(model, tableName);
       
        if(rows.length != 0){
            for(int i = rows.length -1 ; i >= 0; i--){
                model.removeRow(rows[i]);
                query="delete from "+tableName+" where "+a+" = "+ where.get(i).get(pk_number); 
                stmt.executeUpdate(query);
            }
        }
        } catch (SQLException ex){
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	public void updateTable(DefaultTableModel model,int row,int col,String value,String colname,Object tableName) {
        try{
        String a = null;
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getPrimaryKeys(null, null, tableName.toString());
        while (rs.next()){
            a=rs.getString("COLUMN_NAME"); 
        }
        if( !a.equals(colname)){
            int pk = getPrimaryKey(model,tableName.toString());
            String query="UPDATE "+tableName.toString()+" SET "+colname+" = \""+ value
                        + "\" WHERE "+a+" = "+model.getValueAt(row, pk);
            System.out.println(query);
            stmt.executeUpdate(query);
        }
        }catch(Exception ex){
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public int getPrimaryKey(DefaultTableModel model,String tableName){
        try{
            stmt = con.createStatement();
            DatabaseMetaData meta = con.getMetaData();
            ResultSet rs = meta.getPrimaryKeys(null, null, tableName);
            String a = "null";
            String query=null;
            Vector<Vector> where = new Vector<Vector>();
            Integer pk_number=0;
            while (rs.next()){
                a=rs.getString("COLUMN_NAME"); 
                
            }
            where.add(model.getDataVector().elementAt(1));
            for (int i = 0; i < where.get(0).size() ; i++){
                if (model.getColumnName(i).equals(a)){
                    pk_number = i;
                }
            }
            return pk_number;
            } catch (SQLException ex){
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                return 0;
            }
    }
	public String closeConnection() {
       try{
        stmt.close();
        return "Connection closed";
       }catch(Exception ex){
        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        return "Connecion coudnt be closed";
       }
	}
}