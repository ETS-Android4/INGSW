package db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
    private static final int MAXCONNECTIONS = 5;
    private String host = "127.0.0.1";
    private String service = "xe";
    private int port = 1521;
    private String user = "GCI16";
    private String password = "GCI16";
    private Connection conn;
    
    
    private Database(){

        freeConnections = new LinkedBlockingQueue<>(MAXCONNECTIONS);
        for(int i = 0;i < MAXCONNECTIONS;i++){
            try {
                conn = createConnection();
                freeConnections.put(conn);
            } catch (SQLException | InterruptedException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        //occupiedConnections = new HashMap<Thread,Connection>();
    }

    private synchronized Connection createConnection() throws SQLException{
        ods = new OracleDataSource();
        ods.setDriverType("thin");
        ods.setServerName(host);
        ods.setPortNumber(port);
        ods.setUser(user);
        ods.setPassword(password);
        ods.setDatabaseName(service);
        Connection newConnection = ods.getConnection();

        return newConnection;
    }
    
    public synchronized Connection getConnection(){
        Connection c = null;
        try {
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
    
     public ResultSet execQuery(String query, List<Object> params) throws SQLException{
        PreparedStatement st;
        ResultSet rs = null;
        Connection c = getConnection();
        int k = 1;
        st = c.prepareStatement(query);
        if (params != null){
            for (Object p : params ){
                if( p instanceof Date)
                    st.setDate(k,(Date) p);
                if( p instanceof Integer)
                    st.setInt(k, (Integer)p);
                else if (p instanceof Float)
                    st.setFloat(k,(Float)p);
                else if( p instanceof Number)
                    st.setDouble(k, (Double)p);
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
