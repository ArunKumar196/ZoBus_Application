package zobus;
import java.sql.SQLException;
import java.util.*;
public class Customer extends Account{
	static Map<String, Customer> map=new LinkedHashMap<>();
	static int passengers;
	private int  optionOfDisplay;
	private String name;
	private char gender;
	private String seatNo;
	static String email;
	private String passangerName;
	public String getPassangerName() {
		return passangerName;
	}
	public void setPassangerName(String passangerName) {
		this.passangerName = passangerName;
	}
	public Customer() {
	}
	public Customer(char gender, String seatNo) {
		this.gender=gender;
		this.seatNo=seatNo;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getGender() {
		return gender;
	}
	public void setGender(char gender) {
		this.gender = gender;
	}
	public int getOptionOfDisplay() {
		return optionOfDisplay;
	}
	public String getSeatNo() {
		return seatNo;
	}
	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}
	public void setOptionOfDisplay(int optionOfDisplay) {
		this.optionOfDisplay = optionOfDisplay;
	}
	Bus b = new Sleeper();
	void display() throws Exception {
		switch(optionOfDisplay) {
		case 1:
			b.showBuses("both");
			break;
		case 2:
			b.showBuses("ac");
			break;
		case 3:
			b.showBuses("nonAc");
			break;
		case 4:
			b.showBuses("sleeper");
			break;
		case 5:
			b.showBuses("seater");
			break;
		}
		
	}
	Bus bus=null;
	boolean seatAvailable() throws Exception {
		if(b.getBus_type().equals("Ac sleeper")||b.getBus_type().equals("Non Ac sleeper"))
			bus = new Sleeper();
		else
			bus = new Seater();
		
		if(bus.seatAvailability(seatNo))
			return true;
		return false;
	}
	void showBus(int i) throws Exception {
		b.getBusId(i);
		b.getBusDetails();
		if(b.getBus_type().equals("Ac sleeper")||b.getBus_type().equals("Non Ac sleeper")) {
			Sleeper sl = new Sleeper();
			System.out.println("		"+b.getBus_type());
			sl.showBusSeatAvailability();
		}else{
			Seater st = new Seater();
			System.out.println("		"+b.getBus_type());
			st.showBusSeatAvailability();
		}
	}
	boolean ticketBooking() throws Exception {
		System.out.println("Show bus "+b.getBus_type());
		Scanner sc=new Scanner(System.in);
		if(b.getBus_type().equals("Ac sleeper")||b.getBus_type().equals("Non Ac sleeper")){
			bus=new Sleeper();
		}
		else{
			bus=new Seater();
		}
		for(Map.Entry<String, Customer> en:map.entrySet()) {
			if(bus.bookTickets(en.getKey(), en.getValue().getSeatNo(), en.getValue().getGender())) {
				continue;
			}else {
				while(true) {
				System.out.print("Enter different seat no for passanger '"+en.getKey()+"' : ");
				String s=sc.next();
				char gen=en.getValue().getGender();
				map.put(en.getKey(), new Customer(gen,s));
				if(bus.bookTickets(en.getKey(), s, gen))
					break;
				}
			}
		}
		return true;
	}
	void addBookingHistory() throws Exception{
		b.bookingHistory();
	}
	int fareCalculation() throws Exception {
		int sum=b.totalCalculation();
		return sum;
	}
	int cancelBooking(String busNo,String seatNos) throws SQLException {
		return b.cancel(busNo,seatNos);
	}
}
