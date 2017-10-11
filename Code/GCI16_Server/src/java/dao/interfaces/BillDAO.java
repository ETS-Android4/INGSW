package dao.interfaces;

import entities.Bill;
import java.util.List;

/**
 * Provides database-hiding methods for Bill class.
 * @author GCI16_25
 */
public interface BillDAO {
    /**
     * Retrieves bills unpaid for three months;
     * @return null if an error occurs, the list of unpaid bills otherwise.
     */
    public List<Bill> getUnpaidBills();
}
