import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import oracle.jdbc.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riccardo
 */
public class Database {
    private static Database instance;
    private final LinkedList<Connection> freeConnections;
    private String dbUrl;           // Il nome del database
    private String dbDriver;        // Il driver del database
    private String dbUser;
    private String dbPassword;      // Access password
    
    private Database(){
        freeConnections = new LinkedList<Connection>(){
                    @Override
                    public synchronized Connection removeFirst(){
                        return super.removeFirst();
                    }

                    @Override
                    public synchronized boolean add(Connection c){
                        return super.add(c);
                    }

                    @Override
                    public synchronized boolean isEmpty(){
                        return super.isEmpty();
                    }
                };
        
    }

    public synchronized Connection getConnection(){
        Connection conn = null;
        if(freeConnections.isEmpty()){
            while(conn==null){
                try {
                 DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                } catch (SQLException ex) {
                  //Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        else{
            conn = freeConnections.removeFirst();
            try {
                if(!conn.isValid(0))
                    conn = getConnection();
            } catch (SQLException ex) {
                conn = getConnection();
            }
        }
        return conn;
    }
    
    public static synchronized Database getInstance(){
        if(instance==null){
            instance = new Database();
      
        }
        return instance;
    }

    private static class HashSet {

        public HashSet() {
        }
    }
    
}
