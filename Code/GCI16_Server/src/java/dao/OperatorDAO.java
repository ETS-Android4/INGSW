/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entities.Operator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class OperatorDAO {
    
    public static Operator getOperator(String user,String pass){
        ArrayList<Object> params = new ArrayList<>();
        String query = "select * from backOfficeOperator where username= ? AND pass= ?"; 
        params.add(user);
        params.add(pass);
        ResultSet rs;
        Operator op = null;
        try {
            System.err.println("ciaoo prima");
            rs = db.Database.getInstance().execQuery(query, params);
            System.err.println("ciaoo dopo");
            
            if(rs != null && rs.next()){
                System.out.println("ciaoo");
                op = new Operator(rs.getString("username"),rs.getString("pass"));
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(OperatorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return op;
    }
}
