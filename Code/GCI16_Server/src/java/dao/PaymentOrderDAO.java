/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
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
import org.json.*;
/**
 *
 * @author carlo
 */
public class PaymentOrderDAO {

    public List<PaymentOrder> showPaymentOrders(){//TODO CAMBIARE PREZZO INGIUNZIONE!!!
        
        List <PaymentOrder> list = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();
        
        String query = "SELECT P.IDPAYMENTORDER, P.PROTOCOL,C.NAME,C.SURNAME,B.YEAR,B.TRIMESTER,B.AMOUNT,P.AMOUNT,P.STATUS "
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
                    Bill b = new Bill(c,rs.getDouble("b.amount"));
                    
                    stat = rs.getString("status");
                    if(stat.equals("NOT ISSUED"))
                        stat = "NOTISSUED";
                    status = Status.valueOf(stat); 
                    
                    PaymentOrder p = new PaymentOrder(rs.getInt("idPaymentOrder"),rs.getInt("protocol"),status,b);
                    
                    
                       
/*
                    paymentOrder = new PaymentOrder(rs.getInt("idPaymentOrder"),rs.getString("debtor"), rs.getInt("protocol"),
                                                    rs.getInt("year"), rs.getInt("trimester"),
                                                    rs.getDouble("amount"), status);*/
                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }
    
    public boolean createPaymentOrder(int idBill){
        
        ArrayList<Object> params = new ArrayList<>();
        String query = "INSERT INTO PAYMENTORDER (STATUS,BILL) VALUES(?,?)";
        params.add("NOT NOTIFIED");
        params.add(idBill);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        
        return true;
        
    }
    
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
    
    public boolean issuePaymentOrder(int idPaymentOrder){
        ArrayList<Object> params = new ArrayList<>();
        String query = "UPDATE PAYMENTORDER SET STATUS = 'ISSUED' WHERE IDPAYMENTORDER = ? AND STATUS = 'NOT NOTIFIED'";
        params.add(idPaymentOrder);
        try {
            Database.getInstance().execQuery(query, params);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    
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
