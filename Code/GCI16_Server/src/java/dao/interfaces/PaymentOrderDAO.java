package dao.interfaces;

import entities.Bill;
import entities.PaymentOrder;
import entities.PaymentOrder.Status;
import java.util.List;

/**
 * Provides database-hiding methods for PaymentOrder class.
 * @author GCI16_25
 */
public interface PaymentOrderDAO {

    /**
     * Retrieves the list of payment orders.
     * @return list of payment orders
     */
    public List<PaymentOrder> getPaymentOrders();
    
    /**
     * Retrieves protocol number of a payment order
     * 
     * @param p
     * @return protocol number
     */
    public int getProtocol(PaymentOrder p);
        
    /**
     * Retrieves a payment order from a bill's id
     * @param b
     * @return a payment order 
     */
    public PaymentOrder getPaymentOrderByBill(Bill b);
    
    /**
     * Inserts payment order into database
     * @param b
     * @return false if an error occurs, true otherwise.
     */
    public boolean createPaymentOrder(Bill b);
    
    /**
     * Deletes a payment order
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean deletePaymentOrder(PaymentOrder p);
    
    /**
     * 
     * @param p
     * @param newStatus
     * @return 
     */
    public boolean update(PaymentOrder p, Status newStatus);
}
