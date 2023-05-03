package zobus;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
abstract class Bus {
	static ArrayList<String> aList=new ArrayList<>();
	static String bus_id;
	private String bus_type;
	static int fare;
	private int total;
	static int f=0,refund=0;
	public String getBus_type() {
		return bus_type;
	}
	public void setBus_type(String bus_type) {
		this.bus_type = bus_type;
	}
	public void getBusId(int i) {
		bus_id=aList.get(i-1);
	}
	void allBuses() throws SQLException {
		String sql= "select * from bus_details";
		Statement s=(Statement)Database.con.createStatement();
		ResultSet rs=s.executeQuery(sql);
		System.out.println(" 				Bus Details");
		System.out.println("Bus_id		Bus type		Seats Available		Fare Per Seat");
		System.out.println("------------------------------------------------------------------------------------");
		while(rs.next()) {
			System.out.println(rs.getString("bus_id")+"		"+rs.getString(2)+"		"+rs.getInt("seats_available")+"			"+rs.getInt("fare"));
		}
		System.out.println("------------------------------------------------------------------------------------");
	}
	void getBusDetails() throws Exception{
		Database.con=Database.connection();
		String bus="select * from bus_details where bus_id= ? ";
		PreparedStatement prep=Database.con.prepareStatement(bus);
		prep.setString(1, bus_id);
		prep.addBatch();
		ResultSet rs =prep.executeQuery();
		while(rs.next()) {
			bus_type=rs.getString("bus_type");
			fare=rs.getInt("fare");
		}
	}
	void printingFunction(String sql) throws Exception {
		Database.con=Database.connection();
		PreparedStatement prep =Database.con.prepareStatement(sql); 
		ResultSet rs=prep.executeQuery();
		System.out.printf("Bus Available\n-------------------------------------------------------------------\n");
		System.out.println("S_no		Bus type		Seats Available		Fare Per Seat");
		int i=1;
		while(rs.next()) {
			if(Customer.passengers <= rs.getInt("seats_available")) {
				aList.add(rs.getString("bus_id"));
				System.out.println(i++ +"		"+rs.getString(2)+"		"+rs.getInt("seats_available")+"			"+rs.getInt("fare"));
			}
		}
		System.out.println("-------------------------------------------------------------------");
	}
	void showBuses(String option) throws Exception{
		String sql=null;
		switch(option) {
		case "both":
			sql= "select * from bus_details";
			break;
		case "ac":
			sql= "select * from bus_details where bus_type='Ac sleeper' or bus_type='Ac seater'";
			break;
		case "nonAc":
			sql= "select * from bus_details where bus_type='Non Ac sleeper' or bus_type='Non Ac seater'";
			break;
		case "sleeper":
			sql="select * from bus_details where bus_type like '%sleeper'";
			break;
		case "seater":
			sql="select * from bus_details where bus_type like '%seater'";
			break;
		}
		if(sql!=null) {
			printingFunction(sql);
		}
	}
	static void addTempBooking(String name, String seat) throws SQLException {
		String sql="insert into tempBooking values(?, ?, ?, ?, ?)";
		PreparedStatement prep= Database.con.prepareStatement(sql);
		prep.setString(1, Customer.email);
		prep.setString(2, bus_id);
		prep.setString(3, name);
		prep.setString(4, seat);
		prep.setInt(5, fare);
		prep.addBatch();
		prep.execute();
	}
	int totalCalculation() throws Exception {
		String sql="Select sum(farePerSeat) as total from tempBooking where bookedByEmail= ? ";
		PreparedStatement prep= Database.con.prepareStatement(sql);
		prep.setString(1, Customer.email);
		prep.addBatch();
		ResultSet rs=prep.executeQuery();
		while(rs.next()){
			total=rs.getInt("total");
		}
		return total;
	}
	void bookingHistory() throws SQLException {
		String sql="Select * from tempBooking where bookedbyemail= ? ";
		PreparedStatement prep =Database.con.prepareStatement(sql);
		prep.setString(1, Customer.email);
		prep.addBatch();
		ResultSet rs=prep.executeQuery();
		while(rs.next()) {
			String insert="insert into booking_details(bookedByEmail, bus_id, name, seatNo, fareperseat ,bus_type) values(?, ?, ?, ?, ?, ?)";
			PreparedStatement prep1 =Database.con.prepareStatement(insert);
			prep1.setString(1, rs.getString("bookedByEmail"));
			prep1.setString(2, rs.getString("bus_id"));
			prep1.setString(3, rs.getString("name"));
			prep1.setString(4, rs.getString("seatNo"));
			prep1.setInt(5, rs.getInt("fareperseat"));
			prep1.setString(6, bus_type);
			prep1.addBatch();
			prep1.execute();
		}
		updateSeatAvailable();
		String s="truncate table tempBooking";
		Statement s1=(Statement)Database.con.createStatement();
		s1.execute(s);
	}
	void updateSeatAvailable() throws SQLException {
		String sql="update bus_details set seats_available = seats_available - ? where bus_id= ? ";
		PreparedStatement prep =Database.con.prepareStatement(sql);
		prep.setInt(1, Customer.passengers);
		prep.setString(2, bus_id);
		prep.addBatch();
		prep.executeUpdate();
	}
	int cancel(String busNo, String seatNos) throws SQLException {
		String seats[] = seatNos.split(",");
		for(int i=0;i<seats.length;i++) {
			String seat="select bus_id,name ,seatNo,bus_type,fareperseat from booking_details where bookedByEmail = ? and seatNo = ? and cancel = ? and  bus_id= ?";
			PreparedStatement prep =Database.con.prepareStatement(seat);
			prep.setString(1, Customer.email);
			prep.setString(2, seats[i]);
			prep.setString(3, "false");
			prep.setString(4, busNo);
			prep.addBatch();
			ResultSet rs =prep.executeQuery();
			boolean flag=false;
			while(rs.next()) {
				flag=true;
				bus_id=rs.getString("bus_id");
				String name=rs.getString("name");
				char ch[]=rs.getString("seatNo").toCharArray();
				bus_type=rs.getString("bus_type");
				fare=rs.getInt("fareperseat");
				String updateSeat=null;
				if(bus_type.equals("Ac sleeper")||bus_type.equals("Non Ac sleeper")) {
					updateSeat="update sleeperBuses set "+ch[0]+"='A' where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
				}
				else
					updateSeat="update buses set "+ch[0]+"='A' where s_no='"+ch[1]+"' and bus_id='"+bus_id+"'";
				Statement s1=(Statement)Database.con.createStatement();
				if(s1.executeUpdate(updateSeat)==1)
				{
					int cancelFee=0;
					if(bus_type.equals("Ac sleeper") || bus_type.equals("Ac seater"))
						cancelFee=fare-fare/2;
					else if(bus_type.equals("Non Ac sleeper") || bus_type.equals("Non Ac seater"))
						cancelFee=fare-fare/4;
					refund=refund+cancelFee;
					String sql="update booking_details set farePerSeat= ? , cancel= ? where name= ? and bookedByEmail= ? ";
					PreparedStatement prep1 =Database.con.prepareStatement(sql);
					prep1.setInt(1, cancelFee);
					prep1.setString(2, "true");
					prep1.setString(3, name);
					prep1.setString(4, Customer.email);
					prep1.addBatch();
					if(prep1.executeUpdate()==1) {
						String updateBus="update bus_details set seats_available = seats_available + 1 where bus_id= ? ";
						PreparedStatement prep2 =Database.con.prepareStatement(updateBus);
						prep2.setString(1, bus_id);
						prep2.addBatch();
						prep2.execute();
					}
				}
				else
					System.out.println("not updated");
			}
			if(!flag)
				System.out.println("No records exist");
		}
		return refund;
	}
	void busSummary(int busNo) throws SQLException {
		String busType=null;
		int seatAvailable=0;
		String sql="Select bus_type,seats_available from bus_details where bus_id = ? ";
		PreparedStatement prep =Database.con.prepareStatement(sql);
		prep.setInt(1, busNo);
		prep.addBatch();
		ResultSet rs = prep.executeQuery();
		while(rs.next()) {
			busType=rs.getString("bus_type");
			seatAvailable=rs.getInt("seats_available");
		}
		int seatFilled=12-seatAvailable;
		System.out.println(busType);
		System.out.println("Seats Filled : "+seatFilled);
		int cCount=0;
		String cancelCount="select count(cancel) as cancelCount from booking_details where cancel= ? and bus_id= ? ";
		prep =Database.con.prepareStatement(cancelCount);
		prep.setString(1, "true");
		prep.setInt(2,  busNo);
		prep.addBatch();
		ResultSet cancel=prep.executeQuery();
		while(cancel.next()) {
			cCount=cancel.getInt("cancelCount");
		}
		int tFare=0;
		String totalFare="select sum(farePerSeat) as fare from booking_details where bus_id = ? ";
		prep =Database.con.prepareStatement(totalFare);
		prep.setInt(1, busNo);
		prep.addBatch();
		ResultSet fare=prep.executeQuery();
		while(fare.next()) {
			tFare=fare.getInt("fare");
		}
		System.out.println("Total Fare Collected : ₹"+tFare+"( "+seatFilled+" tickets + "+cCount+" Cancellation ) ");
	}
	void truncateTemp(String seatNo) throws SQLException {
		Statement s=(Statement)Database.con.createStatement();
		char[] ch=seatNo.toCharArray();
		String avail="A";
		String updateSeat="update buses set "+ch[0]+"='"+avail+"' where s_no='"+ch[1]+"'";
		s.executeUpdate(updateSeat);
		String sql= "truncate table tempBooking";
		s.execute(sql);
	}
	void CustomerBooking() throws SQLException {
		String sql ="select * from booking_details where bookedByEmail= ? and cancel= ? order by bus_id";
		PreparedStatement prep =Database.con.prepareStatement(sql);
		prep.setString(1, Customer.email);
		prep.setString(2, "false");
		prep.addBatch();
		ResultSet rs = prep.executeQuery();
		System.out.println("Your Booking Details.. ");
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("BusNo		Bus		Name		Seat No");
		while(rs.next()) {
			System.out.println(rs.getString("bus_id")+"		"+rs.getString("bus_type")+"	"+rs.getString("name")+"		"+rs.getString("seatNo"));
		}
		System.out.println("-----------------------------------------------------------------------");
	}
	abstract void showBusSeatAvailability() throws Exception;
	abstract boolean seatAvailability(String sNo) throws Exception;
	abstract boolean bookTickets(String name, String seatNo, char gender) throws Exception;
}