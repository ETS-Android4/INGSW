/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entities.Reading;
import java.util.Collection;

/**
 *
 * @author Riccardo
 */
public class ReadingDAO {
    public void saveReadings(Collection<Reading> readings){
        //TODO
        if(readings==null) throw new IllegalArgumentException("Argument must be not null");
        
    }
}
