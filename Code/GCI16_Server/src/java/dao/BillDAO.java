/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entities.Bill;
import entities.Customer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carlo
 */
public class BillDAO {
    
    public List<Bill> getUnpaidBills() {
        String query = "select idBill, year, trimester,name,surname,amount "
                    +  "from bill b join customer c on c.idcustomer = b.customer " +
                        "where extract(year from current_date) >= b.year and " +
                        "extract(month from current_date) >= b.trimester*3 - 3 and "+
                        "b.status = 'unpaid'";
        ArrayList<Object> params = null;
        List<Bill> list = new ArrayList<>();
        
        try {
            ResultSet rs = db.Database.getInstance().execQuery(query, params);
            if(rs!= null){
                while(rs.next()){
                    Customer c = new Customer(rs.getString("name"), rs.getString("surname"));
                    Bill b = new Bill(rs.getInt("idBill"),c,rs.getDouble("amount"),rs.getInt("year"),rs.getInt("trimester"));
                    list.add(b);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    
    
   /* public Bill getBill(int id){
        String query = "select * from bill where id = ?";
        ArrayList<Object> params = new ArrayList<>();
        params.add(id);
        try {
            ResultSet rs = db.Database.getInstance().execQuery(query, params);
            
        } catch (SQLException ex) {
            Logger.getLogger(BillDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    */
}
