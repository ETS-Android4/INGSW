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
            response.sendError(462,"No session!"); //sessione inesistene.
            return;
        }
        String action = request.getParameter("action");
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
                    idBill = Integer.parseInt(bill);
                    res  = createPaymentOrder(idBill);
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
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    deletePaymentOrder(idPaymentOrder);
                }
                break;
                
            case "saveAsPaid":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    saveAsPaid(idPaymentOrder);
                }
                break;
                
            case "saveAsNotPertinent":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    saveAsNotPertinent(idPaymentOrder);
                }
                break;
                
            case "saveAsSuspended":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    saveAsSuspended(idPaymentOrder);
                }
                break;
                
            case "issue":
                paymentOrder = request.getParameter("paymentOrder");
                res = null;
                if(paymentOrder != null){
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    res = issuePaymentOrder(idPaymentOrder);
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
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    reissuePaymentOrder(idPaymentOrder);
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
    private String createPaymentOrder(int idBill){
        String string = null;
        if(pDao.createPaymentOrder(idBill)){
           PaymentOrder p = pDao.getPaymentOrderByBill(idBill);
           Gson gson = new Gson();
           /*Payment order in JSON format.*/
           string = gson.toJson(p);
           
        }
        return string;
    }
    
    private boolean deletePaymentOrder(int idPaymentOrder){
        return pDao.deletePaymentOrder(idPaymentOrder);
    }
    
    private boolean saveAsPaid(int idPaymentOrder){
        return pDao.saveAsPaid(idPaymentOrder);
    }
    
    private boolean saveAsNotPertinent(int idPaymentOrder){
        return pDao.saveAsNotPertinent(idPaymentOrder);
    }
    
    private boolean saveAsSuspended(int idPaymentOrder){
        return pDao.saveAsSuspended(idPaymentOrder);
    }
    
    /**
     * Issues a payment order, and then return its protocol number.  
     * @param idPaymentOrder
     * @return the payment order in JSON format.
     */
    private String issuePaymentOrder(int idPaymentOrder){
        String ret = null;
        if(pDao.issuePaymentOrder(idPaymentOrder)){
            int protocol = pDao.getProtocol(idPaymentOrder);
            Gson gson = new Gson();
            ret = gson.toJson(protocol);
        }
        return ret;
    }
    
    private boolean reissuePaymentOrder(int idPaymentOrder){
        return pDao.reissuePaymentOrder(idPaymentOrder);
    }
    
}
