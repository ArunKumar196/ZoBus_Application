package zobus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class Seater extends Bus{
	void showBusSeatAvailability() throws Exception {
		Database.con=Database.connection();
		String sql="select * from buses where bus_id= ? ";
		PreparedStatement prep= Database.con.prepareStatement(sql); 
		prep.setString(1, bus_id);
		ResultSet rs=prep.executeQuery();
		System.out.printf("		Seats Available\n-------------------------------------------------------------------\n");
		System.out.println("Row No	|	A	B	C");
		System.out.println("---------------------------------");
		while(rs.next()) {
			System.out.println(rs.getInt("s_no")+"	|	"+rs.getString("A")+"	"+rs.getString("B")+"	"+rs.getString("C"));
		}
		System.out.println("-------------------------------------------------------------------");
	}
	boolean seatAvailability(String sNo) throws Exception {
		char ch[]=sNo.toCharArray();
		Statement s=(Statement)Database.con.createStatement();
		String exist="select "+ch[0]+" from buses where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
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
	boolean bookTickets(String name, String seatNo, char gender) throws Exception {
		Database.con=Database.connection();
		Statement s=(Statement)Database.con.createStatement(); 
		char ch[]=seatNo.toCharArray();
		String adjSeat;
		String checkGen;
		int seat=Character.getNumericValue(ch[1]);
		if(seat%2==0) {
			seat=seat-1;
			adjSeat=Character.toString(ch[0])+Integer.toString(seat);
		}else{
			seat=seat+1;
			adjSeat=Character.toString(ch[0])+Integer.toString(seat);
		}
		String bEmail="select * from tempBooking where seatNo = '"+adjSeat+"' and bus_id ='"+bus_id+"'";
		Statement s2=(Statement)Database.con.createStatement(); 
		ResultSet resultSet=s2.executeQuery(bEmail);
		while(resultSet.next()) {
			if(resultSet.getString("bookedByEmail").equals(Customer.email)) {
				String sql="update buses set "+ch[0]+"='"+gender+"' where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
				if(s.executeUpdate(sql)==1)
				{
					addTempBooking(name, seatNo);
				}
				return true;
			}
		}
		checkGen="select "+ch[0]+" from buses where s_no='"+seat+"' and bus_id='"+bus_id+"'" ;
		ResultSet rs=s.executeQuery(checkGen);
		String st = null;
		while(rs.next())
			st=rs.getString(Character.toString(ch[0]));
		if(!((st.equals("M") && gender=='F')||(st.equals("F") && gender=='M'))) {
			String sql="update buses set "+ch[0]+"='"+gender+"' where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
			if(s.executeUpdate(sql)==1)
			{
				addTempBooking(name, seatNo);
			}
			return true;
		}
		return false;
	}
}
