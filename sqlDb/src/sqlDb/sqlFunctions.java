package sqlDb;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date.*;

public class sqlFunctions {
	Connection conn;
	

	public sqlFunctions() {
		try {
			Class.forName("org.postgresql.Driver");
			//System.out.println("Driver found!");
		} catch (ClassNotFoundException e) {
			System.out.println("Something went wrong!");
			
		}
	}
		
		
		
		
		
	public void dbConnect() {
		try {
			System.out.println("----------------\n----------------");
			
			Scanner scan = new Scanner(System.in);
			String ip,dbName,username,password;
			
			System.out.print("Give ip:");
			ip=scan.next();
			
			System.out.print("Give name of sql database:");
			dbName=scan.next();
			
			System.out.print("Give username:");
			username=scan.next();
			
			System.out.print("Give password:");
			password=scan.next();
			
			
			conn=DriverManager.getConnection("jdbc:postgresql://"+ip+":5432/"+dbName,username,password); //try to connect with the given database
			System.out.println("Connection is successfull");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
		
	
	public void dbClose() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
		
		
		
	public String hotelSearch() {
		System.out.println("----------------\n----------------");
		try {
			
			Scanner scan = new Scanner(System.in);
			
			ArrayList<String> nameHotel = new ArrayList<String>( );  //ArrayList for names of all the results
			
			PreparedStatement pst=conn.prepareStatement("select * from hotel where name LIKE(?)  "); // select the hotel by prefix name
			
			System.out.print("Search hotels with a prefix you want: ");
			String prefixHotel=scan.next();
			
			pst.setString(1, prefixHotel+"%");
			//System.out.println(pst);
			ResultSet res=pst.executeQuery();
			
			int i=1;
			
			while(res.next()) { // show results of the hotel prefix name
				nameHotel.add(res.getString(2));
				
				System.out.printf("%-3s.Name:%-35s Id:%-10s Stars:%-7s Address:%-30s City:%-25s Country:%-25s Phone:%-20s Fax:%-20s\n",i,res.getString(2),+res.getInt(1),res.getString(3),
				res.getString(4),res.getString(5),res.getString(6),res.getString(7),res.getString(8));
				
				//System.out.println(i+".Name:"+res.getString(2)+"\t\tId:"+res.getInt(1)+"\t\tStars:"+res.getString(3)+"\t\tAddress:"+res.getString(4)+
				//		"\t\tcity:"+res.getString(5)+"\t\tCountry:"+res.getString(6)+"\t\tPhone:"+res.getString(7)+"\t\tFax:"+res.getString(8));
				i++;
			}
			
	        System.out.print("\nChoose a hotel: ");
	        int num = scan.nextInt();  // number of the choosen hotel
	         
	        while(num<0 || num>nameHotel.size()) {
	        	System.out.print("Incorrect value! Please choose the id of the hotel you want: ");
	 	        num = scan.nextInt();
	        }
	        
	        //System.out.println(idHotel.get(num-1));
	        res.close();
			return  nameHotel.get(num-1);  // return the name of the hotel that user choosed
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	public int clientSearch(String hotel) {
		//System.out.println(hotel);
		
		try {
			
			System.out.println("----------------\n----------------");
			ArrayList<Integer> idPerson = new ArrayList<Integer>( );
			
			Scanner scan = new Scanner(System.in);
			System.out.print("Search clients with a last name's prefix you want: ");
			String prefixName=scan.next();
			
			Statement stmt;
			PreparedStatement pst=conn.prepareStatement("select p.lname,c.\"idClient\",c.documentclient 		from client c inner join person p on c.\"idClient\"=p.\"idPerson\" \r\n"
					+ "															inner join hotelbooking hb on c.\"idClient\"=hb.\"bookedbyclientID\" \r\n"
					+ "															inner join roombooking rb on rb.\"hotelbookingID\"=hb.idhotelbooking\r\n"
					+ "															inner join room r on r.\"idRoom\"=rb.\"roomID\" \r\n"
					+ "															inner join hotel h on h.\"idHotel\"=r.\"idHotel\"\r\n"
					+ "															where  h.name=?  and  p.lname LIKE (?)"
					+ "                                                         group by c.\"idClient\",p.lname,p.\"idPerson\""
					+ "                                                         order by p.lname");
			pst.setString(1, hotel);
			pst.setString(2, prefixName+"%");
			ResultSet res = pst.executeQuery();
			
			
			while(res.next()) {
				idPerson.add(res.getInt(2));
				System.out.printf("Last Name:%-15s  Client Id:%-15s  Client's Document%-20s\n",res.getString(1),res.getString(2),res.getString(3));
			}
			
			 int id;
		     boolean exists=false;
		     do {
		    	 System.out.print("\nChoose clien's id: ");
			     id = scan.nextInt();
		    	 for(int j=0;j<idPerson.size();j++) {
		    		 if(id==idPerson.get(j)) {
		    			 exists=true;
		    		 }
		    	 }
		     }while(exists==false);
			
		     res.close();
			return  id;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
		return 0;
		
	}
	
	
	
	
	
	
	
	public void clientReservations(String hotel) {
		try {
				System.out.println("----------------\n----------------");
				Scanner scan = new Scanner(System.in);
				int idClient;
				
				ArrayList<Integer> idRoom= new ArrayList<Integer>( );
				ArrayList<Integer> idhotelbooking = new ArrayList<Integer>( );
				
				System.out.println("Search reservation by client's id:");
				idClient=scan.nextInt();
				
				// find all reservations of a client
				PreparedStatement pst=conn.prepareStatement("select rb.\"hotelbookingID\",rb.\"roomID\",rb.checkin,rb.checkout,rb.rate 	from roombooking rb inner join hotelbooking hb on rb.\"hotelbookingID\"=hb.idhotelbooking \r\n"
						+ "														 				inner join room r on rb.\"roomID\"=r.\"idRoom\"\r\n"
						+ "																		 inner join hotel h on h.\"idHotel\"=r.\"idHotel\"\r\n"
						+ "																		 where h.name=? and hb.\"bookedbyclientID\"=?\r\n"
						+ "																		 order by hb.idhotelbooking,rb.\"roomID\"");
				pst.setString(1, hotel);
				pst.setInt(2, idClient );
				ResultSet res = pst.executeQuery();
				
				int i=1;
				
				if(res.next()) {
				
					while(res.next()) {
						idRoom.add(res.getInt(2));
						idhotelbooking.add(res.getInt(1));
						System.out.printf("%-3s.Hotel booking:%-15s  Room ID:%-15s  Check in:%-20s  Check out:%-20s  Rate:%-10s\n",i,res.getInt(1),res.getInt(2),res.getString(3),res.getString(4),res.getString(5));
						i++;
					}
					System.out.print("\nChoose a hotelbooking to update.(Press 0 to exit):");
					int num = scan.nextInt();
					//System.out.println(idRoom.get(num-1));
					
					if (num==0) {
						return;
					}
					try {
						//choose the roombooking chosen by user
						PreparedStatement pst1=conn.prepareStatement("select * from roombooking rb inner join hotelbooking hb on rb.\"hotelbookingID\"=hb.idhotelbooking "
								+ "                                where hb.idhotelbooking=? and rb.\"roomID\"=? ");
						pst1.setInt(1, idhotelbooking.get(num-1));
						pst1.setInt(2, idRoom.get(num-1) );
						ResultSet res1 = pst1.executeQuery();
						
						
						while(res1.next()) {
							//System.out.printf("Room:%-10s  Check in:%-15s  Check out:%-15s  Rate:%-15s \n",res1.getInt(2),res1.getString(4),res1.getString(5),res1.getString(6));
						
							String checkin,checkout,rate;
							System.out.print("Old check in:"+res1.getString(4)+"    change to:");
							checkin=scan.next();
							
							System.out.print("Old check out:"+res1.getString(5)+"    change to:");
							checkout=scan.next();
							
							
							
							//System.out.print(checkin);
							//System.out.println(checkout);
	
							//System.out.println(idhotelbooking.get(num-1));
							//System.out.println(idRoom.get(num-1));
							
							
							//Update checkin/checkout from roombooking. Rate is automaticly updated.
							PreparedStatement pst2=conn.prepareStatement("update roombooking"
									                                   + " set checkin=?,checkout=?"
									                                   + " where \"hotelbookingID\"=? and \"roomID\"=? ");
							
							Date dateIn =java.sql.Date.valueOf(checkin);
							Date dateOut=java.sql.Date.valueOf(checkout);
							
							pst2.setDate(1,dateIn);
							pst2.setDate(2, dateOut );
							pst2.setInt(3, idhotelbooking.get(num-1));
							pst2.setInt(4, idRoom.get(num-1) );
							//System.out.println(pst2);
							pst2.executeUpdate();	
						}
						
						
						
						
					     res1.close();
					}catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				res.close();
				}else {
					System.out.println("client with id:"+idClient+" does not exist!");
					return ;
				}
				
				
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				
		}
	
	
	
	
	
	public void showAvailableRooms(String hotel) {
		System.out.println("----------------\n----------------");
		
		ArrayList<Integer> idRoom= new ArrayList<Integer>( );
		
		Scanner scan = new Scanner(System.in);
		String checkin,checkout;
		int idHotel=0;
		
		try {
			PreparedStatement pst=conn.prepareStatement("select \"idHotel\" from hotel where  name=?\r\n");
			
			pst.setString(1,hotel);
			
			ResultSet res = pst.executeQuery();
			
			int i=0;
			while(res.next()) {
				idHotel=res.getInt(1)	;
			}
			res.close();

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		System.out.print("Give date for check in:");
		checkin=scan.next();
		
		System.out.print("Give date for check out:");
		checkout=scan.next();
		
		try {
			PreparedStatement pst1=conn.prepareStatement("select r.\"idRoom\",r.number,r.roomtype from room r \r\n"
					                                                                           + " where  r.\"idHotel\"=? and r.\"idRoom\"  not in \r\n"
					                                                                           + " (select r.\"idRoom\" from room r  inner join roombooking rb on r.\"idRoom\"=rb.\"roomID\"\r\n"
					                                                                           + " where r.\"idHotel\"=?\r\n"
					                                                                           + " and((rb.checkout>=? and rb.checkout<=? )\r\n"
					                                                                           + " or (rb.checkin<? and rb.checkout>? )\r\n"
					                                                                           + " or (rb.checkin>=? and rb.checkin<=? ))\r\n"
					                                                                           + " order by r.\"idRoom\")");
			Date dateIn =java.sql.Date.valueOf(checkin);
			Date dateOut=java.sql.Date.valueOf(checkout);
			
			
			pst1.setInt(1,idHotel);
			pst1.setInt(2, idHotel );
			pst1.setDate(3,dateIn);
			pst1.setDate(4, dateOut );
			pst1.setDate(5,dateIn);
			pst1.setDate(6, dateIn );
			pst1.setDate(7,dateIn);
			pst1.setDate(8, dateOut );
			
			ResultSet res1 = pst1.executeQuery();
			
			int i=1;
			while(res1.next()) {
				idRoom.add(res1.getInt(1));
				System.out.printf("%-3s.Room ID:%-20s  Number:%-20s  Room type:%-20s\n",i,res1.getInt(1),res1.getInt(2),res1.getString(3));
				i++;
			}
			res1.close();
			
			int roomID,num;
			System.out.println("\nChoose a room to make a reservation:");
			num=scan.nextInt();
			
			roomID=idRoom.get(num-1);
			System.out.println(roomID);
			
			int personId;
			System.out.println("\n Choose person ID to make the reservation:");
			personId=scan.nextInt();
			
			
			PreparedStatement pst3=conn.prepareStatement("insert into roombooking(\"hotelbookingID\",\"roomID\",\"bookedforpersonID\",checkin,checkout,rate\"\r\n"
													   + "VALUES(  (select  max(\"hotelbookingID\")+1 from hotelbooking),?,?,?,?,0)");
			
			pst3.setInt(1,roomID);
			pst3.setInt(2, personId );
			pst3.setDate(3,dateIn);
			pst3.setDate(4, dateOut );
			
			pst3.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
		

}
