package controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import data.UserData;
import data.UserList;
import gui.ClientView;

/**
 * Main Controller of the project, housing the userList and clientView for the project, and passing information between the two.
 * @author Nolan Wright
 */
public class ClientController {
	
	/** The UserList for the program, holds all UserData data for the program */
	private UserList userList;
	
	/** The ClientView for the program, houses all the GUI data for the program */
	private ClientView clientView;
	
	/** The fileLocation for the program, used for loading and saving data */
	private String fileLocation;
	
	/**
	 * Standard Constructor for the Client Controller, starting the userList and ClientView
	 */
	public ClientController() {
		userList = new UserList(null);
		clientView = new ClientView(this);
		fileLocation = "";
	}

	/**
	 * Initializes the Program, via collecting the file location from ClientView, loading the data, and then starting the ClientView.
	 * Along with this it starts all the internal schedulers for autosave, time tracking, and starts the display for logged in users.
	 */
	public void initialize() {
		fileLocation = clientView.getUserFile();
		if(fileLocation == null) {
			fileLocation = "src/main/resources/userDataFile.txt";
		}
		userList.loadData(fileLocation);
		clientView.intializeWindow();
		
		this.initalizeAutoSave();
		this.initalizeTimeTracking();
		this.initalizeDisplayLoggedInUsers();
	}

	/**
	 * Starts the scheduler for displaying the logged in users, running it every minute.
	 */
	private void initalizeDisplayLoggedInUsers() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> displayLoggedInUsers();

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
		
	}
	
	
	/**
	 * Used to tell the ClientView to display the current loggedIn Users
	 */
	private void displayLoggedinUsers() {
		clientView.displayLoggedInUsers(userList.getLoggedInUsers());
	}

	/**
	 * Calls the ClientView to display the given loggedInUsers from the User List
	 */
	private void displayLoggedInUsers() {
		clientView.displayLoggedInUsers(userList.getLoggedInUsers());
	}

	/**
	 * Starts the scheduler for decreasing the time for logged in users, running it every second.
	 */
	private void initalizeTimeTracking() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> decreaseTimeForLoggedInUsers();

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
		
	}

	/**
	 * Tells the UserList to decrease all the LoggedIn Users by 1 second
	 */
	private void decreaseTimeForLoggedInUsers() {
		userList.decreaseTimeForLoggedInUsers();
	}
	
	/**
	 * Starts the schedule for autoSaving the Users, running it every 2 minutes.
	 */
	private void initalizeAutoSave() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> saveData(0);

        scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.MINUTES);
		
	}

	/**
	 * This method is called by the ClientView to add users to the UserList given a UserData Object
	 * @param ud Given User Data to be added to the list.
	 */
	public void addUser(UserData ud) {
		userList.addUser(ud);
	}

	/**
	 * Saves the UserList to a file (same file that was given at startUp)
	 * @param finalSave If a 1 is given to the saveData program, UserList will be instructed to logout all users before saving.
	 */
	public void saveData(int finalSave) {
		System.out.println("Saving");
		if(finalSave == 1) {
			userList.logoutAllUsers();
		}
		userList.saveData(fileLocation);
	}

	/**
	 * Used to grab a User by it's designated ID from the UserList
	 * @param id The Given User's ID
	 * @return Returns the user given an ID, or Null if the user cannot be found
	 */
	public UserData getUserById(String id) {
		return userList.getUserById(Long.parseLong(id));
	}

	/**
	 * Used to toggle the User's LoggedIn Status
	 * @param ud UserData that needs it's logged in status toggled
	 */
	public void toggleUser(UserData ud) {
		userList.toggleUser(ud);
		this.displayLoggedinUsers();
	}
	
	/**
	 * Used to Search through the UserList via a LevenshteinDistance Equation
	 * @param userName The given User Name (if available)
	 * @param userEmail The given User Email (if available)
	 * @param userPhoneNumber The given User Phone Number (if available)
	 * @return Returns a list of up to 5 users with the closest distance from the equation
	 */
	public UserData[] searchForUsers(String userName, String userEmail, String userPhoneNumber) {
		return userList.searchThroughUsers(userName, userEmail, userPhoneNumber);
	}
}
