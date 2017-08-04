/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author carlo
 */
public class PaymentOrderDAO {
 
    
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
