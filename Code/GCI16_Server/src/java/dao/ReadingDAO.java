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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Riccardo
 */
public class ReadingDAO {
    public boolean saveReadings(Collection<Reading> readings){
        int i;//TODO
        if(readings==null) throw new IllegalArgumentException("Argument must be not null");
        String single = "INSERT INTO READINGS(operator, meter, date, consumption)VALUES(?,?,?,?);";
        String query = "";
        List<Object> params = new LinkedList<>();
        for(Reading r : readings){
            query += single;
            params.add(r.getOperatorId());
            params.add(r.getMeterId());
            params.add(new java.sql.Date(r.getDate().getTime()));
            params.add(r.getConsumption());
        }
        try{
        ResultSet result = Database.getInstance().execQuery(query, params);
        }catch(SQLException ex){
            return false;
        }
        return true;
    }
}
