package server;

import dao.PaymentOrderDAO;
import org.json.*;
import entities.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = {"/"})
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
    public void service(HttpServletRequest request, HttpServletResponse response){
        //TODO CONTROLLO ACCESSI
        String paymentOrder,bill;
        int idBill,idPaymentOrder;
        /* Parameter 1 - action */
        String action = request.getParameter("action");
        
        switch(action){
            case "create":
                bill = request.getParameter("bill");
                if(bill != null){
                    idBill = Integer.parseInt(bill);
                    createPaymentOrder(idBill);
                }
                //TODO gestire caso in cui non va bene 
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
                if(paymentOrder != null){
                    idPaymentOrder = Integer.parseInt(paymentOrder);
                    issuePaymentOrder(idPaymentOrder);
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
        //response.getWriter()
        
        
    }
    
  
    
    private boolean createPaymentOrder(int idBill){
        return pDao.createPaymentOrder(idBill);
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
    
    private void saveAsSuspended(int idPaymentOrder){
        return pDao.saveAsSuspended(idPaymentOrder);
    }
    
    private void issuePaymentOrder(int idPaymentOrder){
        return pDao.issuePaymentOrder(idPaymentOrder);
    }
    
    private void reissuePaymentOrder(int idPaymentOrder){
        return pDao.reissuePaymentOrder(idPaymentOrder);
    }
}
