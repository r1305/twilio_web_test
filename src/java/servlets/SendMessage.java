/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.ReadResponse;
import utils.SendWsp;

/**
 *
 * @author Rogger
 */
public class SendMessage extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        Integer smuserid = null;
        String token = null;
        //String host = "10.200.43.232";
        String host = "54.39.1.25";
        
        ReadResponse reader = new ReadResponse();
        JSONParser parser = new JSONParser();
        
        //Parameter received from Twilio
        String MessageSid=request.getParameter("MessageSid");
        //String SmsSid=request.getParameter("SmsSid");
        //String AccountSid=request.getParameter("AccountSid");
        //String MessagingServiceSid=request.getParameter("MessagingServiceSid");
        String To=request.getParameter("To");
        String From=request.getParameter("From");
        
        String Body=request.getParameter("Body");
        SimpleDateFormat format_fecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format_hora = new SimpleDateFormat("hh:mm:ss");
        String fecha = format_fecha.format( new Date() );
        String hora = format_hora.format( new Date() );
        long unixTime = System.currentTimeMillis() / 1000L;
        
        /**Send message to final user **/
        String from_phone_number = From.split(":")[1];
        
        //Create new connection with socket
        
        JSONObject queryJSON = new JSONObject();
        JSONObject clientInformation = new JSONObject();
        clientInformation.put("telefono", from_phone_number);
        queryJSON.put("socialmediaID", "7e832592-3814-46de-968f-de059f3a8134");
        queryJSON.put("clientInformation", clientInformation.toString());

        String query = queryJSON.toString();
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
            
            
            String sb = reader.readResponse(httpresponse);
            
            JSONObject response2 = (JSONObject)parser.parse(sb);
            smuserid = Integer.parseInt(response2.get("id").toString());
            token = (String)response2.get("uuid");
            
            /** Send Message to Asesor **/
            String uuid = token;
            String master_id=from_phone_number;            
            String sender =String.valueOf(smuserid);
            String receiver = "0";
            String time = String.valueOf((System.currentTimeMillis() / 1000L));
            
            //Make url with GET params
            URIBuilder builder = new URIBuilder("http://"+host+":8080/coreservices/pages/api/chat/exMessage");
            builder.setParameter("uuid", URLEncoder.encode(uuid,"UTF-8"));
            builder.setParameter("master-id", master_id);
            builder.setParameter("sender", sender);
            builder.setParameter("receiver", receiver);
            builder.setParameter("time", time);
            builder.setParameter("message", Body);
            builder.setCharset(Charset.forName("UTF-8"));

            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.setHeader("Content-type", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip, deflate");
            httpGet.setHeader("Accept-Language", "es-ES,es;q=0.9,en;q=0.8");
            httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
            httpresponse = httpClient.execute(httpGet);
            
            sb = reader.readResponse(httpresponse);
            JSONObject response3 = (JSONObject)parser.parse(sb);
            
            try (PrintWriter out = response.getWriter()) {
            /*Logger logger = Logger.getLogger("MyLog");  
            FileHandler fh;
            fh = new FileHandler("D:\\NetBeansProjects\\TwilioWeb\\log\\apache.log");  
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);    */       
            JSONObject json = new JSONObject();
            json.put("Fecha",fecha);
            json.put("HoraRecepcion",hora);
            json.put("NumeroServicio",To);
            json.put("TokenMensaje",MessageSid);
            json.put("NumeroDestino",To);
            json.put("NumeroOrigen",From);
            json.put("Mensaje",Body);
            json.put("Costo","0.005");
            json.put("DateTimeWic",String.valueOf(unixTime));
            //json.put("EstadoEnvio",((boolean)send.get("send")?1:0));
            json.put("EstadoLectura",0);
            json.put("smuserid",smuserid);
            json.put("token",token);
            json.put("response3",response3);
            json.put("message_type", response3.get("message").toString().split(":")[1]);
            
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
        processRequest(request, response);
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
        processRequest(request, response);
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
