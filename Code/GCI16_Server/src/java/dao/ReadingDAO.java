/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
import entities.Reading;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 *
 * @author Riccardo
 */
public class ReadingDAO {
    public void saveReadings(Collection<Reading> readings) throws SQLException{
        int i;//TODO
        if(readings==null) throw new IllegalArgumentException("Argument must be not null");
        Connection con = Database.getInstance().getConnection();
        String single = "INSERT INTO READINGS(operator, meter, date, consumption)VALUES(?,?,?,?);";
        String total = "";
        for(i=0; i<readings.size(); i++)
            total += single;
        
        PreparedStatement statement = con.prepareStatement(total);
        i=1;
        for(Reading r : readings){
            statement.setInt(i++, r.getOperatorId());
            statement.setInt(i++, r.getMeterId());
            //TODO statement.setDate(i++, new Date());
            statement.setFloat(i++, r.getConsumption());
        }
        
        statement.executeQuery();
    }
}
