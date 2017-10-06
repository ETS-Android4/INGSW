
package backofficeclient.controllers;

import backofficeclient.Bill;
import backofficeclient.BillTable;
import backofficeclient.ConfirmPanel;
import backofficeclient.PaymentOrder;
import backofficeclient.PaymentOrder.Status;
import backofficeclient.PaymentOrderTable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import pdfgenerator.PDFGenerator;

/**
 *
 * @author cdevi
 */


public class PaymentOrderController {
    PaymentOrderTable paymentOrderFrame; 
    String session;
    List<PaymentOrder> list;
    BillTable billFrame;
    List<Bill> billList;
    
    public PaymentOrderController(String session){
        this.session = session;
    }
    
    public void start(){
        paymentOrderFrame = new PaymentOrderTable(session, this);
        URL url;
        try {
            url = new URL("http://localhost:8081/GCI16/PaymentOrder?action=show");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.connect();
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                line = rd.readLine();
                rd.close();
                Gson gson = new Gson();
                java.lang.reflect.Type POListType = new TypeToken<Collection< PaymentOrder> >(){}.getType();
                list = gson.fromJson(line, POListType);
                paymentOrderFrame.setTable(list);
                paymentOrderFrame.setVisible(true);
            }
            else if(resCode == 462){
                JOptionPane.showMessageDialog(paymentOrderFrame,"Server not avalaible");
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);    
        
        }catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public void createPaymentOrder(){
        billFrame = new BillTable(this,session);
        try{
            URL url = new URL("http://localhost:8081/GCI16/Bill?action=show");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.connect();
            int resCode = connection.getResponseCode();
            if(resCode==200){
                InputStream is = connection.getInputStream();                
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line = rd.readLine();
                rd.close();
                Gson gson = new Gson();
                // From JSON to collection.
                java.lang.reflect.Type BillListType = new TypeToken<Collection< Bill> >(){}.getType();
                List<Bill> list = gson.fromJson(line, BillListType);
                this.billList = list;
                if(list != null){
                    billFrame.setTable(list);
                    billFrame.setVisible(true);
                }
            }
            else if (resCode == 462){
                JOptionPane.showMessageDialog(billFrame,"Server not available");
            }
        }   catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void createPaymentOrderByBill(){
        if( !ConfirmPanel.showConfirm(billFrame)) return;
        int row = billFrame.getTableSelectedRow();
        Bill b = billList.get(row);
        int id = b.getId();
        Gson gson = new Gson();
        String gsonString = gson.toJson(b);
        try{
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=create&");
            
            wr.writeBytes("bill="+gsonString);
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                line = rd.readLine();     
                rd.close();
                PaymentOrder p = gson.fromJson(line, PaymentOrder.class);
                paymentOrderFrame.addPaymentOrder(p);
                list.add(p);
                ConfirmPanel.showSuccess(billFrame);
                billFrame.dispose();
            }
            else if(resCode == 462){
                JOptionPane.showMessageDialog(billFrame,"Server not available");
            }
        }catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void deletePaymentOrder(){
        //Ask confirm operation
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        int id = p.getId();
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return;
        String gson = new Gson().toJson(p);
        try {
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //Set JSESSIONID
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=delete&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect();
            int resCode = connection.getResponseCode();
            if(resCode == 200){                
                list.remove(row);
                paymentOrderFrame.removePaymentOrderByRow(row);
                //Operation completed
                ConfirmPanel.showSuccess(paymentOrderFrame);
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }

    public void saveAsSuspendedPaymentOrder(){
       //Ask confirm operation
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return; 
        String gson = new Gson().toJson(p);
        try{
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=saveAsSuspended&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect();
 
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                paymentOrderFrame.setPaymentOrderStatus(row, "SUSPENDED");
                PaymentOrder paymOrd = list.get(row);
                paymOrd.setStatus(Status.SUSPENDED);
                ConfirmPanel.showSuccess(paymentOrderFrame);
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
            
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void saveAsPaidPaymentOrder(){
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return; 
        String gson = new Gson().toJson(p);
        try{
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=saveAsPaid&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect();   
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                list.remove(row);
                paymentOrderFrame.removePaymentOrderByRow(row);
                ConfirmPanel.showSuccess(paymentOrderFrame);
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        } catch (MalformedURLException ex) {
                Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void saveAsNotPertinentPaymentOrder() {
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        int id = p.getId();
        //Confirm operation
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return;
        String gson = new Gson().toJson(p);
        try{
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=saveAsNotPertinent&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect(); 
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                //Removes that payment order from the table
                paymentOrderFrame.removePaymentOrderByRow(row);
                list.remove(row);
                ConfirmPanel.showSuccess(paymentOrderFrame);
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void issuePaymentOrder() {
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        int id = p.getId();
        //Confirm operation
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return;
        Gson gson = new Gson();
        String gsonString = gson.toJson(p);
        try {
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=issue&");
            wr.writeBytes("paymentOrder="+gsonString);
            connection.connect(); 
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                //Set issued the selected payment order 
                paymentOrderFrame.setPaymentOrderStatus(row, "ISSUED"); //Modifico la colonna relativa allo stato.
                p.setStatus(Status.ISSUED);
                //Server returns all informations about the issued payment order
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                line = rd.readLine();
                rd.close();
                int protocol = gson.fromJson(line, Integer.class);
                p.setProtocol(protocol);
                //Sets number protocol of payment order in the table 
                paymentOrderFrame.setProtocolNumberByRow(row, protocol);
                // Generates PDF 
                PDFGenerator.generate(p);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Payment order with protocol " + p.getProtocol() + " has been issued.\nA PDF, with all the information, was created correctly");
                
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void reissuePaymentOrder(){
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = list.get(row);
        int id = p.getId();
        //Confirm operation
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return;
        String gson = new Gson().toJson(p);
        try{
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder?");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", session);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes("action=reissue&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect();    

            int resCode = connection.getResponseCode();
            if(resCode == 200){
                //Sets issued the selected payment order 
                paymentOrderFrame.setPaymentOrderStatus(row,"ISSUED");
                p.setStatus(Status.ISSUED);
                ConfirmPanel.showSuccess(paymentOrderFrame);
                //In this case there is no creation of a new PDF
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public PaymentOrder getPaymentOrderByRow(int row){
        return list.get(row);
    } 
}