/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.mobile;

import com.google.gson.Gson;
import dao.AssignmentDAO;
import entities.Assignment;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
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
@WebServlet(name = "AssignmentServlet", urlPatterns = {"/AssignmentServlet"})
public class AssignmentServlet extends HttpServlet {
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        if(session==null /* || session.getOperator()==null*/){
            response.sendError(401, "Authorization required");
            return;
        }
        int operatorId = 0;
        
        try ( PrintWriter out = response.getWriter()) {
            Collection<Assignment> assignments = assignmentDAO.getAssignments(operatorId);
            //Collection -> JSON
            Gson gson = new Gson();
            String jsonString = gson.toJson(assignments);
            out.print(jsonString);
        } catch (SQLException ex) {
            Logger.getLogger(AssignmentServlet.class.getName()).log(Level.SEVERE, null, ex);
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
    }

}
