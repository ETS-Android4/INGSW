package backofficeclient.controllers;

import backofficeclient.views.BackOfficeLoginForm;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * TODO
 * @author cdevi
 */
public class BackOfficeLoginController {
    private JFrame login;
    
    public static void main(String arg[]){
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(BackOfficeLoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        BackOfficeLoginController loginController = new BackOfficeLoginController();
        loginController.start();
    }
    
    public void start(){
        login = new BackOfficeLoginForm(this);
        login.setVisible(true);
    }
    
    public void login(String user, String pass){
        URL url;
        try {
            System.out.println(1);
            url = new URL("http://localhost:8081/GCI16/Login?user="+user+"&pass="+pass);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            String cookieNameMatch = "JSESSIONID";
            String session = null;
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                String cookieString = connection.getHeaderField("Set-Cookie").replaceAll("\\s", "");
                for (String s : cookieString.split(";")) {
                    if (s.contains(cookieNameMatch)){
                        session = s;
                        break;
                    }
                }
                new MainController(session).start();
                login.dispose();
            }
            else if(resCode == 461){
                JOptionPane.showMessageDialog(login,"Login Error! Insert valid entries.");
            }
            else if(resCode == 463){
                JOptionPane.showMessageDialog(login,"Login Error! Missing username or password.");
            }
            else if(resCode == 500){
                JOptionPane.showMessageDialog(login,"Server not avalaible");
            }
        }catch(ConnectException | SocketTimeoutException ex){
            JOptionPane.showMessageDialog(login,"Server not available");      
        }catch (IOException ex) {
            Logger.getLogger(BackOfficeLoginForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
