package zobus;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
	static Connection con=null;
	private Database() {
		
	}
	public static Connection connection() throws Exception{
		if(con==null)
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/<database-name>","<mysql-user-name>","<mysql-password>");
		}
		return con;
	}
}
