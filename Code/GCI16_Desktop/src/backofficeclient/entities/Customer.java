package backofficeclient.entities;

/**
 * Represents entity customer.
 * @author GCI16_25
 */
public class Customer {
    private String name;
    private String surname;
    
    public Customer(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
    
    public String getName(){
        return name;
    }
    
    public String getSurname(){
        return surname;
    }
}
