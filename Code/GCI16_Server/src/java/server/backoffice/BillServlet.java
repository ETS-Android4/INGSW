package server.backoffice;

import com.google.gson.Gson;
import dao.interfaces.BillDAO;
import entities.Bill;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles operator's request of getting unpaid bills 
 * @author carlo
 */
@WebServlet(name = "BillServlet", urlPatterns = {"/Bill"})
public class BillServlet extends HttpServlet {
    private volatile BillDAO billDAO;
    
    @Override
    public void init(){
        setBillDAO(new dao.concrete.oraclesql.BillDAOOracleSQL());
    }
    
    public void setBillDAO(BillDAO billDAO){
        this.billDAO = billDAO;
    }
    
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
    protected void service (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String action = request.getParameter("action");
        Gson gson = new Gson();
        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendError(462,"No session!");//Errore sessione inesistente.
            return ;
        }
        if(action==null){
            response.sendError(463, "Missing parameter");
            return;
        }
        if (action.equals("get")){
            List<Bill> list = billDAO.getUnpaidBills();
            /*Return the list in json format.*/
            String res = gson.toJson(list);
            try {
                PrintWriter pw = response.getWriter();
                pw.print(res);
            } catch (IOException ex) {
                Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            response.sendError(464, "Bad parameter");
        }
        
    }
}
