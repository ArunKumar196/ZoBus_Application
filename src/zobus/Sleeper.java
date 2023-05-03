package zobus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sleeper extends Bus{
	void showBusSeatAvailability() throws Exception {
		Database.con=Database.connection();
		String sql="select * from sleeperBuses where bus_id= ? ";
		PreparedStatement prep= Database.con.prepareStatement(sql); 
		prep.setString(1, bus_id);
		ResultSet rs=prep.executeQuery();
		System.out.printf("		Seats Available\n-------------------------------------------------------------------\n");
		System.out.println("Row No	|	A	B");
		System.out.println("---------------------------------");
		int count=0;
		System.out.println("Lower Deck");
		while(rs.next()) {
			System.out.println(rs.getInt("s_no")+"	|	"+rs.getString("A")+"	"+rs.getString("B"));
			count++;
			if(count==3)
				System.out.println("Upper Deck");
		}
		System.out.println("-------------------------------------------------------------------");
	}
	boolean seatAvailability(String sNo) throws Exception {
		char ch[]=sNo.toCharArray();
		Statement s=(Statement)Database.con.createStatement();
		String exist="select "+ch[0]+" from sleeperbuses where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
		ResultSet recordExist=s.executeQuery(exist);
		boolean avail=false;
		while(recordExist.next())
		{
			if(recordExist.getString(Character.toString(ch[0])).equals("A")) {
				avail=true;
			}
		}
		if(avail)
			return true;
		else
			return false;
	}
	boolean updateBus(String name,String seatNo, char gender) throws SQLException {
		char ch[]=seatNo.toCharArray();
		String sql="update sleeperbuses set "+ch[0]+"='"+gender+"' where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
		Statement s= Database.con.createStatement();
		if(s.executeUpdate(sql)==1)
		{
			addTempBooking(name, seatNo);
			return true;
		}
		return false;
	}
	boolean bookTickets(String name, String seatNo, char gender) throws Exception {
		Database.con=Database.connection();
		Statement s=(Statement)Database.con.createStatement(); 
		char ch[]=seatNo.toCharArray();
		String adjSeat;
		String checkGen;
		int seat=Character.getNumericValue(ch[1]);
		if(seat%3!=0) {
			if((seat+1)%3!=0) {
				System.out.println("!%3"+seat);
				seat=seat+1;
				adjSeat=Character.toString(ch[0])+Integer.toString(seat);
			}else{
				System.out.println("%3"+seat);
				seat=seat-1;
				adjSeat=Character.toString(ch[0])+Integer.toString(seat);
			}
			String bEmail="select * from tempBooking where seatNo = '"+adjSeat+"' and bus_id ='"+bus_id+"'";
			Statement s2=(Statement)Database.con.createStatement(); 
			ResultSet resultSet=s2.executeQuery(bEmail);
			while(resultSet.next()) {
				System.out.println("how");
				if(resultSet.getString("bookedByEmail").equals(Customer.email)) {
					return updateBus(name,seatNo,gender);
				}
			}
			checkGen="select "+ch[0]+" from sleeperbuses where s_no='"+seat+"' and bus_id='"+bus_id+"'" ;
			ResultSet rs=s.executeQuery(checkGen);
			String st = null;
			while(rs.next())
				st=rs.getString(Character.toString(ch[0]));
			if(!((st.equals("M") && gender=='F')||(st.equals("F") && gender=='M'))) {
				System.out.println("but how");
				return updateBus(name,seatNo,gender);
			}
			return false;
		}
		else
		{
			System.out.println("Not possible");
			return updateBus(name,seatNo,gender);
		}
	}
}
