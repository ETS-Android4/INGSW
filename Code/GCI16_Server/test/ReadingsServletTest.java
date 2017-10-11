import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.interfaces.ReadingDAO;
import entities.Assignment;
import entities.Customer;
import entities.Reading;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import server.mobile.ReadingsServlet;
/**
 *
 * @author GCI16_25
 */
public final class ReadingsServletTest extends Mockito{
    private ReadingsServlet servlet = new ReadingsServlet();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ReadingDAO readingDAO;
    private HttpSession session;
    
    private static class HttpServletResponseForTest extends HttpServletResponseWrapper{
        private int status = 200;
        private String message;
        
        public HttpServletResponseForTest() {
            super(mock(HttpServletResponse.class));
        }
        
        @Override
        public void setStatus(int status){
            this.status = status;
        }

        @Override
        public void sendError(int sc) throws IOException {
            setStatus(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            sendError(sc);
            this.message = msg;
        }

        @Override
        public int getStatus() {
            return this.status;
        }    
    } 
    
    @Before
    public void prepareTest(){
        //servlet = mock(ReadingsServlet.class);
        request = mock(HttpServletRequest.class);
        response = new HttpServletResponseForTest();
        readingDAO = mock(ReadingDAO.class);
        session = mock(HttpSession.class);
        servlet.setReadingDAO(readingDAO);
    }
    
    /*  Test 1
        Session found, JSON readings well-formed and DAO returns true */
    @Test
    public void test_okSession_okJSON_trueDAO() throws Exception{
        Gson gson = new Gson();
        Reading readingObject = new Reading(1, 1, new Date(), 1);
        LinkedList<Reading> readingCollection = new LinkedList<>();
        readingCollection.add(readingObject);
        String reading = gson.toJson(readingCollection);
        
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(reading); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(true); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(200, response.getStatus());
    }
    
    /*  Test 2
        Session found, JSON readings well-formed and DAO returns false */
    @Test
    public void test_okSession_okJSON_falseDAO() throws Exception{
        Gson gson = new Gson();
        Reading readingObject = new Reading(1, 1, new Date(), 1);
        LinkedList<Reading> readingCollection = new LinkedList<>();
        readingCollection.add(readingObject);
        String reading = gson.toJson(readingCollection);
        
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(reading); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(false); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(500, response.getStatus());
    }
    
    /*  Test 3
        Session found, no JSON readings and DAO returns true */
    @Test
    public void test_okSession_noJSON_trueDAO() throws Exception{
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(null); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(true); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(463, response.getStatus());
    }
    
    /*  Test 4
        Session found, no JSON readings and DAO returns false */
    @Test
    public void test_okSession_noJSON_falseDAO() throws Exception{
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(null); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(false); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(463, response.getStatus());
    }
    
    /*  Test 5
        Session found, not reading JSON and DAO returns true */
    @Test
    public void test_okSession_notReadingJSON_trueDAO() throws Exception{
        Gson gson = new Gson();
        Customer customerObject = new Customer("peppe", "peeeppe");
        LinkedList<Customer> customerCollection = new LinkedList<>();
        customerCollection.add(customerObject);
        String customer = gson.toJson(customerCollection);
        System.out.println(customer);
        try{
            Type type = new TypeToken<List<Reading>>(){}.getType();
            List<Reading> r = gson.fromJson(customer, type);
            System.out.println(r);
            System.out.println(gson.toJson(r));
        }catch(RuntimeException ex){
            System.out.println(ex.getMessage());
            System.out.println(ex.getClass().getName());
        }
        
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(customer); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(true); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(463, response.getStatus());
    }
    
    /*  Test 6
        Session found, not reading JSON and DAO returns false */
    @Test
    public void test_okSession_notReadingJSON_falseDAO() throws Exception{
        Gson gson = new Gson();
        Assignment readingObject = new Assignment(1, 1, "", "");
        LinkedList<Assignment> assignmentCollection = new LinkedList<>();
        assignmentCollection.add(readingObject);
        String reading = gson.toJson(assignmentCollection);
        
        when(request.getSession(false)).thenReturn(session); // Set stub session
        when(request.getParameter("readings")).thenReturn(reading); // Set stub readings parameter
        when(readingDAO.saveReadings(any())).thenReturn(false); // Set stub readings parameter
        
        servlet.doPost(request, response);
        Assert.assertEquals(463, response.getStatus());
    }
}
