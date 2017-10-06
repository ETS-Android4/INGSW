//TODO CONTROLLI JSON!!!!!

package server;

import com.google.gson.*;
import dao.PaymentOrderDAO;
import entities.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet(urlPatterns = {"/PaymentOrder"})
/**
 *
 * @author Riccardo
 */
public class PaymentOrderServlet extends HttpServlet {
    private PaymentOrderDAO pDao;
    @Override
    public void init(){
       pDao = new PaymentOrderDAO();
    }
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        String paymentOrder,bill;
        int idBill,idPaymentOrder;
        String res;
        
        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendError(462,"No session!"); 
            return;
        }
        String action = request.getParameter("action");
        System.out.println("AZIONE: "+action);
        
        switch(action){
            case "show":
                res = showPaymentOrders();
                try {
                    PrintWriter pw = response.getWriter();
                    pw.print(res);
                } catch (IOException ex) {
                    Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
        
                break;
            
            case "create":
                
                bill = request.getParameter("bill");
                
                if(bill != null){
                    res  = createPaymentOrder(bill);
                    try {
                        PrintWriter pw = response.getWriter();
                        pw.print(res);
                    } catch (IOException ex) {
                        Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                break;
            case "delete":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    deletePaymentOrder(paymentOrder);
                }
                break;
                
            case "saveAsPaid":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null)
                    saveAsPaid(paymentOrder);
                break;
                
            case "saveAsNotPertinent":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    saveAsNotPertinent(paymentOrder);
                }
                break;
                
            case "saveAsSuspended":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null)
                    saveAsSuspended(paymentOrder);
                
                break;
                
            case "issue":
                paymentOrder = request.getParameter("paymentOrder");
                res = null;
                if(paymentOrder != null){
                    res = issuePaymentOrder(paymentOrder);
                    PrintWriter pw;
                    try {
                        pw = response.getWriter();
                        pw.write(res);
                    } catch (IOException ex) {
                        Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
                
            case "reissue":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    reissuePaymentOrder(paymentOrder);
                }
                break;
            default:
                try {
                    response.sendError(0, "BAD ACTION"); //TODO
                } catch (IOException ex) {
                    Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
  
    private String showPaymentOrders(){
        List<PaymentOrder> list = pDao.showPaymentOrders();
        Gson gson = new Gson();
        /*List of payment orders in JSON format.*/
        String string = gson.toJson(list);
        return string;
       
    }
    /**
     * Create a payment order from a bill's id and then return the inserted payment order.
     * This because it's necessary to obtain that payment orders's ID, given into databse. 
     * @param idBill
     * @return 
     */
    private String createPaymentOrder(String bill){
        String res = null;
        Gson gson = new Gson();
        Bill b = gson.fromJson(bill, Bill.class);
        if(pDao.createPaymentOrder(b)){
           PaymentOrder p = pDao.getPaymentOrderByBill(b);
           /*Payment order in JSON format.*/
           res = gson.toJson(p);
           
        }
        return res;
    }
    
    private boolean deletePaymentOrder(String paymentOrder){
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        return pDao.deletePaymentOrder(p);
    }
    
    private boolean saveAsPaid(String paymentOrder){
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        return pDao.saveAsPaid(p);
    }
    
    private boolean saveAsNotPertinent(String paymentOrder){
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        return pDao.saveAsNotPertinent(p);
    }
    
    private boolean saveAsSuspended(String paymentOrder){
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        return pDao.saveAsSuspended(p);
    }
    
    /**
     * Issues a payment order, and then return its protocol number.  
     * @param idPaymentOrder
     * @return the payment order in JSON format.
     */
    private String issuePaymentOrder(String paymentOrder){
        String ret = null;
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        if(pDao.issuePaymentOrder(p)){
            System.out.println("Protocol: ");
            int protocol = pDao.getProtocol(p);
            System.out.print(protocol);
            Gson gson = new Gson();
            ret = gson.toJson(protocol);
        }
        return ret;
    }
    
    private boolean reissuePaymentOrder(String paymentOrder){
        PaymentOrder p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
        return pDao.reissuePaymentOrder(p);
    }
    
}
