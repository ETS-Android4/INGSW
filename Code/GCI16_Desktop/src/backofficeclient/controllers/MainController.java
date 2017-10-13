package backofficeclient.controllers;

import backofficeclient.views.MainForm;

/**
 * Allows to start the controllers of the different functionality.
 * In this case an user can accede only to PaymentOrder section.
 * @author GCI16_25
 */
public class MainController {
    private String session;
    private MainForm mainPage;
    
    /**
     * Constructor
     * @param session current JSESSIONID.
     */
    public MainController(String session){
        this.session = session;
    } 
    
    /**
     * Shows main menu.
     */
    public void start(){
        mainPage = new MainForm(this);
        mainPage.setVisible(true);
    }
    
    /**
     * Starts payment order controller.
     */
    public void managePaymentOrders(){
        PaymentOrderController paymOrdController = new PaymentOrderController(session);
        paymOrdController.start();
        mainPage.dispose();
    }
    
    /**
     * Allows to exit the program.
     */
    public void logout(){
        mainPage.dispose();
        new BackOfficeLoginController().start();
    }
}
