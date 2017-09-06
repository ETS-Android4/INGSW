/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backofficeclient;


/**
 *
 * @author carlo
 */
public class Bill {
    private int id;
    private Customer customer;
    private double cost;
    
    public Bill(int id, Customer customer, double cost){
        this.id = id;
        this.customer = customer;
        this.cost = cost;
        
    }
    
    public Bill(Customer customer,double cost){
        this.customer = customer;
        this.cost = cost;
    }
    
    public int getId(){
        return id;
    }
    
    public Customer getCustomer(){
        return customer;
    }
    
    public double getCost(){
        return cost;
    }
}