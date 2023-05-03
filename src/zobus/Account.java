package zobus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Account{
	private String name;
	private String email;
	private String password;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	boolean validateUser(String email, String password, String role) throws Exception {
		Database.con = Database.connection();
		String sql="Select * from signup where email=? and password=? ";
		PreparedStatement prep =Database.con.prepareStatement(sql);
		prep.setString(1, email);
		prep.setString(2, password);
		prep.addBatch();
		ResultSet rs=prep.executeQuery();
		while(rs.next())
		{
			if(rs.getString("role").equals(role))
				return true;
		}
		return false;
	}
	void createAccount() {
		try {
			Database.con = Database.connection();
			if(Database.con!=null) {
				String sql="Insert into signup(name,email,password) values(?, ?, ?)";
				PreparedStatement prep =Database.con.prepareStatement(sql);
				prep.setString(1, name);
				prep.setString(2, email);
				prep.setString(3, password);
				prep.addBatch();
				if(prep.executeUpdate()==1)
					System.out.println("Sign up successfull");
				else
					System.out.println("Somethin went wrong");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
