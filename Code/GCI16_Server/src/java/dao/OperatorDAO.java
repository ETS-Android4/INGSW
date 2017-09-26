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
 * Provides database-hiding methods for Assingment class.
 * @author GCI16_25
 */
public class OperatorDAO {
    
    /**
     * Verify that exists an operator with given user and pass.
     * 
     * @param user
     * @param pass
     * @param type operator type (see operator constant).
     * @return 
     */
    public Boolean exists(String user, String pass, int type){
        ArrayList<Object> params = new ArrayList<>(2);
        String query = null;
        System.out.println("type = "+type);
        if(type==Operator.TYPE_BACKOFFICE){
            query = "select * from GCI16.backOfficeOperator where username= ? AND pass= ?";
            params.add(user);
        }
        else{
            query = "select * from GCI16.READINGS_OPERATOR where operatorId=? AND pass=?";
            params.add(Integer.parseInt(user));
        }
        
        Boolean result = null;
        
        params.add(pass);
        
        try(ResultSet rs = db.Database.getInstance().execQuery(query, params)){
            if(rs != null)
                result = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(OperatorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
