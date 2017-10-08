package backofficeclient.entities;

/**
 * Represents a operator of the company.
 * 
 * @author GCI16_25
 */
public class Operator {
    public static final int TYPE_BACKOFFICE = 1;
    public static final int TYPE_READINGS = 2;
    private String identifier;
    private String pass;
    private int type;
    
    /**
     * Creates a new instance of the class.
     * 
     * @param identifier username or id of the operator
     * @param pass password of the operator
     * @param type type from class constant values
     * @throws IllegalArgumentException if type is not taken from classe's constant values
     */
    public Operator(String identifier, String pass, int type){
        if(type<1 || type>2) throw new IllegalArgumentException();
        this.identifier = identifier;
        this.pass = pass;
        this.type = type;
    }
    
    public String getIdentifier(){
        return identifier;
    }
    
    public String getPass(){
        return pass;
    }

    public int getType() {
        return type;
    }
}
