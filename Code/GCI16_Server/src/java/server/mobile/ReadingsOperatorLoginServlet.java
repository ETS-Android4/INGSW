/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.mobile;

import db.Database;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 *
 * @author Riccardo
 */
@WebServlet(name = "ReadingsOperatorLoginServlet", urlPatterns = {"/ReadingsOperatorLogin"})
public class ReadingsOperatorLoginServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //TODO read credentials
        int operatorId = Integer.parseInt(request.getParameter("operatorId"));
        String password = request.getParameter("password");

        //TODO codici errore
        HttpSession session = request.getSession(false);
        if(session!=null){
            Integer op = (Integer) session.getAttribute("operatorId");
            if(op==operatorId) return;
        }
        
        //TODO check login
        String query = "SELECT * FROM GCI16.READINGS_OPERATOR WHERE operatorId=? AND pass=?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(operatorId);
        params.add(password);
        boolean ok = false;
        try {
            ResultSet result = Database.getInstance().execQuery(query, params);
            ok = result.next();
            result.close();
        } catch (SQLException ex){
            Logger.getLogger(ReadingsOperatorLoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(ok){
            response.setStatus(200);
            //crea una nuova sessione
            session = request.getSession(true);
            session.setMaxInactiveInterval(2592000); //un mese
            session.setAttribute("operatorId", operatorId);
        }
        else{
            response.sendError(460, "Wrong id or password");
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
