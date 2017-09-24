/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import db.Database;
import entities.Assignment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides database-hiding methods for Assingment class.
 * 
 * @author Riccardo
 */
public class AssignmentDAO {
    /**
     * Retrieves the assignments destined to the operator whose ID is passed as parameter.
     * 
     * @param operatorId
     * @return null if an error occurs, the list of the assignments for the operator otherwise
     */
    public Collection<Assignment> getAssignments(int operatorId){
        Collection<Assignment> assignments = new LinkedList<>();
        List<Object> params = new LinkedList<>();
        String query = "SELECT * FROM ASSIGNMENT A JOIN METER M ON A.meterId=M.meterId WHERE operatorId=?";
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
        } catch (SQLException ex) {
            Logger.getLogger(AssignmentDAO.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return assignments;
    }
}