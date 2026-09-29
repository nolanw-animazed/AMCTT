package data;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.text.similarity.LevenshteinDistance;

import io.UserListIO;

/**
 * UserList is used to contain all the UserData for the Project, along with holding the LoggedInUser List
 * @author Nolan Wright
 */
public class UserList {
	
	/** The main list for the User Data, contains all loaded data */
	private ArrayList<UserData> userDataList;
	
	/** The list of Logged In Users, specifically ordered by their remaining time*/
	private ArrayList<UserData> loggedInUserList;
	
	/**
	 * Standard Constructor for the UserList
	 * @param dataFile DataFile for the UserData (if Null, the listen will just start empty)
	 */
	public UserList(String dataFile) {
		if(dataFile != null) {
			userDataList = UserListIO.extractUserData(dataFile);
		} else {
			userDataList = new ArrayList<UserData>();
		}
		loggedInUserList = new ArrayList<UserData>();
	}
	
	/**
	 * Used to load data into the UserList (Typically used instead of passing the file through the constructor)
	 * @param file DataFile for the UserData (will overwrite anything currently in the list)
	 */
	public void loadData(String file) {
		userDataList = UserListIO.extractUserData(file);
	}

	/**
	 * Used to add new users to the list (usually created within ClientView)
	 * Also checks for ID Overlap during addition.
	 * @param ud New User Data to be added to the list
	 */
	public void addUser(UserData ud) {
		checkForIdOverlap(ud);
		userDataList.add(ud);
		
	}

	/**
	 * Used to check if there is an ID Overlap in the list given a userData, if there is the UserData is forced to create a new ID
	 * @param ud Userdata for Checking
	 */
	private void checkForIdOverlap(UserData ud) {
		for(int i = 0; i < userDataList.size(); i++) {
			if(ud.getId() == userDataList.get(i).getId()) {
				System.out.println("Overlap");
				ud.generateId(1);
				i = -1;
			}
		}
	}

	/**
	 * Returns the output String for the entire list.
	 * @return Returns all User Data in a String to save it to a file
	 */
	public String getOutputString() {
		String output = "";
		
		for(int i = 0; i < userDataList.size(); i++) {
			output = output + userDataList.get(i).toString() + "\n";
		}
		
		return output;
	}

	/**
	 * Used to get a User from the list given their ID
	 * @param id The given ID of the User
	 * @return Returns a user given their id if they are found, else null
	 */
	public UserData getUserById(long id) {
		for(int i = 0; i < userDataList.size(); i++) {
			UserData user = userDataList.get(i);
			if(id == user.getId()) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Used to toggle a users loggedIn Status, and update the appropriate lists.
	 * @param ud The UserData that needs to be toggled.
	 */
	public void toggleUser(UserData ud) {
		if(!ud.isLoggedIn()) {
			ud.loginUser();
			loggedInUserList.add(ud);
			Collections.sort(loggedInUserList);
		} else {
			ud.logoutUser();
			loggedInUserList.remove(ud);
			Collections.sort(loggedInUserList);
		}
		
	}

	/**
	 * Used to decrease the time for all logged in users by 1 second
	 */
	public void decreaseTimeForLoggedInUsers() {
		if(loggedInUserList.size() > 0) {
			for(int i = 0; i < loggedInUserList.size(); i++) {
				loggedInUserList.get(i).subtractSeconds(1);
			}
		}
	}

	/**
	 * Returns a list of all logged in users
	 * @return List of all logged in users
	 */
	public ArrayList<UserData> getLoggedInUsers() {
		return loggedInUserList;
	}

	/**
	 * Used to logout all users within the LoggedIn List
	 */
	public void logoutAllUsers() {
		for(int i = 0; i < loggedInUserList.size(); i++) {
			loggedInUserList.get(i).logoutUser();
		}
	}

	/**
	 * Search Method using the LevenshteinDistance that compares User Data
	 * @param userName Name of the User for comparison
	 * @param userEmail Email of the User for comparison
	 * @param userPhoneNumber Phone Number of the User for comparison
	 * @return Returns up to 5 users with similar names, emails, and/or phone numbers
	 */
	public UserData[] searchThroughUsers(String userName, String userEmail, String userPhoneNumber) {
		LevenshteinDistance ld = LevenshteinDistance.getDefaultInstance();
		
		boolean nameEmpty = true;
		boolean emailEmpty = true;
		boolean phoneEmpty = true;
		
		if(!userName.equals("")) {
			nameEmpty = false;
		}
		if(!userEmail.equals("")) {
			emailEmpty = false;
		}
		if(!userPhoneNumber.equals("")) {
			phoneEmpty = false;
		}
		
		UserData[] outputUser;
		int[] distanceList;
		
		if(userDataList.size() < 5) {
			outputUser = new UserData[userDataList.size()];
			distanceList = new int[userDataList.size()];
		} else {
			outputUser = new UserData[5];
			distanceList = new int[5];
		}
		
		for(int i = 0; i < distanceList.length; i++) {
			distanceList[i] = -1;
		}
		
		for(int i = 0; i < userDataList.size(); i++) {
			int distance = 0;
			UserData ud = userDataList.get(i);
			if(!nameEmpty) {
				distance = distance + ld.apply(userName, ud.getName());
			}
			if(!emailEmpty) {
				distance = distance + ld.apply(userEmail, ud.getEmail());
			}
			if(!phoneEmpty) {
				distance = distance + ld.apply(userPhoneNumber, ud.getPhoneNumber());
			}
			
			for(int j = 0; j < outputUser.length; j++) {
				if(distanceList[j] == -1) {
					outputUser[j] = userDataList.get(i);
					distanceList[j] = distance;
					break;
				}
				if(distanceList[j] > distance) {
					for(int x = outputUser.length - 1; x > j && x > 0; x--) {
						outputUser[x] = outputUser[x-1];
						distanceList[x] = distanceList[x-1];
					}
					outputUser[j] = userDataList.get(i);
					distanceList[j] = distance;
					break;
				}
			}
		}
		
		return outputUser;
	}
	
	/**
	 * Used to save the data from the list to the given file location (Calls IO.SaveUserData)
	 * @param fileLocation Given File Location
	 */
	public void saveData(String fileLocation) {
		UserListIO.saveUserData(fileLocation, this);
	}
	
	@Override
	public String toString() {
		String output = "";
		
		for(int i = 0; i < userDataList.size(); i++) {
			if(i != 0) {
				output = output + "\n";
			}
			output = output + userDataList.get(i);
		}
		
		return output;
	}
	
}
