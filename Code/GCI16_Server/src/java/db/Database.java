package db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import oracle.jdbc.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author Riccardo
 */
public class Database {
    private static Database instance;
    private OracleDataSource ods;
    private final LinkedBlockingQueue<Connection> freeConnections;
    //private final HashMap<Thread,Connection> occupiedConnections;
    private static final int MAXCONNECTIONS = 10;
    private String host = "127.0.0.1";
    private String service = "xe";
    private int port = 1521;
    private String user = "CARLO";
    private String password = "carlo";
    private Connection conn;
    
    
    private Database(){

        freeConnections = new LinkedBlockingQueue<Connection>(MAXCONNECTIONS);
        for(int i = 0;i < MAXCONNECTIONS;i++){
            conn = createConnection();
           if(freeConnections == null )
                System.out.println("\n\n\n\nFree connections null\n\n\n\n");
            
            try {
                freeConnections.put(conn);
            } catch (InterruptedException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        //occupiedConnections = new HashMap<Thread,Connection>();
        
        
    }

    private synchronized Connection createConnection(){
        System.out.println("\n\n\ncreate Connection\n\n\n");
        Connection conn = null;
        try {
            ods = new OracleDataSource();
            ods.setDriverType("thin");
            ods.setServerName(host);
            ods.setPortNumber(port);
            ods.setUser(user);
            ods.setPassword(password);
            ods.setDatabaseName(service);
            System.out.println("\n\n\n\nPrima di getConnection\n\n\n\n");
            conn = ods.getConnection();
            System.out.println("\n\n\n\nDopo di getConnection\n\n\n\n");
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public synchronized Connection getConnection(){
        Connection c = null;
        try {
            if(freeConnections.isEmpty()) System.out.println("CODA VUOTA");
            c = freeConnections.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
    
    public static synchronized Database getInstance(){
        if(instance==null){
            instance = new Database();
        }
        return instance;
    }
    
     public ResultSet execQuery(String query, ArrayList<Object> params) throws SQLException{
        PreparedStatement st;
        ResultSet rs = null;
        Connection c = getConnection();
        int k = 1;
        st = c.prepareStatement(query);
        if (params != null){
            for (Object p : params ){
                if( p instanceof Integer)
                    st.setInt(k, (Integer)p);
                else if (p instanceof Float){
                    st.setFloat(k,(Float)p);
                }
                else if( p instanceof Number){
                    st.setDouble(k, (Double)p);
                }
                else 
                    st.setString(k, (String)p);
                k++;
            }
        }
        try{
            rs = st.executeQuery();
            freeConnections.put(c);
        }catch(SQLException e){
            System.err.println("query = " + query);
            throw e;
        } catch (InterruptedException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    
    
}
