package data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserListTest {

	@Test
	void UserListIntializationTest() {
		UserList ul = null;
		try {
			ul = new UserList("src/test/resources/SmallUserListTestFile.txt");
		} catch(IllegalArgumentException e) {
			fail();
		}
		
		String output = ul.toString();
		
		assertEquals("Nolan Wrong, Nolan@Nolan.Nolan, 3361234567, 1 Hours and 20 Minutes", output);
	}

}
