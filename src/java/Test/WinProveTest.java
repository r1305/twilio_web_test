/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import org.json.simple.JSONObject;
import utils.Data;

/**
 *
 * @author Admin
 */
public class WinProveTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Data data = new Data();
        JSONObject obj = data.getClientInformation("SOMED$274eee71-b720-4e30-b201-f7d69fabb242");
        System.out.println(obj.get("master-id"));
    }
    
}
