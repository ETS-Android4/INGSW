package backofficeclient;

/**
 *
 * @author Mattia
 */
public class PaymentOrder {
    private int id;
    private Integer protocol;
    private Status status = Status.NOTISSUED;
    private Bill bill;
    //private String debtor;
    //private int year;
    //private int trimester;
    private double amount;
    
    public enum Status{
        NOTISSUED, ISSUED, SUSPENDED, PAID, NOTPERTINENT, NOTIFIED;
    }
    
  /*  public PaymentOrder(int id, String debtor, Integer protocol, int year, int trimester, double amount, Status status){
        this.id = id;
        this.debtor = debtor;
        this.amount = amount;
        this.protocol = protocol;
        this.trimester = trimester;
        this.year = year;
        this.status = status;
        
    }*/
  
    
    public PaymentOrder(int id, Integer protocol, Status status, Bill bill){
        this(protocol, status, bill);
        this.id = id;
   }
    
    public PaymentOrder(Integer protocol, Status status, Bill bill){
        this.protocol = protocol;
        this.status = status;
        this.bill = bill;
    }
    
    public Bill getBill(){
        return bill;
    }

    public int getId(){
        return id;
    }
            
    
    public Integer getProtocol(){
        return protocol;
    }
    

    
    public Status getStatus(){
        return status;
    }
    
    public String getDebtor(){
        String name = bill.getName(); 
        String surname = bill.getSurname();
        return (name + " " +surname); 
    }
  
    public int getTrimester(){
        return bill.getTrimester();
    }
    
    public int getYear(){
        return bill.getYear();
    }
    
    public double getAmount(){
        return amount;
    }

    
    
}
