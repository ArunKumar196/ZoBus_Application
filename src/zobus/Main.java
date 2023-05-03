package zobus;
import java.util.*;
public class Main {
	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);
		int choice;
		System.out.printf("**************ZOBUS**************\n\n");
		do {
			System.out.println("Enter 1-> Customer Enter->2 Admin");
			choice=sc.nextInt();
			switch(choice) {
			case 1:
				int custChoice;
				Customer customer=new Customer();
				Bus busObj = new Sleeper();
				while(true) {
					System.out.println("Enter 1-> Login	Enter 2-> SignUp Enter 0-> Exit");
					custChoice=sc.nextInt();
					switch(custChoice) {
					case 1:
						System.out.print("Email : ");
						String email=sc.next();
						System.out.print("Password : ");
						String pass=sc.next();
						try {
							if(customer.validateUser(email, pass, "user")) {
								Customer.email=email;
								int custOp=0;
								while(true) {
									System.out.printf("1->Book Tickets \n2->Cancel Ticket\n3->Booking History\n0->Exit\n");
									custOp=sc.nextInt();
									if(custOp==0) {
										System.out.println("-----------------------Thank You-----------------------");
										break;
									}
									switch(custOp) {
									case 1:
										System.out.print("Enter No of Passengers : ");
										int passengers=sc.nextInt();
										System.out.printf("Choose bus display mode \n 1->Both Ac and Non Ac \n 2->Only Ac \n 3->Only Non Ac\n 4->Only Sleeper\n 5->Only Seater\n Your option : ");
										int displayChoice=sc.nextInt();
										Customer.passengers=passengers;
										customer.setOptionOfDisplay(displayChoice);
										customer.display();
										int bus_no;
										char conf;
										while(true) 
										{
											System.out.println("Choose a bus (ex: 1 or 2 etc...)");
											bus_no=sc.nextInt();
											customer.showBus(bus_no);
											System.out.println("Enter y to confirm  n to exit");
											conf=sc.next().charAt(0);
											if(conf=='y' || conf=='Y') {
												
												break;
											}else if(conf=='n' || conf=='N') {
												continue;
											}else
												System.out.println("Enter y or n");
											
										}
										if(conf=='y' || conf=='Y') {
											System.out.println("Enter "+Customer.passengers+" passanger details");
											while(true) {
												for(int i=1;i<=Customer.passengers;i++) {
													System.out.println("passanger No : "+i);
													System.out.print("Name : ");
													String name=sc.next();
													System.out.print("Gender (M / F) : ");
													char gender=sc.next().charAt(0);
													String seatNo;
													
													while(true) {
														System.out.print("Seat No : ");
														seatNo=sc.next();
														customer.setSeatNo(seatNo);
														if(customer.seatAvailable())
															break;
														else
															System.out.println("Seat not available choose a different seat");	
													}
													Customer.map.put(name, new Customer(gender, seatNo));
												}
												if(customer.ticketBooking())
												{
													System.out.println("Total Amount = ₹"+customer.fareCalculation());
													System.out.println("Enter Y to confirm booking N to re-book tickets");
													char confBooking=sc.next().charAt(0);
													if(confBooking=='y' || confBooking=='Y') {
														Customer.map=new LinkedHashMap<>();
														Bus.aList=new ArrayList<>();
														customer.addBookingHistory();
														System.out.println("Booking Confirmed :)");
														break;
													}
													else if(confBooking=='n' || confBooking=='N') {
														for(Map.Entry<String, Customer> en:Customer.map.entrySet()) {
															busObj.truncateTemp(en.getValue().getSeatNo());
														}
														Customer.map=new LinkedHashMap<>();
														continue;
													}
												}
											}
										}
										break;
									case 2:
										char end;
										while(true) {
											busObj.CustomerBooking();
											System.out.print("Enter Bus No : ");
											String busNo=sc.next();
											System.out.print("Enter Seat Numbers (ex.A1(single seat)  or  A1,A2,A3...(Multiple seats)) ");
											String seatNos=sc.next();
											int refund=customer.cancelBooking(busNo,seatNos);
											System.out.println("Cancellation fee deducted : ₹"+refund);
											System.out.println("If you want to continue cancelling seats press Y or N to exit");
											end=sc.next().charAt(0);
											if(end=='y' || end=='Y')
												continue;
											else if(end=='n' || end=='N') {
												Bus.refund=0;
												break;
											}
										}
										break;
									case 3:
										busObj.CustomerBooking();
										break;
									default:
										System.out.println("Enter 1 or 2 or 0");
									}
								}
							}
							else
								System.out.println("Enter Correct Email and Password");
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case 2:
						Account acc=new Account(); 
						System.out.print("Name : ");
						String name = sc.next();
						System.out.print("Email : ");
						String custEmail = sc.next();
						System.out.print("Password : ");
						String password = sc.next();
						acc.setName(name);
						acc.setEmail(custEmail);
						acc.setPassword(password);
						acc.createAccount();
						break;
					case 0:
						break;
					default:
						System.out.println("Enter 1 or 2");
						break;
					}
					if(custChoice==0)
						break;
				}
				break;
			case 2:
				Admin admin = new Admin();
				System.out.print("Email : ");
				String email=sc.next();
				System.out.print("Password : ");
				String pass=sc.next();
				try {
					if(admin.validateUser(email, pass ,"admin")) {
						int adChoice;
						while(true) {
							System.out.println("Enter 1->Bus Details  0->Exit");
							adChoice=sc.nextInt();
							if(adChoice==1) {
								int busNo;
								while(true) {
									admin.showBus();
									System.out.print("Enter the Bus_id to see the summary: ");
									busNo=sc.nextInt();
									admin.busDetails(busNo);
									System.out.print("Enter Y to continue or N to Exit: ");
									char con=sc.next().charAt(0);
									if(con=='y'||con=='Y')
										continue;
									else if(con=='n' || con=='N')
										break;
									else
										System.out.println("Enter Y  or  N");
								}
							}
							if(adChoice==0)
								break;
						}
					}else
						System.out.println("Enter correct email and password");
				}catch(Exception e) {
					System.out.println(e);
				}
			}
		}while(choice!=0);
	}
}
