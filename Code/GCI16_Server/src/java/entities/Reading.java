package entities;

/**
 * The result of a meter reading.
 *
 * @author GCI16_25
 */
public class Reading {
    private int operatorId;
    private int meterId;
    private java.util.Date date;
    private float consumption;

    public int getOperatorId(){
        return operatorId;
    }

    public int getMeterId(){
        return meterId;
    }
    public java.util.Date getDate(){
        return date;
    }

    public float getConsumption(){
        return consumption;
    }

    public Reading(int operatorId, int meterId, java.util.Date date, float consumption){
        if(consumption<0 || date==null) throw new IllegalArgumentException();
        this.operatorId = operatorId;
        this.meterId = meterId;
        this.date = date;
        this.consumption = consumption;
    }

    @Override
    public String toString(){
        return "Operator : " + operatorId + ", Meter ID : " + meterId +
                ", Date : " + date + ", Consumption : " + consumption;
    }
}