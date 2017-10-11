package dao.interfaces;

import entities.Operator;

/**
 * Provides database-hiding methods for Operator class.
 * @author GCI16_25
 */
public interface OperatorDAO {
    /**
     * Verify that exists an operator with given user and pass.
     * 
     * @param op the operator of which to control the existence
     * @return null if a Database access error occurs, true or false respectively
     *  if the operator is or not in the database otherwise.
     */
    public Boolean exists(Operator op);
}