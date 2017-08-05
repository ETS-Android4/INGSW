/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
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

    public String showPaymentOrders(){//TODO CAMBIARE PREZZO INGIUNZIONE!!!
        String query = "SELECT P.PROTOCOL,CONCAT(C.NAME,' ',C.SURNAME) AS DEBTOR,B.YEAR,B.TRIMESTER,P.AMOUNT,P.STATUS  "
                    +  "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) PB JOIN CUSTOMER C ON PB.CUSTOMER = C.IDCUSTOMER "
                    +  "WHERE P.STATUS NOT IN ('PAID','NOT PERTINENT')";
        ResultSet rs = null;
        String ret = null;
        
        try {
            rs = Database.getInstance().execQuery(query,null);
        } catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rs != null){
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonOb;
            try {
                while(rs.next()){
                    jsonOb = new JSONObject();
                    jsonOb.put("Protocol", rs.getInt(1));
                    jsonOb.put("Debtor", rs.getString(2));
                    jsonOb.put("Year",rs.getString(3));
                    jsonOb.put("Trimester",rs.getInt(4));
                    jsonOb.put("Amount",rs.getDouble(5));
                    jsonOb.put("Status",rs.getString(6));
                    jsonArr.put(jsonOb);
                }
                ret = jsonArr.toString();
            } catch (SQLException ex) {
                Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
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
