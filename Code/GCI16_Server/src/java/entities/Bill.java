package entities;

/**
 *
 * @author Riccardo
 */
public class Bill {
    private int id;
    private Customer customer;
    private double cost;
    private int year;
    private int trimester;
    
    public Bill(int id, Customer customer, double cost){
        this.id = id;
        this.customer = customer;
        this.cost = cost;
        
    }
    
    public Bill(Customer customer,double cost,int year,int trimester){
        this.customer = customer;
        this.cost = cost;
        this.year = year;
        this.trimester = trimester;
    }
    
    public int getId(){
        return id;
    }
    
    public Customer getCustomer(){
        return customer;
    }
    
    public int getTrimester(){
        return trimester;
    }
    
    public int getYear(){
        return year;
    }
    
    public double getCost(){
        return cost;
    }
    
    public String getName(){
        return customer.getName();
    }

    public String getSurname(){
        return customer.getSurname();
    }
}
