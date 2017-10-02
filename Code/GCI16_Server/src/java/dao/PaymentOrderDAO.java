/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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
public class PaymentOrderDAO {

    /**
     * Retrieves the list of payment orders.
     * @return list of payment orders
     */
    public List<PaymentOrder> showPaymentOrders(){
        
        List <PaymentOrder> list = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();
        
        /* Retrieves informations about payment orders, associated with corresponding bill and customer.*/  
        String query = "SELECT P.IDPAYMENTORDER, P.PROTOCOL,C.NAME,C.SURNAME,B.YEAR,B.TRIMESTER,B.AMOUNT as BAMOUNT ,P.AMOUNT as PAMOUNT,P.STATUS "
                + "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) JOIN CUSTOMER C ON B.CUSTOMER = C.IDCUSTOMER "
                + "WHERE P.STATUS NOT IN (?,?)";
        
        params.add("NOT PERTINENT");
        params.add("PAID");
        ResultSet rs = null;
        String ret = null;
        String stat;
        Status status;
        try {
            System.out.println("PRIMA");
            rs = Database.getInstance().execQuery(query,params);
            System.out.println("DOPO");
            
            PaymentOrder paymentOrder;
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
            Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    /**
     * Retrieves protocol number of a payment order
     * @param idPaymentOrder
     * @return protocol number
     */
    public int getProtocol(int idPaymentOrder){
        String query = "SELECT PROTOCOL "
                     + "FROM PAYMENTORDER "
                     + "WHERE IDPAYMENTORDER = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(idPaymentOrder);
        int protocol = 0;
        try{
            ResultSet rs = dao.Database.getInstance().execQuery(query, params);
            if(rs != null && rs.next()){
                protocol = rs.getInt("protocol");       
            }
        }catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return protocol;
    }
        
    /**
     * Retrieves a payment order from a bill's id
     * @param idBill
     * @return a payment order 
     */
    public PaymentOrder getPaymentOrderByBill(int idBill){
        String query = "SELECT P.IDPAYMENTORDER, P.PROTOCOL,C.NAME,C.SURNAME,B.YEAR,B.TRIMESTER,B.AMOUNT as BAMOUNT ,P.AMOUNT as PAMOUNT,P.STATUS "
                + "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) JOIN CUSTOMER C ON B.CUSTOMER = C.IDCUSTOMER "
                + "WHERE P.BILL = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(idBill);
        PaymentOrder p = null;
        try {
            ResultSet rs = dao.Database.getInstance().execQuery(query, params);
            if(rs!=null){
            
                if(rs.next()){
                    Customer c = new Customer(rs.getString("name"),rs.getString("surname"));
                    Bill b = new Bill(c,rs.getDouble("bamount"),rs.getInt("year"),rs.getInt("trimester"));

                    String stat = rs.getString("status");
                    if(stat.equals("NOT ISSUED"))
                        stat = "NOTISSUED";
                    Status status = Status.valueOf(stat); 

                     p = new PaymentOrder(rs.getInt("idPaymentOrder"),rs.getInt("protocol"),status,b,rs.getDouble("pamount"));
                }
            }     
        }catch(SQLException ex){
                Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        return p;
    }
    
    /**
     * Inserts payment order into database
     * @param idBill
     * @return false if an error occurs, true otherwise.
     */
    public boolean createPaymentOrder(int idBill){
        
        ArrayList<Object> params = new ArrayList<>();
        String query = "INSERT INTO PAYMENTORDER (STATUS,BILL) VALUES(?,?)";
        params.add("NOT ISSUED");
        params.add(idBill);
        try {
            ResultSet rs = Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        
        return true;
        
    }
    
    /**
     * Deletes a payment order
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean deletePaymentOrder(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "DELETE FROM PAYMENTORDER WHERE IDPAYMENTORDER = ?";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Issues payment order.
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean issuePaymentOrder(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'ISSUED' WHERE IDPAYMENTORDER = ? AND STATUS = 'NOT ISSUED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Reissues payment order.
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean reissuePaymentOrder(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'ISSUED' WHERE IDPAYMENTORDER = ? AND STATUS = 'SUSPENDED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Saves as paid payment order.
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean saveAsPaid(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'PAID' WHERE IDPAYMENTORDER = ? AND STATUS = 'NOTIFIED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Issues a payment order.
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean saveAsSuspended(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'SUSPENDED' WHERE IDPAYMENTORDER = ? AND STATUS = 'NOTIFIED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Saves as not pertinent a payment order.
     * @param idPaymentOrder
     * @return false if an error occurs, true otherwise.
     */
    public boolean saveAsNotPertinent(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'NOT PERTINENT' WHERE IDPAYMENTORDER = ? AND STATUS = 'SUSPENDED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
}
