package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Riccardo
 */
public class PaymentOrderServlet extends HttpServlet {
    
    
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response){
        //TODO CONTROLLO ACCESSI
        
        /* Parameter 1 - action */
        String action = request.getParameter("action");
        
        switch(action){
            case "create":
                break;
            case "delete":
                break;
            case "saveAspaid":
                break;
            case "saveAsNotPertinent":
                break;
            case "saveAsSuspended":
                break;
            case "issue":
                break;
            case "reissue":
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
    
    private void createPaymentOrder(){
        //TODO
    }
    
    private void deletePaymentOrder(PaymentOrder p){
        //TODO
    }
    
    private void saveAsPaid(PaymentOrder p){
        //TODO
    }
    
    private void saveAsNotPertinent(PaymentOrder p){
        //TODO
    }
    
    private void saveAsSuspended(PaymentOrder p){
        //TODO
    }
    
    private void issuePaymentOrder(PaymentOrder p){
        //TODO
    }
    
    private void reissuePaymentOrder(PaymentOrder p){
        //TODO
    }
}
