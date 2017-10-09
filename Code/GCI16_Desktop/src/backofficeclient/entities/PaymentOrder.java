package backofficeclient.entities;

/**
 * Represents entity paymentOrder
 * @author GCI16_25
 */
public class PaymentOrder {
    private int id;
    private Integer protocol;
    private Status status = Status.NOTISSUED;
    private Bill bill;
    private double amount;
    
    /*Enumeration of possible payment order status.*/
    public enum Status{
        NOTISSUED, ISSUED, SUSPENDED, PAID, NOTPERTINENT, NOTIFIED;
    }
    
    /**
     * Creates a new instance of payment order
     * 
     * @param id payment order's id.
     * @param protocol protocol number.
     * @param status indicates the status.
     * @param bill indicates the corresponding bill.
     * @param amount payment order's amount.
     */
    public PaymentOrder(int id, Integer protocol, Status status, Bill bill,double amount){
        this.protocol = protocol;
        this.status = status;
        this.bill = bill;
        this.id = id;
        this.amount = amount;
    }
    
    public boolean isNextStatus(Status newStatus){
        switch (newStatus){
            case ISSUED:
                if(status.equals(Status.NOTISSUED)) return true;
            
            case PAID:
                if(status.equals(Status.NOTIFIED)) return true;
                
            case SUSPENDED:
                if(status.equals(Status.NOTIFIED)) return true;
            
            case NOTIFIED:
                if(status.equals(Status.ISSUED)) return true;
            
            case NOTPERTINENT:
                if(status.equals(Status.SUSPENDED)) return true;
        }
        return false;
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
    
    public void setStatus(Status status){
        this.status = status;
    }
    
    public void setProtocol(Integer protocol){
        this.protocol = protocol;
    }
}