package main;

import controller.ClientController;

/**
 * StarterFile is the class that enables the ClientController to start program. This likely could be combined into ClientController with creating a main in there,
 * however it is going to remain as is to allow the ClientController to focus on controlling.
 * 
 * @author Nolan Wright
 */
public class StarterFile {
	/**
	 * The main method for the project, this starts up the Client Controller, which then controls the project.
	 * @param args Unused
	 */
	public static void main(String[] args) {
		ClientController c = new ClientController();
		//The Client Controller is initialized, so the GUI can start up
		c.initialize();
	}
}
