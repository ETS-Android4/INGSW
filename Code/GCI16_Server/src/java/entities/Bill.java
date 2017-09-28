package entities;

/**
 * Represents entity Bill
 * @author GCI16_25
 */
public class Bill {
    private int id;
    private Customer customer;
    private double cost;
    private int year;
    private int trimester;
    
    /**
     * Creates new instance of a bill.
     * 
     * @param id bill's id.
     * @param customer person who has to pay this bill.
     * @param cost bill's amount.
     * @param year bill's reference year. 
     * @param trimester bill's trimester year.
     */
    public Bill(int id, Customer customer, double cost,int year,int trimester){
        this(customer,cost,year,trimester);
        this.id = id;  
    }
    /**
     * Costructor without id
     * 
     * @param customer
     * @param cost
     * @param year
     * @param trimester 
     */
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
