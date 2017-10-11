package dao.interfaces;

import entities.Reading;
import java.util.Collection;

/**
 * Provides database-hiding methods for Reading class.
 * 
 * @author Riccardo
 */
public interface ReadingDAO {
    /**
     * Saves a list of readings into the database.
     * 
     * @param readings list of readings to save into the database
     * @return true if the operation is successful, false otherwise
     */
    public boolean saveReadings(Collection<Reading> readings);
}
