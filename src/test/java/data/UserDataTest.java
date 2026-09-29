/**
 * 
 */
package data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Testing class for the UserData Container Class
 */
class UserDataTest {

	
	@Test
	void UserDataSettersTest() {
		UserData u = new UserData("Nolan", "Nolan@Nolan.Nolan", "3360123456");
		
		assertEquals("Nolan", u.getName());
		assertEquals("Nolan@Nolan.Nolan", u.getEmail());
		assertEquals("3360123456", u.getPhoneNumber());
		assertEquals("0", u.currentHours());
		
		u.setName("Bob");
		u.setEmail("Bob@Bob.Bob");
		u.setPhoneNumber("2520123456");
		
		assertEquals("Bob", u.getName());
		assertEquals("Bob@Bob.Bob", u.getEmail());
		assertEquals("2520123456", u.getPhoneNumber());
		assertEquals("0", u.currentHours());
	}
	
	@Test
	void UserDataErrorCheckingTest() {
		UserData u;
		try {
			u = new UserData("Nolan", "Nolan.Nolan.Nolan", "3360123456");
			fail();
		} catch(IllegalArgumentException e) {
			//Sending an Error is wanted here
		}
		
		try {
			u = new UserData("Nolan", "Nolan@Nolan.Nolan", "3360123456234");
			fail();
		} catch(IllegalArgumentException e) {
			//Sending an Error is wanted here
		}
		
		try {
			u = new UserData("Nolan", "Nolan@Nolan.Nolan", "53360123456");
			fail();
		} catch(IllegalArgumentException e) {
			//Sending an Error is wanted here
		}
		
		try {
			u = new UserData("Nolan", "Nolan@Nolan.Nolan", "13360123456");
			//Sending an Error is not wanted here (we are assuming that if they do 11 numbers it's an extra one at the front we will cut off
			assertEquals("3360123456", u.getPhoneNumber());
		} catch(IllegalArgumentException e) {
			fail();
		}
	}
	
	@Test
	void UserDataAddHours() {
		UserData u = new UserData("Nolan", "Nolan@Nolan.Nolan", "3360123456");
		
		u.addHours(2);
		
		assertEquals("2", u.currentHours());
		
		u.addHours(3);
		
		assertEquals("5", u.currentHours());
	}
	
	@Test
	void UserDataLoseTime() {
		UserData u = new UserData("Nolan", "Nolan@Nolan.Nolan", "3360123456");
		
		u.addHours(2);
		
		assertEquals("2", u.currentHours());
		assertEquals("120", u.currentMinutes());
		
		u.subtractHours(1);
		
		assertEquals("1", u.currentHours());
		assertEquals("60", u.currentMinutes());
		
		u.subtractMinutes(30);
		
		assertEquals("0", u.currentHours());
		assertEquals("30", u.currentMinutes());;
	}
	
	@Test
	void UserDataCurrentTimeRemaining() {
		UserData u = new UserData("Nolan", "Nolan@Nolan.Nolan", "3360123456");
		
		u.addHours(2);
		
		assertEquals("2", u.currentHours());
		assertEquals("120", u.currentMinutes());
		
		assertEquals("2 Hours and 0 Minutes", u.currentTimeRemaining());
		
		u.subtractMinutes(20);
		
		assertEquals("1 Hours and 40 Minutes", u.currentTimeRemaining());
		
		u.subtractMinutes(45);
		
		assertEquals("55 Minutes", u.currentTimeRemaining());
		
		u.subtractMinutes(55);
		
		assertEquals("0 Minutes", u.currentTimeRemaining());
		
		u.subtractMinutes(10);
		
		assertEquals("0 Minutes", u.currentTimeRemaining());
		
		u.subtractHours(1);
		
		assertEquals("0 Minutes", u.currentTimeRemaining());
	}

}
