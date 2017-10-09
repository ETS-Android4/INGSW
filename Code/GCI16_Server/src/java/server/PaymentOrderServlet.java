//TODO CONTROLLI JSON!!!!!

package server;

import com.google.gson.*;
import dao.PaymentOrderDAO;
import entities.*;
import entities.PaymentOrder.Status;
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
        int idBill,idPaymentOrder,protocol=0;
        String res;
        PaymentOrder p=null;
        PaymentOrder.Status newStatus=null;
        Gson gson = new Gson();
        PrintWriter pw = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if(session == null){
            response.sendError(462,"No session!"); 
            return;
        }
        String action = request.getParameter("action");
        System.out.println(action);
        switch(action){
            case "show":
                res = getPaymentOrders();
                pw.print(res);
                break;
            
            case "create":
                bill = request.getParameter("bill");
                if(bill != null){
                    res  = createPaymentOrder(bill);
                    pw.print(res);
                }
                break;
            case "delete":
                paymentOrder = request.getParameter("paymentOrder");
                if(paymentOrder != null){
                    deletePaymentOrder(paymentOrder);
                }
                break;
            
            default:
                paymentOrder = request.getParameter("paymentOrder");
                p = new Gson().fromJson(paymentOrder, PaymentOrder.class);
                System.out.println("ID: "+p.getId()+" Protocol: "+p.getProtocol());
                switch(action){
                    case "saveAsPaid":
                        newStatus = Status.PAID;
                        break;
                        
                    case "saveAsSuspended":
                        newStatus = Status.SUSPENDED;
                        break;
                        
                    case "issue": 
                        if(!p.getStatus().equals(Status.NOTISSUED)){
                            response.sendError(465,"Not practicable operation");
                            return;
                        }
                        newStatus = Status.ISSUED;
                        break;
                        
                    case "reissue":
                        if(!p.getStatus().equals(Status.SUSPENDED)){
                            response.sendError(465,"Not practicable operation");
                            return;
                        }
                        newStatus = Status.ISSUED;
                        break;
                        
                    case "saveAsNotPertinent":
                        newStatus = Status.NOTPERTINENT;
                        break;
                        
                    default:
                       response.sendError(464,"Bad parameter value");
                       return;
                }
                if(p.isNextStatus(newStatus)){
                    if(!pDao.update(p,newStatus)){
                        response.sendError(500,"Internal server error");
                        return;
                    }
                    else{
                        if(p.getStatus().equals(Status.NOTISSUED) && newStatus.equals(Status.ISSUED)){
                            protocol = pDao.getProtocol(p);
                            pw.write(gson.toJson(protocol));
                        }
                    }
                }
                else{
                    response.sendError(465,"Not practicable operation");
                    return;
                }
                break;
            }
    }
    
  
    private String getPaymentOrders(){
        List<PaymentOrder> list = pDao.getPaymentOrders();
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
    
    
    
}
