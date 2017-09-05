/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backofficeclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author carlo
 */
public abstract class ServerConnection {
        
    public static HttpURLConnection executeGet(String targetURL) {
        HttpURLConnection connection = null;
        
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Language", "en-US");  

            connection.setUseCaches(false);
            connection.setDoOutput(true);
            
            connection.connect();
            
            
            //Get Response  
          /*  InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;

            while ((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
            rd.close();
        */
        
            return connection;
        
        
        } catch (Exception e) {
           e.printStackTrace();
           return null;
       } finally {
           if (connection != null) {
             connection.disconnect();
            }   
         }
 
    }
}
