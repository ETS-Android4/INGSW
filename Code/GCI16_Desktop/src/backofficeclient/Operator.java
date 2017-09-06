/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

/**
 *
 * @author carlo
 */
public class Operator {
    String user;
    String pass;
    
    public Operator(String user,String pass){
        this.user = user;
        this.pass = pass;
    }
    
    public String getUser(){
        return user;
    }
    
    public String getPass(){
        return pass;
    }
}
