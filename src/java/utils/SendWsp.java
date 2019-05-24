/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.json.simple.JSONObject;

/**
 *
 * @author Rogger
 */
public class SendWsp {
    public static final String ACCOUNT_SID = "ACcf613a9147320027e71a55ec13a421ab";
    public static final String AUTH_TOKEN = "92ada478f8bf130bf6dc74b2fbf5413a";
    public static final String FROM = "+14155238886";
    
    public JSONObject sendMessage(String input,String to){
        
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        JSONObject json = new JSONObject();
        String price ="";
        String error ="";
        String send ="";
        Message message;
        try{
            message= Message.creator( 
                    new com.twilio.type.PhoneNumber("whatsapp:"+to), 
                    new com.twilio.type.PhoneNumber("whatsapp:"+FROM),
                    input)      
                .create();
            price = "0.005";
            send="1";
        }catch(Exception e){
           send="0";
           error = e.toString();
           e.printStackTrace();
        }
        
        json.put("price",price);
        json.put("send",send);
        json.put("error",error);
        return json;
    }
}
