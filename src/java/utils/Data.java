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
    
    public JSONObject getClientInformation(String token){
        JSONObject json = new JSONObject();
        JSONParser parser = new JSONParser();
        SqlConnection connection = new SqlConnection();
        Connection con = connection.createConnection();
        Statement stmt;
        ResultSet rs;
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
}
