package zobus;
import java.sql.SQLException;
public class Admin extends Account{
	Bus b =new Sleeper();
	void showBus() throws SQLException {
		b.allBuses();
	}
	void busDetails(int busNo) throws SQLException {
		b.busSummary(busNo);
	}
}
