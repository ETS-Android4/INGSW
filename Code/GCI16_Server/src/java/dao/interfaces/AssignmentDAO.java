package dao.interfaces;

import entities.Assignment;
import java.util.Collection;

/**
 * Provides database-hiding methods for Assingment class.
 * 
 * @author Riccardo
 */
public interface AssignmentDAO {
    /**
     * Retrieves the assignments destined to an operator.
     * 
     * @param operatorId the id of the operator.
     * @return null if an error occurs, the list of the assignments for the operator otherwise.
     */
    public Collection<Assignment> getAssignments(int operatorId);
}