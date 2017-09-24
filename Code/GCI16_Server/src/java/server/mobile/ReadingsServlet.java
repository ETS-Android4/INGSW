package server.mobile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.ReadingDAO;
import entities.Reading;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles operators' requests of save the readings they've done 
 * into the server.
 * Answers with response code 462 if the client does not provide a valid 
 * session cookie. Otherwise it gives response code 200 if the operation is 
 * successful, else 500.
 * 
 * @author Riccardo
 */
@WebServlet(name = "ReadingsServlet", urlPatterns = {"/Readings"})
public class ReadingsServlet extends HttpServlet {
    private final ReadingDAO readingDAO = new ReadingDAO();
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        // checks session cookie
        HttpSession session = request.getSession(false);
        if(session==null){
            response.sendError(462, "No session");
            return;
        }
        
        //JSON -> Collection
        String jsonList = request.getReader().readLine();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Reading>>(){}.getType();
        Collection<Reading> readings = gson.fromJson(jsonList, type);

        boolean saved = readingDAO.saveReadings(readings);
        if(saved)
            response.setStatus(200);
        else
            response.sendError(500, "Internal server error");
    }
}
