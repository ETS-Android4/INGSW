
package backofficeclient.controllers;

import backofficeclient.entities.Bill;
import backofficeclient.views.BillForm;
import backofficeclient.ConfirmPanel;
import backofficeclient.entities.PaymentOrder;
import backofficeclient.entities.PaymentOrder.Status;
import backofficeclient.views.PaymentOrderForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private PaymentOrderForm paymentOrderFrame; 
    private final String session;
    private List<PaymentOrder> paymOrdList;
    private BillForm billFrame;
    private List<Bill> billList;
    
    public PaymentOrderController(String session){
        this.session = session;
    }
    
    public void start(){
        paymentOrderFrame = new PaymentOrderForm(this);
        paymentOrderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new MainController(session).start();
            }
        });

        try {
            URL url = new URL("http://localhost:8081/GCI16/PaymentOrder?action=show");
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
                paymOrdList = gson.fromJson(line, POListType);
                paymentOrderFrame.setTable(paymOrdList);
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
        billFrame = new BillForm(this);
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
            wr.close();
            connection.connect();
            
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                line = rd.readLine();     
                rd.close();
                PaymentOrder p = gson.fromJson(line, PaymentOrder.class);
                paymentOrderFrame.addPaymentOrder(p);
                paymOrdList.add(p);
                JOptionPane.showMessageDialog(billFrame, "Operation successfully completed!");

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
        PaymentOrder p = paymOrdList.get(row);
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
                paymOrdList.remove(row);
                paymentOrderFrame.removePaymentOrderByRow(row);
                //Operation completed
                JOptionPane.showMessageDialog(paymentOrderFrame, "Operation successfully completed!");
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
        PaymentOrder p = paymOrdList.get(row);
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
                PaymentOrder paymOrd = paymOrdList.get(row);
                paymOrd.setStatus(Status.SUSPENDED);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Operation successfully completed!");
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }       
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void saveAsPaidPaymentOrder(){
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = paymOrdList.get(row);
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
                paymOrdList.remove(row);
                paymentOrderFrame.removePaymentOrderByRow(row);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Operation successfully completed!");
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }else if (resCode == 464){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Bad parameter values"); 
            }else if (resCode == 465){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Not practicable operation"); 
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
        PaymentOrder p = paymOrdList.get(row);
        System.out.println("Stato: "+p.getStatus());
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
                paymOrdList.remove(row);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Operation successfully completed!");
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }else if (resCode == 465){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Not practicable operation"); 
            }
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void issuePaymentOrder() {
        //Confirm operation
        if(!ConfirmPanel.showConfirm(paymentOrderFrame)) return;
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = paymOrdList.get(row);
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
                PDFGenerator pdfGen = new PDFGenerator();
                pdfGen.generate(p);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Payment order with protocol " + p.getProtocol() + " has been issued.\nA PDF, with all the information, was created correctly");      
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        
        }catch (MalformedURLException ex) {
            Logger.getLogger(PaymentOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public void reissuePaymentOrder(){
        int row = paymentOrderFrame.getTableSelectedRow();
        PaymentOrder p = paymOrdList.get(row);
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
            wr.writeBytes("action=reissue&");
            wr.writeBytes("paymentOrder="+gson);
            connection.connect();    

            int resCode = connection.getResponseCode();
            if(resCode == 200){
                //Sets issued the selected payment order 
                paymentOrderFrame.setPaymentOrderStatus(row,"ISSUED");
                p.setStatus(Status.ISSUED);
                JOptionPane.showMessageDialog(paymentOrderFrame, "Operation successfully completed!");
                //In this case there is no creation of a new PDF
            }else if (resCode == 462){
              JOptionPane.showMessageDialog(paymentOrderFrame,"Server not available"); 
            }
        } catch (IOException ex) {
            Logger.getLogger(PaymentOrderForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        paymentOrderFrame.clearSelectionTable();
    }
    
    public PaymentOrder getPaymentOrderByRow(int row){
        return paymOrdList.get(row);
    } 
}