package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import data.UserData;
import data.UserList;

/**
 * The UserListIO Class is used to both extract and save UserData from the project, being called by UserList.
 * 
 * @author Nolan Wright
 */
public class UserListIO {

	/**
	 * ExtractUserData is used to pull all UserData from a file and importing it into the UserList class.
	 * @param dataFile A String of the Absolute Path for a Datafile. If the param is null, the method will just return an empty list.
	 * @exception Returns an exception if the file selected does not exist. This is different from the file being null.
	 * @return Returns an ArrayList of UserData (the list is always empty if the file is null)
	 */
	public static ArrayList<UserData> extractUserData(String dataFile) {
		//If the file is null (as in none is selected), an empty list is returned.
		if(dataFile == null) {
			ArrayList<UserData> outputList = new ArrayList<UserData>();
			return outputList;
		}
		
		File file = new File(dataFile);
		
		//If the file doesn't exist, an IllegalArgumentException is thrown so the user can reselect the file.
		if(!file.exists()) {
			throw new IllegalArgumentException("This file does not exist.");
		}
		
		Scanner input = null;
		
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			//If somehow the File is not found following the previous exception, the system exits. This should not be possible, but this is a backup just in case.
			e.printStackTrace();
			System.exit(0);
		}
		
		ArrayList<UserData> outputList = new ArrayList<UserData>();
		
		while(input.hasNext()) {
			String chunk = input.nextLine();
			
			//This may cause issues if there is a comma in the data, and there is a better way to do this via CSV importers.
			//However, if the data is cleaned of commas, it should be fine.
			String[] split = chunk.split(",");
			
			//If the data is malformed, just skip it.
			if(split.length != 5) {
				continue;
			}
			
			UserData u = null;
			
			//Attempting to create the UserData, if the data isn't good, it is tossed, and the while loop is continued.
			try {
				u = new UserData(split[0], split[1], split[2], Long.parseLong(split[4]));
			} catch(IllegalArgumentException e) {
				continue;
			}
			
			//The time from the data is input if the UserData isn't malformed.
			u.initilizeTime(split[3].strip());
			outputList.add(u);
		}
		
		return outputList;
	}
	
	/**
	 * SaveUserData saves the User Data to the same file that was given during startup. This method is used when the program exits, and during autosaves.
	 * @param dataFile A String of the Absolute Path for a Datafile. It will output to the file even if it doesn't exist.
	 * @param ul Given UserList for output. Likely could be swapped to the output String. [Change is negligible]
	 */
	public static void saveUserData(String dataFile, UserList ul) {
		String userDataOutput = ul.getOutputString();
		
		PrintWriter output = null;
		
		//Attempt to write the data, if it fails, the program exits. Given the autosaves every 2ish minutes, if the program crashes, a lot of data won't be lost.
		try {
			output = new PrintWriter(dataFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		output.print(userDataOutput);
		
		output.close();
		
	}

}
