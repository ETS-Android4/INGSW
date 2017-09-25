/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import dao.OperatorDAO;
import entities.Operator;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author carlo
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {
    OperatorDAO operatorDAO = new OperatorDAO();
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        if(user==null || pass==null){
            response.sendError(463, "Missing parameter");
            return;
        }
        
        Boolean log = operatorDAO.exists(user, pass, Operator.TYPE_BACKOFFICE);
        if(log==null){
            response.sendError(500, "Internal server error");
            return;
        }
        if(log == true){
            response.setStatus(200);
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(43200); //12 hours
        }else{
            response.sendError(461, "Wrong id or password");//Errore credenziali
        }    
    }  
}
