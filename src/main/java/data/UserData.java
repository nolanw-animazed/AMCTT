package data;

import java.time.Duration;
import java.util.Objects;
import java.util.Random;

/**
 * This is a container class for the User Data. It will be used to store their name and email/phone number, along with their time.
 * @author Nolan Wright
 */
public class UserData implements Comparable<UserData>{

	/** The Name of the User [Preferably first and last, but it's whatever]*/
	private String name;
	
	/** The Email of the User [Required if they do not have a phone number]*/
	private String email;
	
	/** The Phone Number of the User [Required if they do not have an email]*/
	private String phoneNumber;
	
	/** The Time the User has remaining in the Manga Cafe, stored as a duration*/
	private Duration mangaCafeTime;
	
	/** The internal id of the user. It can be randomized, however this is only done if there is an overlap in ids in UserList. */
	private long id;
	
	/** A boolean to keep track of if the user is logged in. The UserList will update this variable as needed.*/
	private boolean loggedIn;
	
	/**
	 * General Setter for the UserData Creation (This will auto generate an ID)
	 * @param name Name of the User
	 * @param email Email of the User
	 * @param phoneNumber PhoneNumber of the User
	 */
	public UserData(String name, String email, String phoneNumber) {
		setName(name);
		boolean enoughData = false;
		if(email != null && !email.equals("")) {
			setEmail(email);
			enoughData = true;
		}
		if(phoneNumber != null && !phoneNumber.equals("")) {
			setPhoneNumber(phoneNumber);
			enoughData = true;
		}
		if(!enoughData) {
			throw new IllegalArgumentException("Please enter a valid email or phone number");
		}
		generateId(0);
		mangaCafeTime = Duration.ZERO;
		loggedIn = false;
	}
	
	/**
	 * Constructor used when importing Data from a file, as the UserData will already have a given ID.
	 * @param name Name of the User
	 * @param email Email of the User [must have an @]
	 * @param phoneNumber Phone Number of the User [Numbers only]
	 * @param id Id of the User [Randomly Generated]
	 */
	public UserData(String name, String email, String phoneNumber, long id) {
		setName(name);
		boolean enoughData = false;
		if(email != null && !email.equals("") && !email.equals("null")) {
			setEmail(email);
			enoughData = true;
		}
		if(phoneNumber != null && !phoneNumber.equals("") && !phoneNumber.equals("null")) {
			setPhoneNumber(phoneNumber);
			enoughData = true;
		}
		if(!enoughData) {
			throw new IllegalArgumentException("Please enter a valid email or phone number");
		}
		
		this.id = id;
		mangaCafeTime = Duration.ZERO;
	}

	/**
	 * Generate a new ID via Random
	 * @param state
	 */
	public void generateId(int state) {
		Random r = new Random();
		if(state == 0) {
			r.setSeed(hashCode());
		}
		
		
		id = r.nextLong(100000, 999999999);
		
	}

	/**
	 * Getter for Name
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for Name
	 * @param name the name to set
	 */
	public void setName(String name) {
		if(name == null || name.equals("")) {
			throw new IllegalArgumentException("Please Enter a name");
		}
		this.name = name;
	}



	/**
	 * Getter for Email
	 * @return the email
	 */
	public String getEmail() {
		if(email == null) {
			return "";
		}
		return email;
	}


	/**
	 * Setter for Email
	 * @param email the email to set
	 * @exception Thrown if the Email does not contain an @ symbol
	 */
	public void setEmail(String email) {
		if(!email.contains("@")) {
			throw new IllegalArgumentException("Please enter a valid Email Address (contains @)");
		}
		this.email = email.strip();
	}

	/**
	 * Getter for the Phone Number
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		if(phoneNumber == null) {
			return "";
		}
		return phoneNumber;
	}
	
	/**
	 * Setter for the Phone Number
	 * @param phoneNumber the phoneNumber to set
	 * @exception Thrown if the phone number is too long, too short, or isn't numbers
	 */
	public void setPhoneNumber(String phoneNumber) {
		if(phoneNumber == null || phoneNumber.equals("")) {
			throw new IllegalArgumentException("Please enter a valid phone number (please use 10 digits)");
		}
		phoneNumber = phoneNumber.replaceAll("\\D", "");
		if(phoneNumber.length() < 10) {
			throw new IllegalArgumentException("Please enter a valid phone number (please use 10 digits)");
		}
		if(phoneNumber.length() != 10) {
			if(phoneNumber.charAt(0) != '1' || phoneNumber.length() > 11) {
				throw new IllegalArgumentException("Please enter a valid phone number (please use 10 digits)");
			}
			phoneNumber = phoneNumber.substring(1);
		}
		this.phoneNumber = phoneNumber.strip();
	}
	
	/**
	 * Returns the ID of the User
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Used to add hours to the User
	 * @param hoursToAdd Whole Number value of hours to add to the user
	 */
	public void addHours(long hoursToAdd) {
		mangaCafeTime = mangaCafeTime.plusHours(hoursToAdd);
	}
	
	/**
	 * Used to add minutes to the user
	 * @param minutesToAdd Whole number value of minutes to add to the user
	 */
	public void addMinutes(long minutesToAdd) {
		mangaCafeTime = mangaCafeTime.plusMinutes(minutesToAdd);
	}
	
	/**
	 * Used to Subtract Hours from the User
	 * @param hoursToSubtract Whole Number value of hours to subtract from the user
	 */
	public void subtractHours(long hoursToSubtract) {
		mangaCafeTime = mangaCafeTime.minusHours(hoursToSubtract);
		
		if(mangaCafeTime.isNegative()) {
			mangaCafeTime = Duration.ZERO;
		}
	}
	
	/**
	 * Used to subtract minutes from the User
	 * @param minutesToSubtract Whole Number Value of minutes to subtract from the User
	 */
	public void subtractMinutes(long minutesToSubtract) {
		mangaCafeTime = mangaCafeTime.minusMinutes(minutesToSubtract);
		
		if(mangaCafeTime.isNegative()) {
			mangaCafeTime = Duration.ZERO;
		}
	}
	
	/**
	 * Output of the Current Hours for the User
	 * @return Whole Number Output of the Hours Rounded Down [50 minutes would round to 0 hours]
	 */
	public String currentHours() {
		return Long.toString(mangaCafeTime.toHours());
	}
	
	/**
	 * Output of the Current Minutes for the User
	 * @return Whole Number Output of the Minutes Rounded Down [50.9 minutes would round to 50 minutes]
	 */
	public String currentMinutes() {
		return Long.toString(mangaCafeTime.toMinutes());
	}

	/**
	 * Output the Hours and Minutes in a String such as (1 Hour and 30 Minutes)
	 * @return A String with the current time the client has left.
	 */
	public String currentTimeRemaining() {
		String output = "";
		
		long hours = mangaCafeTime.toHours();
		long minutes = mangaCafeTime.toMinutes();
		
		if(hours == 0 && minutes == 0) {
			output = "0 Minutes";
			return output;
		}
		if(hours == 0) {
			output = minutes + " Minutes";
			return output;
		}
		
		output = hours + " Hours and " + (minutes - (hours * 60)) + " Minutes";
		return output;
	}
	
	/**
	 * Used to Initialize the time for the User from a given String
	 * @param string Formatted
	 */
	public void initilizeTime(String string) {
		String[] split = string.split(" ");
		if(split.length == 5) {
			addHours(Long.parseLong(split[0]));
			addMinutes(Long.parseLong(split[3]));
		} else {
			addMinutes(Long.parseLong(split[0]));
		}
		
	}
	
	/**
	 * Used to Login the User
	 * @exception Throws an exception if the User currently doesn't have any time
	 */
	public void loginUser() {
		if(mangaCafeTime.toMinutes() == 0) {
			throw new IllegalArgumentException("User does not currently have any time.");
		}
		loggedIn = true;
	}
	
	/**
	 * Used to logout the User 
	 */
	public void logoutUser() {
		loggedIn = false;
		//Adds a minute to the user unless they are out 0 due to rounding on filesave 
		//(Example: if a user is at 59 seconds, it will save as 0 minutes due to rounding, so this prevents that)
		if(mangaCafeTime != Duration.ZERO) {
			addMinutes(1);
		}
	}
	
	/**
	 * Subtracts time from the user, given a specific duration (unit of measurement)
	 * @param timeToSubtract The Duration that needs to be subtracted from the user.
	 */
	public void subtractTimeFromUser(Duration timeToSubtract) {
		if(loggedIn) {
			mangaCafeTime = mangaCafeTime.minus(timeToSubtract);
		}
		if(mangaCafeTime.isNegative()) {
			mangaCafeTime = Duration.ZERO;
		}
	}
	
	/**
	 * Returns the User Name and Time Remaining (Used for Displaying LoggedIn Users)
	 * @return String written as UserName - CurrentTime Remaaining()
	 */
	public String nameAndTimeRemaining() {
		return name + " - " + currentTimeRemaining();
	}
	
	/**
	 * Returns a String for the Search Algorithm
	 * @return A String formatted as Name,Email,PhoneNumber
	 */
	public String stringForSearching() {
		String name = this.name;
		String email = this.email;
		String phoneNumber = this.phoneNumber;
		
		if(email == null) {
			email = "";
		}
		
		if(phoneNumber == null) {
			phoneNumber = "";
		}
		
		return name.toLowerCase() + "," + email.toLowerCase() + "," + phoneNumber;
	}

	/**
	 * Used to subtract seconds from the amount of time the user has.
	 * @param secondsToSubtract Amount of seconds as a whole number to subtract from the user time.
	 */
	public void subtractSeconds(int secondsToSubtract) {
		mangaCafeTime = mangaCafeTime.minusSeconds(secondsToSubtract);
		
		if(mangaCafeTime.isNegative()) {
			mangaCafeTime = Duration.ZERO;
		}
		
	}

	/**
	 * Method to check if the user is logged into the system
	 * @return Returns true if the user is loggedIn, and false if the user isn't logged in.
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	@Override
	public String toString() {
		return name + "," + email + "," + phoneNumber + "," + currentTimeRemaining() + "," + id;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(email, name, phoneNumber);
	}

	/**
	 * Only Cares about if the different users have the same email, name, and phoneNumber
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserData other = (UserData) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber);
	}

	/**
	 * This CompareTo Method is specifically used to compare the time between users for sorting in the list
	 */
	@Override
	public int compareTo(UserData other) {
		long thisHours = Long.parseLong(this.currentHours());
		long otherHours = Long.parseLong(other.currentHours());
		if(thisHours < otherHours) {
			return -1;
		}
		if(thisHours > otherHours) {
			return 1;
		}
		
		long thisMinutes = Long.parseLong(this.currentMinutes());
		long otherMinutes = Long.parseLong(other.currentMinutes());
		
		if(thisMinutes < otherMinutes) {
			return -1;
		}
		if(thisMinutes > otherMinutes) {
			return 1;
		}
		
		return 0;
	}
}
