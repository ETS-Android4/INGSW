package gci16.mobile.entities;

/**
 * A task assigned to a operator.
 *
 * @author Riccardo
 */
public class Assignment {
    private int operatorId;
    private String address;
    private int meterId;
    private String customer;

    public Assignment(int operatorId, int meterId, String address, String customer){
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

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Assignment)) return false;
        Assignment a = (Assignment) o;
        return a.meterId==this.meterId;
    }

    @Override
    public int hashCode() {
        return meterId;
    }
}
