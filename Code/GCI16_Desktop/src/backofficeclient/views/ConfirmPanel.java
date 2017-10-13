package backofficeclient.views;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Class that manage confirm dialog.
 * @author GCI16_25
 */
public class ConfirmPanel {
    /**
     * Shows a JDialog where user has to decide if complete or not the operation.
     * @param parent
     * @return 
     */
    public static boolean showConfirm(Component parent){
        String[] options = {"No","Yes"};
        return (JOptionPane.showOptionDialog(parent, "Do you want to complete this operation?", "Confirm operation", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]) == 1);
    }
}

