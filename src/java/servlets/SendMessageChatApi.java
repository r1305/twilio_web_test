/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.ReadResponse;

/**
 *
 * @author Admin
 */
public class SendMessageChatApi extends HttpServlet {
    private final String instanceId = "42642";
    private final String token_chat_api = "r0ylaeoi02z8mhfo";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        Long smuserid = null;
        String token = null;
        //String host = "10.200.43.232";
        String host = "54.39.1.25";
        JSONParser parser = new JSONParser();      
        
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
        /**Send message to final user **/
        JSONObject json = (JSONObject)parser.parse(jb.toString());
        JSONArray jar = (JSONArray)json.get("messages");
        JSONObject messages = (JSONObject)jar.get(0);
        String[] author = messages.get("author").toString().split("@");
        String phone_number = author[0];
        String Body=(String)messages.get("body");
        SimpleDateFormat format_fecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format_hora = new SimpleDateFormat("hh:mm:ss");
        String fecha = format_fecha.format( new Date() );
        String hora = format_hora.format( new Date() );
        long unixTime = System.currentTimeMillis() / 1000L;
        
        //Create new connection with socket
        
        JSONObject queryJSON = new JSONObject();
        JSONObject clientInformation = new JSONObject();
        clientInformation.put("master-id", phone_number);
        queryJSON.put("socialmediaID", "7e832592-3814-46de-968f-de059f3a8134");
        queryJSON.put("tag", "WSP");
        queryJSON.put("clientInformation", clientInformation.toString());

        String query = queryJSON.toString();
        System.out.println("query"+query);
        String url = "http://" + host + ":8080/coreservices/pages/api/user/requesting/createSMUserEx";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        httpPost.setHeader("Accept-Language", "es-ES,es;q=0.9,en;q=0.8");
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        try {
            StringEntity stringEntity = new StringEntity(query);
            httpPost.getRequestLine();
            httpPost.setEntity(stringEntity);
            HttpResponse httpresponse = httpClient.execute(httpPost);
            
            ReadResponse reader2 = new ReadResponse();
            String sb = reader2.readResponse(httpresponse);
            
            JSONObject response2 = (JSONObject)parser.parse(sb);
            smuserid = (Long)response2.get("id");
            token = (String)response2.get("uuid");
            
            Thread.sleep(500);
            /** Send Message to Asesor **/
            String uuid = token;
            String master_id=phone_number;            
            Long sender =smuserid;
            Long receiver = 0L;
            String time = String.valueOf((System.currentTimeMillis() / 1000L));
            
            //Make url with GET params
            URIBuilder builder = new URIBuilder("http://"+host+":8080/coreservices/pages/api/chat/exMessage");
            builder.setParameter("uuid", uuid);
            builder.setParameter("master-id", master_id);
            builder.setParameter("sender", String.valueOf(sender));
            builder.setParameter("receiver", String.valueOf(receiver));
            builder.setParameter("time", time);
            builder.setParameter("message", Body);
            builder.setCharset(Charset.forName("UTF-8"));
            System.out.println("url: "+builder.build());

            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
            httpGet.setHeader("Accept-Language", "es-ES,es;q=0.9,en;q=0.8");
            httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
            httpresponse = httpClient.execute(httpGet);
            System.out.println("httpresponse"+httpresponse);
            
            sb = reader2.readResponse(httpresponse);
            JSONObject response3 = (JSONObject)parser.parse(sb);
            
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
              
            //logger.info(json.toJSONString());
            
            out.print(json);
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
