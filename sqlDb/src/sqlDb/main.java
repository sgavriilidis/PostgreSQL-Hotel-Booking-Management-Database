package sqlDb;

import java.util.Scanner;

public class main {
		
		
		public static void Menu() {
			int choice1;
			int choice2;
			sqlFunctions sql=new sqlFunctions();
			
			do {
				System.out.println("----------------\n----------------");
				Scanner scan = new Scanner(System.in);
				
				do {
					System.out.print("1.Connect to a database.\n"
			         + "2.Find a hotel.\n"
			         + "0.Exit.\n"
			         + "\nMake your choice:");
					choice1=scan.nextInt();
					}while(choice1!=1 && choice1!=2 && choice1!=0);
				
				
				if (choice1==1) {
					sql.dbConnect();  //connect to a database
				}else if(choice1==2) {
					String hotel=sql.hotelSearch();  // Choose a hotel by a prefix name
					do {
						System.out.println("----------------\n----------------");
						System.out.print("1.Search for a client.\n"
				         + "2.Search for a client reservation.\n"
				         + "3.Show available rooms.\n"
				         + "0.Exit."
				         + "\nMake your choice:");
						choice2=scan.nextInt();
						
						if (choice2==1) {
							sql.clientSearch(hotel); //choose a client by a prefix last name
						}else if(choice2==2) {
							sql.clientReservations(hotel);  // show reservations of a client
						}else if(choice2==3) {
							sql.showAvailableRooms(hotel); // show available rooms for given time period
						}
						
						
						}while(choice2!=0);
					
				}					
			}while(choice1!=0);
			sql.dbClose();
		}
	
	
		public static void main(String[] args) {
			Menu();
			System.out.println("Program terminated");
			
		}
	
	}