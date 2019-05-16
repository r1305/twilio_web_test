/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rogger
 */
public class SendMessage extends HttpServlet {

    public static final String ACCOUNT_SID = "ACcf613a9147320027e71a55ec13a421ab";
    public static final String AUTH_TOKEN = "92ada478f8bf130bf6dc74b2fbf5413a";
    public static final String FROM = "+14155238886";
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String input = request.getParameter("msg");
        String to = request.getParameter("phone");
        Message message = Message.creator( 
                    new com.twilio.type.PhoneNumber("whatsapp:"+to), 
                    new com.twilio.type.PhoneNumber("whatsapp:"+FROM),
                    input)      
                .create();
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.print("{\"to\":\""+to+"\",\"message\":\""+input+"\",\"sid\":\""+message.getSid()+"\")}");
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
