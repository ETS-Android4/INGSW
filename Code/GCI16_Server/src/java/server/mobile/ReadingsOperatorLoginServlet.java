/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.mobile;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 *
 * @author Riccardo
 */
@WebServlet(name = "ReadingsOperatorLoginServlet", urlPatterns = {"/ReadingsOperatorLoginServlet"})
public class ReadingsOperatorLoginServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //TODO codici errore
        HttpSession session = request.getSession(false);
        if(session!=null /* && operator!=null */){
            response.sendError(403, "Operator already logged in");
            return;
        }
        
        //TODO read credentials
        
        //TODO check login
        
        if(true){
            response.setStatus(200);
            request.getSession(true); //crea una nuova sessione
        }
        else{
            response.sendError(443, "Bad credentials");
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
