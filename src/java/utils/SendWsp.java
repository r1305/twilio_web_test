/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import static servlets.SendMessage.ACCOUNT_SID;
import static servlets.SendMessage.AUTH_TOKEN;
import static servlets.SendMessage.FROM;

/**
 *
 * @author Rogger
 */
public class SendWsp {
    public static final String ACCOUNT_SID = "ACcf613a9147320027e71a55ec13a421ab";
    public static final String AUTH_TOKEN = "92ada478f8bf130bf6dc74b2fbf5413a";
    public static final String FROM = "+14155238886";
    
    public boolean sendMessage(String input,String to){
        boolean send=false;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
         Message message = Message.creator( 
                    new com.twilio.type.PhoneNumber(to), 
                    new com.twilio.type.PhoneNumber("whatsapp:"+FROM),
                    input)      
                .create();
        return send;
    }
}
