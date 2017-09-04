/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
import entities.Assignment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author Riccardo
 */
public class AssignmentDAO {
    public Collection<Assignment> getAssignments(int operatorId) throws SQLException{
        Collection<Assignment> assignments = new LinkedList<>();
        String query = "SELECT * FROM ASSIGNMENTS WHERE operatorID=?";
        //TODO gestione errori!!??
        Connection con = Database.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement(query);
        statement.setInt(1, operatorId);
        
        ResultSet result = statement.executeQuery();
        while(result.next()){
            
            //assignments.add(new Assignment(operatorId, ))
        }
        result.close();
        
        return assignments;
    }
}
