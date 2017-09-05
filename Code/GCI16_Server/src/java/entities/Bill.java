package entities;

/**
 *
 * @author Riccardo
 */
public class Bill {
    private int id;
    private Customer customer;
    private double cost;
    
    public Bill(int id, Customer customer, double cost){
        this.id = id;
        this.customer = customer;
        this.cost = cost;
    }
    
    public int getId(){
        return id;
    }
    
    public Customer getCustomer(){
        return customer;
    }
    
    public double getCost(){
        return cost;
    }
}
