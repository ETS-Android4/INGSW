/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backofficeclient;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Class that manage confirm dialogs.
 * @author GCI16_25
 */
public class ConfirmPanel {
    public static boolean showConfirm(Component parent){
        String[] options = {"No","Yes"};
        /*User has to decide if complete or not the operation*/
        return (JOptionPane.showOptionDialog(parent, "Do you want to complete this operation?", "Confirm operation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]) == 1);
        //return (JOptionPane.showConfirmDialog(parent, "Do you want to complete this operation?", "Confirm operation", JOptionPane.YES_NO_OPTION)==1);
    }
    
    public static void showSuccess(Component parent){
        JOptionPane.showMessageDialog(parent, "Operation successfully completed!");
    }
}

