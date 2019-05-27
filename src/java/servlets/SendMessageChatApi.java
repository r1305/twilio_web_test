/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import chat.api.utils.ChatApi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Data;
import utils.ReadResponse;

/**
 *
 * @author Admin
 */
public class SendMessageChatApi extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        Long smuserid = null;
        String token = null;
        JSONParser parser = new JSONParser();
        Data data = new Data();
        
        /** GET RESPONSE FROM CHAT-API **/
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JSONObject json = (JSONObject)parser.parse(jb.toString());
        
        JSONArray jar = (JSONArray)json.get("messages");
        JSONObject messages = (JSONObject)jar.get(0);
        String[] author = messages.get("author").toString().split("@");
        String phone_number = author[0];
        String Body=(String)messages.get("body");
        boolean fromMe = (boolean)messages.get("fromMe");
        SimpleDateFormat format_fecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format_hora = new SimpleDateFormat("hh:mm:ss");
        String fecha = format_fecha.format( new Date() );
        String hora = format_hora.format( new Date() );
        long unixTime = System.currentTimeMillis() / 1000L;
        
        //Create new connection with socket
        ChatApi chat_api = new ChatApi();
        if(!fromMe){
            boolean isNewConnection = data.isNewConnection(phone_number);
            if(isNewConnection)
            {
                JSONObject connection = chat_api.createConnection(phone_number);
                smuserid = (Long)connection.get("smuserid");
                token = (String)connection.get("token");
                try {
                    Thread.sleep(500);
                    /** Send Message to Asesor **/
                    JSONObject response3 = chat_api.sendMessage(token, phone_number, smuserid, Body);

                    try (PrintWriter out = response.getWriter()) {
                        /*Logger logger = Logger.getLogger("MyLog");  
                        FileHandler fh;
                        fh = new FileHandler("D:\\NetBeansProjects\\TwilioWeb\\log\\apache.log");  
                        logger.addHandler(fh);
                        SimpleFormatter formatter = new SimpleFormatter();  
                        fh.setFormatter(formatter);    */       
                        JSONObject obj = new JSONObject();
                        obj.put("Fecha",fecha);
                        obj.put("HoraRecepcion",hora);
                        obj.put("NumeroOrigen",phone_number);
                        obj.put("Mensaje",Body);
                        obj.put("Costo","0.005");
                        obj.put("DateTimeWic",String.valueOf(unixTime));
                        obj.put("EstadoLectura",0);
                        obj.put("smuserid",smuserid);
                        obj.put("token",token);
                        obj.put("response3",response3);
                        obj.put("message_type", response3.get("message").toString().split(":")[1]);
                        obj.put("newSession",true);

                        //logger.info(json.toJSONString());
                        out.print(obj);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SendMessageChatApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                JSONObject datos = data.findByPhone(phone_number);
                token = (String)datos.get("token");
                smuserid = Long.parseLong(datos.get("smuserid").toString());
                
                try (PrintWriter out = response.getWriter()) {
                    Thread.sleep(500);
                    JSONObject response3 = chat_api.sendMessage(token, phone_number, smuserid, Body);
                    /*Logger logger = Logger.getLogger("MyLog");  
                    FileHandler fh;
                    fh = new FileHandler("D:\\NetBeansProjects\\TwilioWeb\\log\\apache.log");  
                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    fh.setFormatter(formatter);    */       
                    JSONObject obj = new JSONObject();
                    obj.put("Fecha",fecha);
                    obj.put("HoraRecepcion",hora);
                    obj.put("NumeroOrigen",phone_number);
                    obj.put("Mensaje",Body);
                    obj.put("Costo","0.005");
                    obj.put("DateTimeWic",String.valueOf(unixTime));
                    obj.put("EstadoLectura",0);
                    obj.put("smuserid",smuserid);
                    obj.put("token",token);
                    obj.put("response3",response3);
                    obj.put("message_type", response3.get("message").toString().split(":")[1]);
                    obj.put("newSession",false);
                    obj.put("receiver",data.getReceiver(token));

                    //logger.info(json.toJSONString());
                    out.print(obj);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SendMessageChatApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            data.saveMessage(token, Body, phone_number);
        }else{
            System.out.println("fromMe: False");
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(SendMessageChatApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ParseException ex) {
            Logger.getLogger(SendMessageChatApi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
