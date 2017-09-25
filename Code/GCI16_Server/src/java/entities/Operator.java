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
    public static final int TYPE_BACKOFFICE = 1;
    public static final int TYPE_READINGS = 2;
    String user;
    String pass;
    int type;
    
    public Operator(String user,String pass, int type){
        if(type<1 || type>2) throw new IllegalArgumentException();
        this.user = user;
        this.pass = pass;
        this.type = type;
    }
    
    public String getUser(){
        return user;
    }
    
    public String getPass(){
        return pass;
    }
}
