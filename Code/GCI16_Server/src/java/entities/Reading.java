package entities;


/**
 * Created by Riccardo on 17/08/2017.
 */

public class Reading {
    private int operatorId;
    private int meterId;
    private String date;
    private float consumption;
    
    public int getOperatorId(){ return operatorId; }
    public int getMeterId(){ return meterId; }
    public String getDate(){ return date; }
    public float getConsumption(){ return consumption; }
    
    public Reading(int operatorId, int meterId, String date, float consumption){
        if(consumption<0 || date==null /* || invalid date*/) throw new IllegalArgumentException();
        this.operatorId = operatorId;
        this.meterId = meterId;
        this.date = date;
        this.consumption = consumption;
    }
    
    @Override
    public String toString(){
        return "Op : " + operatorId + ", met : " + meterId + 
                ", date : " + date + ", consumption : " + consumption;
    }
}
