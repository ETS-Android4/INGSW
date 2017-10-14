package gci16.mobile.entities;

/**
 * The result of a meter reading.
 *
 * @author GCI16_25
 */
public class Reading {
    private int operatorId;
    private int meterId;
    private long date;
    private float consumption;

    public int getOperatorId(){
        return operatorId;
    }

    public int getMeterId(){
        return meterId;
    }
    
    public long getDate(){
        return date;
    }

    public float getConsumption(){
        return consumption;
    }

    public Reading(int operatorId, int meterId, float consumption, long date){
        if(consumption<0 || date<0) throw new IllegalArgumentException();
        this.operatorId = operatorId;
        this.meterId = meterId;
        this.date = date;
        this.consumption = consumption;
    }
    
    public Reading(int operatorId, int meterId, float consumption){
        this(operatorId, meterId, consumption, System.currentTimeMillis());
    }

    @Override
    public String toString(){
        return "Operator : " + operatorId + ", Meter ID : " + meterId +
                ", Date : " + date + ", Consumption : " + consumption;
    }
}