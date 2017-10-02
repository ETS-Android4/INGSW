package dao;

import entities.Operator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
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
     * @param op the operator of which to control the existence
     * @return null if a Database access error occurs, true or false respectively
     *  if the operator is or not in the database otherwise.
     */
    public Boolean exists(Operator op){
        String user = op.getIdentifier();
        ArrayList<Object> params = new ArrayList<>(2);
        String query = null;
        if(op.getType()==Operator.TYPE_BACKOFFICE){
            query = "select * from GCI16.backOfficeOperator where username= ? AND pass= ?";
            params.add(user);
        }
        else{
            query = "select * from GCI16.READINGS_OPERATOR where operatorId=? AND pass=?";
            params.add(Integer.parseInt(user));
        }
        params.add(op.getPass());
        Boolean result = null;
        try{
            Database db = dao.Database.getInstance();
            ResultSet rs = db.execQuery(query, params);
            if(rs != null)
                result = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(OperatorDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }
}