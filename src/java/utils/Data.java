/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.fasterxml.jackson.core.JsonParseException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Admin
 */
public class Data {
    Statement stmt = null;
    ResultSet rs = null;
    Connection con = null;
    public JSONObject getClientInformation(String token){
        JSONObject json = new JSONObject();
        JSONParser parser = new JSONParser();
        SqlConnection connection = new SqlConnection();
        con = connection.createConnection();
        try {
            stmt = con.createStatement();
            String sql = "SELECT clientInformation FROM smtbusersession WHERE uuid='"+token+"'";
            rs =stmt.executeQuery(sql);
            while(rs.next()){
                String clientInformation = rs.getString(1);
                json = (JSONObject)parser.parse(clientInformation);                
            }
            con.close();
            rs.close();
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return json;
    }
    
    public boolean isNewConnection(String phone){
        boolean isNew = false;
        JSONObject json = new JSONObject();
        json.put("master-id",phone);
        SqlConnection connection = new SqlConnection();
        con = connection.createConnection();
        int cont=0;
        try {
            stmt = con.createStatement();
            String sql = "SELECT endSession FROM smtbusersession WHERE clientInformation='"+json+"' AND endSession is null";
            rs =stmt.executeQuery(sql);
            while(rs.next()){
                cont++;                
            }
            if(cont==0)
                isNew=true;
            
            con.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isNew;
    }
    
    public JSONObject findByPhone(String phone_number){
        JSONObject obj = new JSONObject();
        JSONObject clientInformation = new JSONObject();
        clientInformation.put("master-id",phone_number);
        SqlConnection connection = new SqlConnection();
        con = connection.createConnection();
        try {
            stmt = con.createStatement();
            String sql = "SELECT uuid,userIdSession FROM smtbusersession WHERE clientInformation='"+clientInformation+"' AND endSession is null";
            rs =stmt.executeQuery(sql);
            while(rs.next()){
                obj.put("smuserid",rs.getInt("userIdSession"));
                obj.put("token",rs.getString("uuid"));
            }
            
            con.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }
}
