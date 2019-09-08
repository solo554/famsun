package  com.famsun.rac.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;
public class ConnectionPool {
	 private Vector<Connection> pool;
	 private String driverClassName="oracle.jdbc.driver.OracleDriver";
	 private String url="jdbc:oracle:thin:@172.18.10.66:1521:mdm";
	
	 //private String url = "jdbc:oracle:thin:@172.18.10.66:1521:tciman";//Orcle�Ķ˿ں�Ϊ1521
	 private String username="tciman";
	 private String password="tciman123x";
	 //private String password="tciman";
	 private int poolSize = 10;
	 private static ConnectionPool instance = null;
	 private ConnectionPool() {
	 
		 Init();
	 }
	 private void addConnection() {
	  Connection conn = null;
	  for (int i = 0; i < poolSize; i++) {
	   try {
	    Class.forName(driverClassName).newInstance();
	    conn = DriverManager.getConnection(url, username, password);
	    pool.add(conn);
	   } catch (Exception e) {
	    e.printStackTrace();
	   }
	  }
	 }
	 private void Init() {
	  // TODO Auto-generated method stub
	  pool = new Vector<Connection>(poolSize);
	
	  addConnection();
	 }
	 
	 public static ConnectionPool getInstance() {
	  if (instance == null) {
	   instance = new ConnectionPool();
	  }
	  return instance;
	 }
	 public synchronized void release(Connection conn) {
	  pool.add(conn);
	 }
	 public synchronized void closePool() {
	  for (int i = 0; i < pool.size(); i++) {
	   try {
	    ((Connection) pool.get(i)).close();
	   } catch (Exception e) {
	    e.printStackTrace();
	   }
	   pool.remove(i);
	  }
	 }
	 public synchronized Connection getConnection() {
	  if (pool.isEmpty()) {	 
		  Init();
	  }
	  
	  if (pool.size() > 0) {
	   Connection connection = null;
	   connection = pool.get(0);
	   pool.remove(connection);
	   return connection;
	  } 
	  return null;
	 }
 
}