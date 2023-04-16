// Division 1 Ski Alpine Race Schedule
// Lana & Denise Database Project


import java.sql.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;

public class RaceSchedule implements AutoCloseable{
		
		// Step 1: Create Token
	
	    private static final String DB_NAME = "dcd0130_project";
	    private static final String DB_USER = "token_a9a4";
	    private static final String DB_PASSWORD = "XPpDh2Knk3ChUkH9";
	    
	    // Step 2: Write SQL Queries
	    
	    // show all locations and the number of races
	    private static final String SQL_QUERY_ALL_LOCATIONS_NUMBER_RACES = 
	    		 "SELECT Location.resort, COUNT(Race.id) AS numberOfRaces\n"
	    				    + "FROM Race\n"
	    				    + "INNER JOIN Location ON Location.locationId = Race.location_id\n" 
	    				    + "GROUP BY Location.resort\n";
	    
	    // Show race by location
	    private static final String SQL_QUERY_LOCATION =
	    		"SELECT locationId FROM Location\n"
	    			+ "WHERE Location.resort LIKE ?;\n";
	    
	    // Add new race
	    private static final String SQL_QUERY_ADD_NEW_RACE_LOCATION = 
	    		"INSERT INTO Location (resort, state, elevation) VALUES (?,?,?);\n";
	    
	    // Show races by location
	    private static final String SQL_QUERY_RACE_BY_LOCATION = 
				    "SELECT Race.date, Race.sex, Race.discipline, Race.status"
				    + " FROM Race\n"
				    + "INNER JOIN Location ON Location.locationId = Race.location_id\n"
				    + "WHERE Location.resort = ?";
	    
	    // Count Races at resort
	    private static final String SUM_TOTAL_RACES_AT_LOCATION = 
			    "SELECT COUNT(Race.id) AS id\n"
			    + "FROM Race\n"
			    + "INNER JOIN Location ON Location.locationId = Race.location_id\n" 
			    + "WHERE Location.resort LIKE ?\n"
			    + "GROUP BY Location.resort\n";
	    
	    // Search for a race
	    private static final String SQL_QUERY_RACE =
	    		"SELECT Race.date, Race.sex, Race.discipline, Race.status \n"
	    				+ "FROM Race\n"
	    				+ "WHERE Race.sex LIKE ?;\n";
	    // Count total races by sex
	    private static final String SQL_QUERY_RACE_BY_SEX =
	    		"SELECT COUNT(Race.id) AS raceCountSex \n"
	    			    + "FROM Race\n"
	    			    + "INNER JOIN Location ON Location.locationId = Race.location_id\n"
	    			    + "WHERE Race.sex LIKE ?;";
	    
	    // Update status of race
	    	// A active: no change
	    	// C cancelled: delete race
	    private static final String SQL_CHANGE_STATUS_CANCELLED =
	    		" UPDATE Race\n"
	    			    + "INNER JOIN Location ON Location.locationId = Race.location_id\n"
	    				+ "SET status = 'C' \n"
	    				+ "WHERE Location.resort LIKE ? AND Race.date LIKE ? AND Race.sex LIKE ?;\n";
	    
	    // Query cancelled races
	    private static final String SQL_QUERY_CANCELLED_RACES =
	    		"SELECT Race.date, Race.sex, Race.status "
	    				+ "FROM Race\n"
	    				+ "WHERE Race.status LIKE 'C';\n";
	    
		// R relocated: update location
	    private static final String SQL_CHANGE_STATUS_RELOCATED =
	    		" UPDATE Race\n"
	    			    + "INNER JOIN Location ON Location.locationId = Race.location_id\n"
	    				+ "SET status = 'R' \n"
	    				+ "WHERE Location.resort LIKE ? AND Race.date LIKE ? AND Race.sex LIKE ?;\n";
	    
	    // Query relocated races
	    private static final String SQL_QUERY_RELOCATED_RACES =
	    		"SELECT Race.date, Race.sex, Race.status "
	    				+ "FROM Race\n"
	    				+ "WHERE Race.status LIKE 'R';\n";

	    // Delete Contact
	    private static final String SQL_QUERY_DROP_CONTACT =
	       		"DELETE \n"
	       				+ "FROM Contact\n"
	       				+ "WHERE Contact.phone LIKE ?;\n";

	    
	    // Query available contacts
	    private static final String SQL_QUERY_ALL_CONTACTS = 
	    		 "SELECT Location.resort, Contact.phone, Contact.email\n"
	    				 + "FROM Location\n"
	    				 + "INNER JOIN Race ON Race.location_id = Location.locationId\n"
	    				 + "INNER JOIN Contact ON Contact.contactId =Race.contact_id\n"
	    				 + "GROUP BY Location.resort\n";    
	    
	    private static final String SQL_QUERY_HIGEST_ELEVATION = 
	    		 "SELECT state, resort, elevation\n"
	    		 + "FROM Location\n"
	    		 + "WHERE elevation = (\n"
	    		 + "	SELECT MAX(elevation)\n"
	    		 + "	FROM Location)\n";
	    		 				 
	    
	    // Declare one of these for every query your program will use.
	    // add a new private static final String and a PreparedStatement for each one
	    private PreparedStatement allLocations;
	    private PreparedStatement queryLocation;
	    private PreparedStatement addNewRaceLocation; 
	    private PreparedStatement queryRaceLocation;
	    private PreparedStatement sumRaces;
	    private PreparedStatement queryRace;
	    private PreparedStatement cancelRace;
	    private PreparedStatement relocateRace;
	    private PreparedStatement queryTotalRacesbySex;
	    private PreparedStatement dropContact;
	    private PreparedStatement allContacts;
	    private PreparedStatement queryCancelledRaces;
	    private PreparedStatement queryRelocatedRaces;
	    private PreparedStatement queryHigestElevation;
	    
	    // Step 4: Connection information to use
	    private final String dbHost;
	    private final int dbPort;
	    private final String dbName;
	    private final String dbUser, dbPassword;

	    // Step 5: The database connection
	    private Connection connection;
	    
	    // Step 6: instance Variable
	    private String resort;

	    public RaceSchedule(String dbHost, int dbPort, String dbName,
	            String dbUser, String dbPassword) throws SQLException {
	        this.dbHost = dbHost;
	        this.dbPort = dbPort;
	        this.dbName = dbName;
	        this.dbUser = dbUser;
	        this.dbPassword = dbPassword;

	        connect();
	    }

	    private void connect() throws SQLException {
	        // URL for connecting to the database: includes host, port, database name, user, password
	        final String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
	                dbHost, dbPort, dbName,
	                dbUser, dbPassword);
	 
	        // Attempt to connect, returning a Connection object if successful
	        this.connection = DriverManager.getConnection(url);
	        
	        // Step 7: Prepare the statements that we will execute
	        this.allLocations= this.connection.prepareStatement(SQL_QUERY_ALL_LOCATIONS_NUMBER_RACES); 
	        this.queryLocation= this.connection.prepareStatement(SQL_QUERY_LOCATION); 
	        this.addNewRaceLocation= this.connection.prepareStatement(SQL_QUERY_ADD_NEW_RACE_LOCATION);   
	        this.queryRaceLocation = this.connection.prepareStatement(SQL_QUERY_RACE_BY_LOCATION);   
	        this.sumRaces = this.connection.prepareStatement(SUM_TOTAL_RACES_AT_LOCATION);  
	        this.queryRace = this.connection.prepareStatement(SQL_QUERY_RACE);  
	        this.cancelRace = this.connection.prepareStatement(SQL_CHANGE_STATUS_CANCELLED);
	        this.relocateRace = this.connection.prepareStatement(SQL_CHANGE_STATUS_RELOCATED);   
	        this.queryTotalRacesbySex = this.connection.prepareStatement(SQL_QUERY_RACE_BY_SEX);
	        this.dropContact = this.connection.prepareStatement(SQL_QUERY_DROP_CONTACT);
	        this.allContacts = this.connection.prepareStatement(SQL_QUERY_ALL_CONTACTS);
	        this.queryCancelledRaces = this.connection.prepareStatement(SQL_QUERY_CANCELLED_RACES);
	        this.queryRelocatedRaces = this.connection.prepareStatement(SQL_QUERY_RELOCATED_RACES);
	        this.queryHigestElevation = this.connection.prepareStatement(SQL_QUERY_HIGEST_ELEVATION);
	        
	    }

	    /**
	     * Runs the application.
	     */
	    public void runApp() throws SQLException {
	        
	    	// Step 8: This is where the main program runs
	    	System.out.println("This are all the Race Locations and their number of races:");
	    	System.out.println();
	    	allLocations();
	    	System.out.println();
	    	
	        Scanner in = new Scanner(System.in);
	        
	        System.out.println("Please enter Race Location:");
	        resort = in.nextLine();
	        
	        String RaceLocation = queryLocation(resort);
	        	if (RaceLocation == null) {
	        		System.out.println("No race location found. Would you like to add a new location? (yes/no)");
	        		String answer = in.nextLine();
	        		
	        		if(answer.equals("yes")) {
	        			System.out.println("Please enter the state of the location and elevation you want to add. (AT 11000)");
	        			String stateAndElevation = in.nextLine();
	        			int x = stateAndElevation.indexOf(' ');
			            String state = stateAndElevation.substring(0,x);
			            String elevation = stateAndElevation.substring(x+1);
			            addNewRaceLocation(this.resort, state, elevation);			
	        		}
	        		
	        		else {
	        			System.out.println("Have a great day! Bye.");
	        			System.exit(0);
	        		}
	        	}
	        	else {
	        		System.out.println("You are viewing races at race location: " + RaceLocation);
		        	System.out.println();
		        	
		        	int races = sumRaces(resort);
		        	
		        	System.out.println("There are " + races + " total races at this location.");
	        		System.out.println();
	        		
	        	}
	        	
	        	System.out.println("This are the scheduled races at this location: ");
        		queryRaceLocation(resort);
        		System.out.println();        	
	        	
	        while (true) {
    	
			        System.out.println("What would you like to do?");
			        System.out.println("S) Search for all races by sex");
			        System.out.println("C) Cancel Race");
			        System.out.println("R) Relocate Race");
			        System.out.println("D) Delete Contact ");
			        System.out.println("E) See higest elevation");
			        System.out.println("Q) Quite");
			        
			        String line = in.nextLine();
			   
			        // Search for a Race by sex
			        
			        if (line.equals("S") || line.equals("s")){
			              System.out.print("Enter F for female or M for male to view their scheduled races.");
			              String keyword = in.nextLine();
			              
			              System.out.println();
			              System.out.println("This are all the scheduled races: ");
			              queryRace(keyword);	
			              
			              System.out.println();
			              int sex = queryTotalRacesbySex(keyword);
			              System.out.println("There are " + sex + " races scheduled.");
			              System.out.println();
			        }
			       
			        
			        // Cancel Race
			        
			        else if (line.equals("C") || line.equals("c")){

			              System.out.print("Enter the date and sex of the race you want to cancel (2023-01-26 F): ");
			              
			              String dateAndSex = in.nextLine();
			              int x = dateAndSex.indexOf(' ');
			              String date = dateAndSex.substring(0,x);
			              String sex = dateAndSex.substring(x+1);
			              cancelRace(this.resort, date, sex); 
			              System.out.print("Those are all cancelled races: ");
			              System.out.println();
			              queryCancelledRaces();
			              
			        }
			              
			        // Relocate Race
			        
			        else if (line.equals("R") || line.equals("r")){

			              System.out.print("Enter the date and sex of the race you want to relocate (2023-01-26 F): ");
			              
			              String dateAndSex = in.nextLine();
			              int x = dateAndSex.indexOf(' ');
			              String date = dateAndSex.substring(0,x);
			              String sex = dateAndSex.substring(x+1);
			              relocateRace(this.resort, date, sex);  
			              System.out.print("Those are all relocated races: ");
			              queryRelocatedRaces();
			        }
			        
			        else if (line.equals("D") || line.equals("d")){

			              System.out.print("Enter the phone number of the contact you want to delete (801-893-8690): ");
			              System.out.println();
			              String phone = in.nextLine();
			              dropContact(phone); 
			              
			              System.out.print("Available contacts per location: ");
			              System.out.println();
			              allContacts();
			              System.out.println();
			        }
			        
			        
			        
			        
			        else if (line.equals("E") || line.equals("e")){
			        	
			        	System.out.println("This is the highest elevation:");
			        	queryHigestElevation();
			        	System.out.println();
			        }
			        
			   
			        // Exit 
			        
			        else if (line.equals("Q") || line.equals("q")) {
			        	System.out.println("Thanks for checking races today! Have a great day.");
			        	break;
			        }
			        else {
			        	break;
			        }
			   }
	}
	    
	    private void queryRelocatedRaces() throws SQLException{
	    	ResultSet results = queryRelocatedRaces.executeQuery();
	    	while (results.next()){ 
	    		 String date = results.getString("Race.date");
	    		 String sex = results.getString("Race.sex");
	    		 String status = results.getString("Race.status");
	    		 System.out.println(date+" "+sex+" "+status);
	    	 } 
	    }


		private void queryCancelledRaces() throws SQLException {
			ResultSet results = queryCancelledRaces.executeQuery();
	    	while (results.next()){ 
	    		 String date = results.getString("Race.date");
	    		 String sex = results.getString("Race.sex");
	    		 String status = results.getString("Race.status");  
	    		 System.out.println();
	    		 System.out.println(date+" "+sex+" "+status);
	    		 System.out.println();
	    	 } 
			
		}

		// Query all available contacts
	    private void allContacts() throws SQLException {
	    	ResultSet results = allContacts.executeQuery();
	    	 while (results.next()){ 
	    		 String resort = results.getString("Location.resort");
	    		 String phone = results.getString("Contact.phone");
	    		 String email = results.getString("Contact.email");
	    		 System.out.println();
	    		 System.out.println("resort: "+resort);
	    		 System.out.println("phone: "+phone);
	    		 System.out.println("email: "+email);
	    		 System.out.println();
	    	 } 
			
		}

		// Delete Contact
	    private void dropContact(String phone) throws SQLException {
			dropContact.setString(1, phone);
			dropContact.execute();
			
		}

		// all locations and their races
	    private void allLocations() throws SQLException {
	    	ResultSet results = allLocations.executeQuery();
	    	 while (results.next()){ 
			String result = results.getString("Location.resort");
			String number = results.getString("numberOfRaces");
        	System.out.println(result+ ": "+number);
	    	 } 
			
	    }

		// relocate Race
	    private void relocateRace(String resort, String date, String sex) throws SQLException {
	    	relocateRace.setString(1, resort);
	    	relocateRace.setString(2, date);
	    	relocateRace.setString(3, sex);
	    	relocateRace.execute();		
		}
	    
	    //cancel Race
		private void cancelRace(String resort, String date, String sex) throws SQLException {
			cancelRace.setString(1, resort);
			cancelRace.setString(2, date);
	    	cancelRace.setString(3, sex);
	    	cancelRace.execute();	
		}

	    private int queryTotalRacesbySex(String keyword) throws SQLException {
	    		queryTotalRacesbySex.setString(1,  "%" + keyword+ "%");
				ResultSet results_sex = queryTotalRacesbySex.executeQuery();

			    results_sex.next();
			    return results_sex.getInt("raceCountSex");
	        }
	    
		// query race by sex  
	    private void queryRace(String keyword) throws SQLException{
	    	queryRace.setString(1,  "%" + keyword+ "%");
	 		
			ResultSet results_keyword = queryRace.executeQuery();

	        // Iterate over each row of the results
	        while (results_keyword.next()) {
	        	String date = results_keyword.getString("Race.date");
	        	String sex = results_keyword.getString("Race.sex");
	        	String discipline = results_keyword.getString("Race.discipline");
	        	String status = results_keyword.getString("Race.status");
	        	System.out.println(date+ " "+ sex + " "+discipline + " "+ status);
	        }
		}
	      
	    
		// Sum total races
	    
	    private int sumRaces(String resort) throws SQLException{
	    	
	    	sumRaces.setString(1, resort);
			ResultSet results_race = sumRaces.executeQuery();

		    if(results_race.next() == true) {
		    	return results_race.getInt("id");
		    }
		    else {
		    	return 0;
		    }
			
		}
	    // Query all races at this location
	    
	    private void queryRaceLocation(String location) throws SQLException {
	    	queryRaceLocation.setString(1, location);
			ResultSet results_location = queryRaceLocation.executeQuery();
			
			if(results_location.next() == false) {
				System.out.println("There is no race at this location.");
			}
		        // Iterate over each row of the results
			else
		    do  { 
		    	String date = results_location.getString("Race.date");
	        	String sex = results_location.getString("Race.sex");
	        	String discipline = results_location.getString("Race.discipline");
	        	String status = results_location.getString("Race.status");
	        	System.out.println(date+ " "+ sex + " "+discipline + " "+ status);
		         
		        } while (results_location.next());
			
		}

		// add New Race Location
	    
	    private void queryHigestElevation() throws SQLException{ 
	    	
	    	
	    	ResultSet results = queryHigestElevation.executeQuery();
	    	
	    	while (results.next()) {

				String state  = results.getString("state");
				String resort  = results.getString("resort");
				String elevation  = results.getString("elevation");
				System.out.println( state + " "  +  resort + " "  + elevation);
			}
		}
	    
		// Query Race Location
	    
	    private String queryLocation(String location) throws SQLException{
	    	queryLocation.setString(1, location);
			ResultSet result = queryLocation.executeQuery();
		
			if(result.next() == false) {
				return null;
			}
			else {
				return result.getString("locationId");
			}
		}
	    
	 // Query highest elevation
	    private void addNewRaceLocation(String resort, String state, String elevation) throws SQLException{
	    	addNewRaceLocation.setString(1, resort);
	    	addNewRaceLocation.setString(2, state);
	    	addNewRaceLocation.setString(3, elevation);
	    	addNewRaceLocation.execute();
			
		}

		
	 // Step 9: This is where each method runs
	    
	    public void close() throws SQLException {
	        connection.close();
	    }

	    /**
	     * Entry point of the application. Uses command-line parameters to override database
	     * connection settings, then invokes runApp().
	     */
	    public static void main(String... args) {
	        // Default connection parameters (can be overridden on command line)
	        Map<String, String> params = new HashMap<>(Map.of(
	            "dbname", "" + DB_NAME,
	            "user", DB_USER,
	            "password", DB_PASSWORD
	        ));

	        boolean printHelp = false;

	        // Parse command-line arguments, overriding values in params
	        for (int i = 0; i < args.length && !printHelp; ++i) {
	            String arg = args[i];
	            boolean isLast = (i + 1 == args.length);

	            switch (arg) {
	            case "-h":
	            case "-help":
	                printHelp = true;
	                break;

	            case "-dbname":
	            case "-user":
	            case "-password":
	                if (isLast)
	                    printHelp = true;
	                else
	                    params.put(arg.substring(1), args[++i]);
	                break;

	            default:
	                System.err.println("Unrecognized option: " + arg);
	                printHelp = true;
	            }
	        }

	        // If help was requested, print it and exit
	        if (printHelp) {
	            printHelp();
	            return;
	        }

	        // Connect to the database. This use of "try" ensures that the database connection
	        // is closed, even if an exception occurs while running the app.
	        
	        try (DatabaseTunnel tunnel = new DatabaseTunnel();
	        		RaceSchedule app = new  RaceSchedule(
	                "localhost", tunnel.getForwardedPort(), params.get("dbname"),
	                params.get("user"), params.get("password")
	            )) {
	            // Run the interactive mode of the application.
	            app.runApp();
	            
	        } catch (IOException ex) {
	            System.err.println("Error setting up ssh tunnel.");
	            ex.printStackTrace();
	        } catch (SQLException ex) {
	            System.err.println("Error communicating with the database (see full message below).");
	            ex.printStackTrace();
	            System.err.println("\nParameters used to connect to the database:");
	            System.err.printf("\tSSH keyfile: %s\n\tDatabase name: %s\n\tUser: %s\n\tPassword: %s\n\n",
	                    params.get("sshkeyfile"), params.get("dbname"),
	                    params.get("user"), params.get("password")
	            );
	            System.err.println("(Is the MySQL connector .jar in the CLASSPATH?)");
	            System.err.println("(Are the username and password correct?)");
	        }
	        
	    }

	    private static void printHelp() {
	        System.out.println("Accepted command-line arguments:");
	        System.out.println();
	        System.out.println("\t-help, -h          display this help text");
	        System.out.println("\t-dbname <text>     override name of database to connect to");
	        System.out.printf( "\t                   (default: %s)\n", DB_NAME);
	        System.out.println("\t-user <text>       override database user");
	        System.out.printf( "\t                   (default: %s)\n", DB_USER);
	        System.out.println("\t-password <text>   override database password");
	        System.out.println();
	    }
	}

