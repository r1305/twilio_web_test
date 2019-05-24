/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import utils.Data;

/**
 *
 * @author Admin
 */
public class ReceiveMessageChatApi extends HttpServlet {

    private final String instanceId = "42642";
    private final String token = "r0ylaeoi02z8mhfo";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpClient httpClient = new DefaultHttpClient();
        JSONParser parser = new JSONParser();
        

        try (PrintWriter out = response.getWriter()) {
            String url = "http://api.chat-api.com/instance"+instanceId+"/sendMessage?token="+token;
            JSONObject sendPost = new JSONObject();
            
            String msg = request.getParameter("body");
            String hangup = request.getParameter("hangup"); //0 vivo 1 fin
            String token = request.getParameter("token");
            Data data = new Data();
            JSONObject clientInformation = data.getClientInformation(token);
            
            String phone_number = clientInformation.get("master-id").toString();

            sendPost.put("phone", phone_number);
            sendPost.put("body",msg);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate");
            httpPost.setHeader("Accept-Language", "es-ES,es;q=0.9,en;q=0.8");
            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");

            StringEntity stringEntity = new StringEntity(sendPost.toString());
            httpPost.setEntity(stringEntity);

            HttpResponse httpresponse = httpClient.execute(httpPost);

            /* Get response from Chat-api */
            try {
                HttpEntity entity = httpresponse.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");                            
                JSONObject json = (JSONObject)parser.parse(responseString);
                try{
                    out.print(json);
                }catch(Exception e){
                    out.print("* "+e.toString());
                }
            } catch (Exception e) {
                out.print("** "+e.toString());
            }
        }catch(Exception e){
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, e);
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
