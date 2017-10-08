package backofficeclient.controllers;

import backofficeclient.views.MainForm;

/**
 *
 * @author cdevi
 */
public class MainController {
    private String session;
    private MainForm mainPage;
    
    public MainController(String session){
        this.session = session;
    } 
    
    public void start(){
        mainPage = new MainForm(this);
        mainPage.setVisible(true);
    }
    
    public void managePaymentOrders(){
        PaymentOrderController paymOrdController = new PaymentOrderController(session);
        paymOrdController.start();
        mainPage.dispose();
    }
    
    public void logout(){
        mainPage.dispose();
        new BackOfficeLoginController().start();
    }
}
