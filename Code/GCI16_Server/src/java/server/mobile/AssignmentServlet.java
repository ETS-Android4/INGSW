package server.mobile;

import com.google.gson.Gson;
import dao.AssignmentDAO;
import entities.Assignment;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles operators' requests of getting their assignments.
 * Answers with response code 462 if the client does not provide 
 * a valid session cookie, with response code 200 and a string containing 
 * all user's assignments otherwise.
 *
 * @author Riccardo
 */
@WebServlet(name = "AssignmentServlet", urlPatterns = {"/Assignments"})
public class AssignmentServlet extends HttpServlet {
    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // checks session cookie
        HttpSession session = request.getSession(false);
        if(session==null){
            response.sendError(462, "No session");
            return;
        }
        
        Integer operatorId = (Integer) session.getAttribute("operatorId");

        // sends the operator a json string containing his assignments
        try ( PrintWriter out = response.getWriter()) {
            Collection<Assignment> assignments = assignmentDAO.getAssignments(operatorId);
            if(assignments==null){
                response.sendError(500, "Internal server error");
                return;
            }
            //Collection -> JSON
            Gson gson = new Gson();
            String jsonString = gson.toJson(assignments);
            out.print(jsonString);
            response.setStatus(200);
        } catch (IOException ex) {
            Logger.getLogger(AssignmentServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(500, "Internal server error");
        }
    }
}