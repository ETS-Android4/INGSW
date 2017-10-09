package dao;

import entities.Reading;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides database-hiding methods for Reading class.
 * 
 * @author Riccardo
 */
public class ReadingDAO {
    /**
     * Saves a list of readings into the database.
     * 
     * @param readings list of readings to save into the database
     * @return true if the operation is successful, false otherwise
     */
    public boolean saveReadings(Collection<Reading> readings){
        if(readings==null) throw new IllegalArgumentException("Argument is null");
        if(readings.isEmpty())return true;
        
        String query = "INSERT INTO READING(operatorid, meterid, readingdate, consumption)VALUES(?,?,?,?)";
        List<Object> params = new ArrayList<>(4);
        Database db = Database.getInstance();
        
        for(Reading r : readings){
            params.add(r.getOperatorId());
            params.add(r.getMeterId());
            params.add(new java.sql.Date(r.getDate().getTime()));
            params.add(r.getConsumption());
            try{
                ResultSet result = db.execQuery(query, params);
                result.close();
            } catch (SQLException ex) {
                Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
                try{
                    db.rollback();
                } catch(SQLException e){}
                return false;
            }
            params.clear();
        }
        
        try {
            db.commit();
        }catch(SQLException ex) {
            Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
