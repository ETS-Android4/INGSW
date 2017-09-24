package server.mobile;

import db.Database;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * Handles a ReadingsOperator request of opening a session.
 * Answers with response code 200 followed by a session cookie
 * in case of successful login and with response code 461 and 463 
 * respectively in case the user submit a wrong id or password and 
 * in case the client misses one of those parameters.
 * 
 * @author Riccardo
 */
@WebServlet(name = "ReadingsOperatorLoginServlet", urlPatterns = {"/ReadingsOperatorLogin"})
public class ReadingsOperatorLoginServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //checks parameters
        String operatorIdParameter = request.getParameter("operatorId");
        String password = request.getParameter("password");
        if(operatorIdParameter==null || password==null){
            response.sendError(463, "Missing parameter");
            return;
        }
        int operatorId = 0;
        try{
            operatorId = Integer.parseInt(operatorIdParameter);
        }catch(NumberFormatException e){
            response.sendError(464, "Bad parameter value");
            return;
        }
        
        String query = "SELECT * FROM GCI16.READINGS_OPERATOR WHERE operatorId=? AND pass=?";
        LinkedList<Object> params = new LinkedList<>();
        params.add(operatorId);
        params.add(password);
        
        boolean ok = false;
        try (ResultSet result = Database.getInstance().execQuery(query, params)) {
                ok = result.next();
        } catch (SQLException ex){
            Logger.getLogger(ReadingsOperatorLoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(ok){
            response.setStatus(200);
            // creates a new session
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(2592000); // one month
            session.setAttribute("operatorId", operatorId);
        }
        else{
            response.sendError(461, "Wrong ID or password");
        }
    }
}
