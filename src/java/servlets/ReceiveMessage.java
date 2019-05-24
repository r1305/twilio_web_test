/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import utils.SendWsp;

/**
 *
 * @author Admin
 */
public class ReceiveMessage extends HttpServlet {

    private final String instanceId = "42642";
    private final String token = "r0ylaeoi02z8mhfo";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        HttpClient httpClient = new DefaultHttpClient();
        SendWsp wsp = new SendWsp();
        //String uuid = request.getParameter("uuid");
        //String id2 = request.getParameter("id2");
        //String msg = request.getParameter("body");
        //String to = URLEncoder.encode(request.getParameter("to"), "UTF-8");
        
        JSONParser parser = new JSONParser();
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject json = (JSONObject)parser.parse(jb.toString());
            JSONArray jar = (JSONArray)json.get("messages");
            JSONObject messages = (JSONObject)jar.get(0);
            String[] author = messages.get("author").toString().split("@");
            String phone_number = "+"+author[0];
            String msg = (String)messages.get("body");
            String url = "http://api.chat-api.com/instance"+instanceId+"/sendMessage?token="+token;
            
            JSONObject sendPost = new JSONObject();
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
            
            JSONObject send = wsp.sendMessage(msg, phone_number);
            try (PrintWriter out = response.getWriter()) {
                JSONObject obj = new JSONObject();
                obj.put("to",phone_number);
                obj.put("body",msg);
                obj.put("error",send.get("error"));
                obj.put("SendStatus",send.get("send"));
                obj.put("token",token);
                obj.put("instanceId", json.get("instanceId"));
                obj.put("messages",json.get("messages"));
                out.print(obj);
            }
        } catch (Exception e) {
            try (PrintWriter out = response.getWriter()) {
                out.print(e.getMessage());
            }
        }
        
        
        /*JSONObject send = wsp.sendMessage(msg,to);
        try (PrintWriter out = response.getWriter()) {
            JSONObject json = new JSONObject();
            json.put("to",to);
            json.put("body",msg);
            json.put("error",send.get("error"));
            json.put("SendStatus",send.get("send"));
            
            out.print(json);
        }*/
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
