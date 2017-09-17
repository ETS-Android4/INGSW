/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Riccardo
 */
@WebServlet(name = "ReadingsServlet", urlPatterns = {"/Readings"})
public class ReadingsServlet extends HttpServlet {
    private final ReadingDAO readingDAO = new ReadingDAO();
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        if(session==null){
            response.sendError(401, "Authorization required");
            return;
        }
        
        //JSON -> Collection
        String jsonList = request.getReader().readLine();
        System.out.println(jsonList);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Reading>>(){}.getType();
        Collection<Reading> readings = gson.fromJson(jsonList, type);
        System.out.println(readings);

        boolean saved = readingDAO.saveReadings(readings);
        
        if(saved)
            response.setStatus(200);
        else
            response.sendError(500, "Internal server error");
    }
}
