package backofficeclient.controllers;

import backofficeclient.Login;
import backofficeclient.MainPage;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
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
public class LoginController {
    JFrame login;
    
    public static void main(String arg[]){
        LoginController loginController = new LoginController();
        loginController.start();
    }
    
    public void start(){
        Login login = new Login(this);
        this.login = login;
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
        login.setVisible(true);
    }
    
    public void login(String user, String pass){
        URL url;
        try {
            url = new URL("http://localhost:8081/GCI16/Login?user="+user+"&pass="+pass);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            String cookieNameMatch = "JSESSIONID";
            String session = null;
            int resCode = connection.getResponseCode();
            if(resCode == 200){
                System.out.println("Andato a buon fine");
                String cookieString = connection.getHeaderField("Set-Cookie").replaceAll("\\s", "");
                for (String s : cookieString.split(";")) {
                    if (s.contains(cookieNameMatch)){
                        session = s;
                        break;
                    }
                }
                MainPage mPage = new MainPage(session,this);
                mPage.setVisible(true);
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
        
        }catch(ConnectException ex){
            JOptionPane.showMessageDialog(login,"Server not available");
            
        }catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
