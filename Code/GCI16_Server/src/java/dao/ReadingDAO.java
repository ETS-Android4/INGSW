/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
import entities.Reading;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riccardo
 */
public class ReadingDAO {
    public boolean saveReadings(Collection<Reading> readings){
        if(readings==null) throw new IllegalArgumentException("Argument must be not null");
        String query = "INSERT INTO GCI16.READING(operatorId, meterId, readingdate, consumption)VALUES(?,?,?,?)";
        List<Object> params = new ArrayList<>(4);
        for(Reading r : readings){
            params.add(r.getOperatorId());
            params.add(r.getMeterId());
            params.add(new java.sql.Date(r.getDate().getTime()));
            params.add(r.getConsumption());
            //TODO Usare batch?
            try {
                ResultSet result = Database.getInstance().execQuery(query, params);
                result.close();
            } catch (SQLException ex) {
                Logger.getLogger(ReadingDAO.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            params.clear();
        }

        return true;
    }
}
