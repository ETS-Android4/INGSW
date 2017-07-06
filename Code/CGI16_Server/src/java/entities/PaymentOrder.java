package entities;

/**
 *
 * @author Mattia
 */
public class PaymentOrder {
    private int id;
    private Integer protocol;
    private Status status = Status.NOTISSUED;
    private Bill bill;
    
    public enum Status{
        NOTISSUED, ISSUED, SUSPENDED, PAID, NOTPERTINENT, NOTIFIED;
    }
    
    
    public PaymentOrder(int id, Integer protocol, Status status, Bill bill){
        this(protocol, status, bill);
        this.id = id;
    }
    
    public PaymentOrder(Integer protocol, Status status, Bill bill){
        this.protocol = protocol;
        this.status = status;
        this.bill = bill;
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
    
    public Bill getBill(){
        return bill;
    }
}
