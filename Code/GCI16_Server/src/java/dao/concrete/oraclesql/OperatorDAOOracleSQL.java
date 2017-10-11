package dao.concrete.oraclesql;

import dao.interfaces.OperatorDAO;
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
public class OperatorDAOOracleSQL implements OperatorDAO {
    @Override
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
            Database db = Database.getInstance();
            ResultSet rs = db.execQuery(query, params);
            if(rs != null)
                result = rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(OperatorDAOOracleSQL.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return result;
    }
}