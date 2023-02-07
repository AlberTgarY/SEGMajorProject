package test.java;

import main.java.com.projectBackEnd.Services.User.Hibernate.*;

import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import java.math.BigInteger;

import main.java.com.projectBackEnd.HibernateUtility;
import main.java.com.projectBackEnd.EntityManager;

/**
 * Test class to extensively unit test interactions between the user entity manager and the Users table in the database
 */
class UserManagerTest{
	private static ConnectionLeakUtil connectionLeakUtil = null;
	private static UserManagerInterface userManager = null;

	/**
	 * Prior to running, database information is set and a singleton manager is created for testing
	 */
	@BeforeAll
    static void setUpDatabase() {

		HibernateUtility.setResource("testhibernate.cfg.xml");
		userManager = UserManager.getUserManager();
		connectionLeakUtil = new ConnectionLeakUtil();

	}

	/**
	 * After the test, the factory is shut down and the LeakUtil can tell us whether any connections leaked
	 */
	@AfterAll
	static void assertNoLeaks() {
		HibernateUtility.shutdown();
		connectionLeakUtil.assertNoLeaks();
	}

	/**
	 * Prior to each test, we'll delete all the users in the users table
	 */
	@BeforeEach
	void setUp() {
		((EntityManager) userManager).deleteAll();
	}

	/**
	 * Test the User copy method
	 */
	@Test
	void testUserCopy() {
		User user1 = new User("test@test.com", "passowrd2, ", "name");
		User user2 = new User("test2@test2.com", "password", "sadnjkdsa");
		user1.copy(user2);
		assertEquals("test2@test2.com", user1.getEmail());
		assertEquals("password", user1.getPassword());
		assertEquals("sadnjkdsa", user1.getName());
	}
	/**
	 * Attempts to add a user with an email missing an '@' to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserInvalidEmail1()  {


		assertThrows(InvalidEmailException.class,() -> {userManager.addUser("email","password5","name");});
		
	}
	/**
	 * Attempts to add a user with an email including a space character to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserInvalidEmail2() {

		assertThrows(InvalidEmailException.class,() -> {userManager.addUser("ema il@email.com","password5","name");});
		
	}
	/**
	 * Attempts to add a user with an email missing it's extension to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserInvalidEmail3() {

		assertThrows(InvalidEmailException.class,() -> {userManager.addUser("email@email.","password5","name");});
		
	}

	/**
	 * Attempts to add a user with an email missing it's body to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserInvalidEmail4() {

		assertThrows(InvalidEmailException.class,() -> {userManager.addUser("@email.com","password5","name");});
		

	}

	/**
	 * Attempts to add a user with an email missing it's body and extension to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserInvalidEmail5() {
	

		assertThrows(InvalidEmailException.class,() -> {userManager.addUser("@email.","password5","name");});
		
	}

	/**
	 * Attempts to add a user with an empty name to the database, expects an exception to be thrown
	 */
	@Test
	void testAddUserIncorrectNameEmpty() {
		
		assertThrows(IncorrectNameException.class,() -> {userManager.addUser("email@email.com","password5","");});
		
	}

	/**
	 * Attempts to add a user with a null name to the database, expects an exception to be thrown

	 */
	@Test
	void testAddUserIncorrectNameNull() {


		assertThrows(IncorrectNameException.class,() -> {userManager.addUser("email@email.com","password5",null);});
		
	}

	/**
	 * Tests that the manager is able to add a number of valid users to the database, expects success
	 */
	@Test
	void testAddUser(){
		fillDatabase(getTestUsers());
		try{
			userManager.addUser("user8@email.com","password8","name");
		}
		catch(EmailExistsException |InvalidEmailException|IncorrectNameException| InvalidPasswordException e){
			fail();
		}
		List<User> users = (List<User>)((EntityManager) userManager).getAll();
		assertEquals(8,users.size());
		assertEquals(1,users.stream().filter(u->(u.getEmail().equals("user8@email.com") && u.getPassword().equals(hash("password8")))).count());
	}

	/**
	 * Attempts to add a user with credentials of a pre existing user to the database, expects an exception to be thrown
	 */
	@Test
	void testAddExistingUser() {
		fillDatabase(getTestUsers());
		assertThrows(EmailExistsException.class,() -> {userManager.addUser("user5@email.com","password5","name");});
		
	}

	/**
	 * Tests that the manager is able to verify a valid user, expects success
	 */
	@Test
	void testVerifyUser(){
		fillDatabase(getTestUsers());
		assertNull(userManager.verifyUser("not_in","not in"));
		assertNotNull(userManager.verifyUser("user6@email.com","password6"));
		assertNotNull(userManager.verifyUser("user6@email.com","password6"));
		assertNull(userManager.verifyUser("user6@email.com","password5"));
	}

	/**
	 * Tests that the manager is able to change the password of an existing user to a legal value, expects succest
	 * @throws UserNotExistException Thrown when a user doesn't exist
	 */
	@Test
	void testChangePassword() throws UserNotExistException{
		fillDatabase(getTestUsers());
		try{
			userManager.changePassword("user1@email.com","password10");
		}
		catch(InvalidPasswordException e){
			fail();
		}
		List<User> users = (List<User>)((EntityManager) userManager).getAll();
		assertEquals(1,users.stream().filter(u->(u.getEmail().equals("user1@email.com") && u.getPassword().equals(hash("password10")))).count());
		assertEquals(7,users.size());


	}

	/**
	 * Attempts to change the password of a non existing user, expects an exception to be thrown
	 */
	@Test
	void testChangePasswordUserNotExsist() {
		fillDatabase(getTestUsers());

		assertThrows(UserNotExistException.class,() -> {userManager.changePassword("user20@email.com","password10");});
	}

	/**
	 * Attempts to change the password of an existing user to an illegal value, expects an exception to be thrown
	 */
	@Test
	void testChangePasswordUserWrongPassword() {
		fillDatabase(getTestUsers());

		assertThrows(InvalidPasswordException.class,() -> {userManager.changePassword("user6@email.com","");});
		assertThrows(InvalidPasswordException.class,() -> {userManager.changePassword("user6@email.com",null);});
	}

	/**
	 * Tests that the manager is able to delete a number of existing users from the database, expects success
	 */
	@Test
	void testDeleteUser(){
		fillDatabase(getTestUsers());
		try{
			userManager.deleteUser("user1@email.com","password1");
			List<User> users = (List<User>)((EntityManager) userManager).getAll();
			assertEquals(0,users.stream().filter(u->(u.getEmail().equals("user1@email.com") && u.getPassword().equals(hash("password1")) == true)).count());
			assertEquals(6,users.size());
		}
		catch(UserNotExistException e){
			fail();
		}
	}

	/**
	 * Attempts to delete a user who does not exist in the database, expects to throw an exception

	 */
	@Test
	void testDeleteNotExistingUser() {
		assertThrows(UserNotExistException.class,() -> {userManager.deleteUser("user0@email.com","password0");});
		fillDatabase(getTestUsers());
		assertThrows(UserNotExistException.class,() -> {userManager.deleteUser("user0@email.com","password0");});
	}

	/**
	 * Attempts to delete a user with an incorrect password, expects to throw an exception
	 */
	@Test
	void testDeleteWrongPasswordUser()  {
		fillDatabase(getTestUsers());
		assertThrows(UserNotExistException.class,() -> {userManager.deleteUser("user1@email.com","password0");});
	}

	/**
	 * Attempts to change the email of a non existing user, expects to throw exceptions
	 */
	@Test
	void testChangeEmailBadEmail() {
		fillDatabase(getTestUsers());
		assertThrows(UserNotExistException.class,() -> {userManager.changeEmail("user@email.com","user19@email.com");});
	}

	/**
	 * Attempts to change the email of an existing user to the email of another existing user, expects to throw an exception
	 */
	@Test
	void testChangeEmailExisitngEmail() {
		fillDatabase(getTestUsers());
		assertThrows(EmailExistsException.class,() -> {userManager.changeEmail("user1@email.com","user5@email.com");});
	}

	/**
	 * Tests that the manager is able to change the email of an existing user to a different valid email,
	 * expects success
	 */
	@Test
	void testChangeEmail(){
		fillDatabase(getTestUsers());
		try{
		userManager.changeEmail("user1@email.com","user10@email.com");
		assertNotNull(userManager.verifyUser("user10@email.com","password1"));
		}
		catch(Exception e){
			fail();
		}	
	}

	/**
	 * Tests that the manager is able to verify an existing legal user email, expects success
	 */
	@Test	
	void testVerifyEmail(){
		fillDatabase(getTestUsers());
		
		assertTrue(userManager.verifyEmail("user1@email.com"));
		assertFalse(userManager.verifyEmail("user0@email.com"));
		
	}

	/**
	 * Tests that the manager is able to change the name of an existing user, expects success
	 * @throws IncorrectNameException This exception is not asserted but may be thrown
	 * @throws UserNotExistException This exception is not expected but may be thrown
	 */
	@Test
	void testChangeGetName() throws IncorrectNameException, UserNotExistException{
		fillDatabase(getTestUsers());
		assertEquals("name",userManager.getName("user1@email.com"));
		userManager.changeName("user1@email.com", "n a me");
		assertEquals("n a me",userManager.getName("user1@email.com"));

	}
	/**
	 * Tests that the manager is able to get the name of an existing user, expects success
	 * @throws UserNotExistException This exception is not expected but may be thrown
	 */
	@Test
	void testGetName() throws UserNotExistException{
		fillDatabase(getTestUsers());
		assertEquals("name",userManager.getName("user1@email.com"));
	}
	/**
	* test if get name of not existing user throws UserNotExistException
	*/
	@Test
	void testGetNameIncorrect() {
		assertThrows(UserNotExistException.class,() -> {userManager.getName("user1@email.com");});
		fillDatabase(getTestUsers());
		assertThrows(UserNotExistException.class,() -> {userManager.getName("user009@email.com");});
	}
	/**
	 * Attempts to change the name of a non existing user, expects an exception to be thrown
	 */
	@Test
	void testChangeGetNameIncorrectUser() {

		assertThrows(UserNotExistException.class,() -> {userManager.changeName("user1@email.com", "new name");});
	}

	/**
	 * Attempts to change the name of an existing user to an empty value, expects to throw an exception
	 * @throws UserNotExistException This exception is not expected but may be thrown
	 */
	@Test
	void testIncorrectNameEmpty() throws UserNotExistException{
		fillDatabase(getTestUsers());
		assertEquals("name",userManager.getName("user1@email.com"));
		assertThrows(IncorrectNameException.class,() -> {userManager.changeName("user1@email.com", "");});
	}

	/**
	 * Attempts to change the name of an existing user to a null value, expects to throw an exception
	 * @throws UserNotExistException This exception is not expected but may be thrown
	 */
	@Test
	void testIncorrectNameNull() throws UserNotExistException{
		fillDatabase(getTestUsers());
		assertEquals("name",userManager.getName("user1@email.com"));
		assertThrows(IncorrectNameException.class,() -> {userManager.changeName("user1@email.com", null);});

	}

	/**
	 * Method used to hash passwords, using SHA-512
	 * @param in The password to hash
	 * @return The hashed passwords
	 */
	private String hash(String in){
		String salt = "fX66CeuGKjmdkguhPEzp";
		int split = in.length()/3;
		String withSalt = in.substring(0,split) + salt + in.substring(split,in.length());
		try{
			MessageDigest alg = MessageDigest.getInstance("SHA-512"); 
			alg.reset();
			alg.update(withSalt.getBytes(StandardCharsets.UTF_8));
			return String.format("%0128x", new BigInteger(1, alg.digest()));
		}
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 

		}

	}

	/**
	 * Quality of life method for retrieving a list of existing users
	 * @return The list of users
	 */
	private ArrayList<User> getTestUsers(){
		ArrayList<User> users = new ArrayList<>();
		users.add(new User("user1@email.com",hash("password1"),"name"));
		users.add(new User("user2@email.com",hash("password2"),"name"));
		users.add(new User("user3@email.com",hash("password3"),"name"));
		users.add(new User("user4@email.com",hash("password4"),"name"));
		users.add(new User("user5@email.com",hash("password5"),"name"));
		users.add(new User("user6@email.com",hash("password6"),"name"));
		users.add(new User("user7@email.com",hash("password7"),"name"));

		return users;
	}

	/**
	 * Fills the database database with a list of users
	 * @param listOfUsers The list to be filled in
	 */
	private void fillDatabase(ArrayList<User> listOfUsers){
		for(User u : listOfUsers){
			((EntityManager) userManager).insertTuple(u);
		}
	}
}
