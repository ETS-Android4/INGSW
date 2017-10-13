package dao.concrete.oraclesql;

import dao.interfaces.PaymentOrderDAO;
import entities.Bill;
import entities.Customer;
import entities.PaymentOrder;
import entities.PaymentOrder.Status;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides database-hiding methods for PaymentOrder class.
 * @author GCI16_25
 */
public class PaymentOrderDAOOracleSQL implements PaymentOrderDAO{

    @Override
    public List<PaymentOrder> getPaymentOrders(){
        
        List <PaymentOrder> list = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();
        
        /* Retrieves informations about payment orders, associated with corresponding bill and customer.*/  
        String query = "SELECT P.IDPAYMENTORDER, P.PROTOCOL,C.NAME,C.SURNAME,B.YEAR,B.TRIMESTER,B.AMOUNT as BAMOUNT ,P.AMOUNT as PAMOUNT,P.STATUS "
                + "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) JOIN CUSTOMER C ON B.CUSTOMER = C.IDCUSTOMER "
                + "WHERE P.STATUS NOT IN (?,?)";
        
        params.add("NOT PERTINENT");
        params.add("PAID");
        ResultSet rs = null;
        String stat;
        Status status;
        try {
            rs = Database.getInstance().execQuery(query,params);
            if(rs != null){
                while(rs.next()){
                    Customer c = new Customer(rs.getString("name"),rs.getString("surname"));
                    Bill b = new Bill(c,rs.getDouble("bamount"),rs.getInt("year"),rs.getInt("trimester"));
                    
                    stat = rs.getString("status");
                    if(stat.equals("NOT ISSUED"))
                        stat = "NOTISSUED";
                    status = Status.valueOf(stat); 
                    PaymentOrder p = new PaymentOrder(rs.getInt("idPaymentOrder"),rs.getInt("protocol"),status,b,rs.getDouble("pamount"));
                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAOOracleSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    
    @Override
    public int getProtocol(PaymentOrder p){
        String query = "SELECT PROTOCOL "
                     + "FROM PAYMENTORDER "
                     + "WHERE IDPAYMENTORDER = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(p.getId());
        int protocol = 0;
        try{
            ResultSet rs = Database.getInstance().execQuery(query, params);
            if(rs != null && rs.next()){
                protocol = rs.getInt("protocol");       
            }
        }catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAOOracleSQL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return protocol;
    }
    
    
    @Override
    public PaymentOrder getPaymentOrderByBill(Bill b){
        String query = "SELECT P.IDPAYMENTORDER, P.PROTOCOL,C.NAME,C.SURNAME,B.YEAR,B.TRIMESTER,B.AMOUNT as BAMOUNT ,P.AMOUNT as PAMOUNT,P.STATUS "
                + "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) JOIN CUSTOMER C ON B.CUSTOMER = C.IDCUSTOMER "
                + "WHERE P.BILL = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(b.getId());
        PaymentOrder p = null;
        try {
            ResultSet rs = Database.getInstance().execQuery(query, params);
            if(rs!=null){
            
                if(rs.next()){
                    /*Customer c = new Customer(rs.getString("name"),rs.getString("surname"));
                    Bill b = new Bill(c,rs.getDouble("bamount"),rs.getInt("year"),rs.getInt("trimester"));*/

                    String stat = rs.getString("status");
                    if(stat.equals("NOT ISSUED"))
                        stat = "NOTISSUED";
                    Status status = Status.valueOf(stat); 

                     p = new PaymentOrder(rs.getInt("idPaymentOrder"),rs.getInt("protocol"),status,b,rs.getDouble("pamount"));
                }
            }     
        }catch(SQLException ex){
                Logger.getLogger(PaymentOrderDAOOracleSQL.class.getName()).log(Level.SEVERE, null, ex);
            }
        return p;
    }
    
  
    @Override
    public boolean createPaymentOrder(Bill b){
        
        ArrayList<Object> params = new ArrayList<>();
        String query = "INSERT INTO PAYMENTORDER (STATUS,BILL) VALUES(?,?)";
        params.add("NOT ISSUED");
        params.add(b.getId());
        try {
            ResultSet rs = Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        
        return true;
    }
    
    
    @Override
    public boolean deletePaymentOrder(PaymentOrder p){
        ArrayList<Object> params = new ArrayList<>();
        String query = "DELETE FROM PAYMENTORDER WHERE IDPAYMENTORDER = ?";
        params.add(p.getId());
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    public boolean update(PaymentOrder p, Status newStatus){
        ArrayList<Object> params = new ArrayList<>();
        String status = null;
        String query = "UPDATE PAYMENTORDER SET STATUS = ? WHERE IDPAYMENTORDER = ?";
        if(newStatus.equals(Status.NOTISSUED))
            status = "NOT ISSUED";
        else if(newStatus.equals(Status.NOTPERTINENT))
            status = "NOT PERTINENT";
        else
            status = newStatus.toString();
        params.add(status);
        params.add(p.getId());
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
