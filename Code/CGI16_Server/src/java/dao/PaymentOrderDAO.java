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
         
        ArrayList<Object> params = new ArrayList<>();
        
        String query = "SELECT P.PROTOCOL,(C.NAME || ' ' || C.SURNAME) AS DEBTOR,B.YEAR,B.TRIMESTER,P.AMOUNT,P.STATUS "
                + "FROM (PAYMENTORDER P JOIN BILL B ON P.BILL = B.IDBILL) JOIN CUSTOMER C ON B.CUSTOMER = C.IDCUSTOMER "
                + "WHERE P.STATUS NOT IN (?,?)";
        
        params.add("NOT PERTINENT");
        params.add("PAID");
        ResultSet rs = null;
        String ret = null;
        
        try {
            System.out.println("PRIMA");
            rs = Database.getInstance().execQuery(query,params);
            System.out.println("DOPO");
        } catch (SQLException ex) {
            Logger.getLogger(PaymentOrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(rs != null){
            JSONArray jsonArr = new JSONArray();
            JSONObject jsonOb;
            try {
                System.out.println("Trasformo in JSON Array!");
                while(rs.next()){
                   jsonOb = new JSONObject();
                    jsonOb.append("Protocol", rs.getInt(3));
                    jsonOb.append("Debtor", rs.getString(2));
                    jsonOb.append("Year",rs.getInt(1));
                    jsonOb.append("Trimester",rs.getInt(4));
                    jsonOb.append("Amount",rs.getDouble(5));
                    jsonOb.append("Status",rs.getString(6));
                    jsonArr.put(jsonOb);
                   
                }
                ret = jsonArr.toString();
                System.out.println("DAO = " + ret);
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
