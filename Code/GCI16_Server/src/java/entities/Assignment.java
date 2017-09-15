package entities;

/**
 * Created by Riccardo on 17/08/2017.
 */

public class Assignment {
    private int operatorId;
    private int meterId;
    private String address;
    private String customer;

    public Assignment(int operatorId, int meterId, String address, String customer){
        if(address==null) throw new IllegalArgumentException("Address must not be null");
        if(customer==null) throw new IllegalArgumentException("Customer must not be null");
        this.operatorId = operatorId;
        this.meterId = meterId;
        this.address = address;
        this.customer = customer;
    }

    public int getOperatorId(){
        return operatorId;
    }

    public int getMeterId() {
        return meterId;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomer() {
        return customer;
    }
}
