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
import java.util.List;

/**
 *
 * @author Riccardo
 */
public class AssignmentDAO {
    public Collection<Assignment> getAssignments(int operatorId) throws SQLException{
        Collection<Assignment> assignments = new LinkedList<>();
        List<Object> params = new LinkedList<>();
        String query = "SELECT * FROM ASSIGNMENT A JOIN METER M ON A.meterId=M.meterId WHERE operatorId=?";
        //TODO gestione errori!!??
        params.add(operatorId);
        try (ResultSet result = Database.getInstance().execQuery(query, params)) {
            while(result.next()){
                assignments.add(new Assignment(
                        result.getInt("operatorid"),
                        result.getInt("meterid"),
                        result.getString("address"),
                        result.getString("customer")
                ));
            }
        }
        return assignments;
    }
}