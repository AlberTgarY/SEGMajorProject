package test.java;

import main.java.com.projectBackEnd.Services.User.Hibernate.*;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.ResetLinks.*;
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

class PasswordResetTest{
	private static ConnectionLeakUtil connectionLeakUtil = null;
	private static UserManagerInterface userManager = null;
	private static PasswordResetInterface resetManager = null;
	private static ResetLinkManagerInterface linkManager = null;
	/**
	 * Prior to running, database information is set and a singleton manager is created for testing
	 */
	@BeforeAll
    static void setUpDatabase() {

		HibernateUtility.setResource("testhibernate.cfg.xml");
		userManager = UserManager.getUserManager();
		resetManager = PasswordReset.getPasswordResetManager();
		linkManager = ResetLinkManager.getResetLinkManager();
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
		try{
			userManager.addUser("user@gmail.com", "password","name");
		}
		catch(Exception e){
			fail();
		}
	}
	/**
	* check if send password reset link does not throw errors
	*/
	@Test
    	@Timeout(15)
	void testSendPasswordResetLink(){
		try{
			resetManager.sendPasswordResetLink("user@gmail.com");
		}
		catch(EmailNotExistException|ServerErrorException e){
			fail();
			
		}
	}
	/**
	* check that sending a token for a incorrect email throws EmailNotExistException
	*/
	@Test
	void testSendPasswordResetLinkEmailNotExist(){
		assertThrows(EmailNotExistException.class,() -> {resetManager.sendPasswordResetLink("user1@user.com");});
	}
	/**
	* test that change password with correct input changes the password
	*/
	@Test
	void testChangePassword(){

		try{
			String t = linkManager.create("user@gmail.com");
			resetManager.changePassword(t, "newpass");
			String token = userManager.verifyUser("user@gmail.com","newpass");
			assertNotNull(token);
			SessionManager.getSessionManager().terminateSession(token);
		}
		catch(TokenNotExistException|UserNotExistException|InvalidPasswordException|EmailNotExistException e){
			fail();
		}


	}
	/**
	* test that change password with incorrect token throws TokenNotExistException
	*/
	@Test
	void testChangePasswordIncorrectToken(){

		assertThrows(TokenNotExistException.class,() -> {resetManager.changePassword("1", "newpass");});

		try{
			String t= linkManager.create("user@gmail.com");
			assertThrows(TokenNotExistException.class,() -> {resetManager.changePassword(t+1, "newpass");});

			assertThrows(TokenNotExistException.class,() -> {resetManager.changePassword("", "newpass");});
			assertThrows(TokenNotExistException.class,() -> {resetManager.changePassword(null, "newpass");});
		}
		catch(EmailNotExistException e){
			fail();
		}		


	}
	/**
	* test if change password with token that is not assigned to any account throws UserNotExistException
	*/
	@Test
	void testChangePasswordIncorrectUser(){

		try{
			String t = linkManager.create("user@gmail.com");
			userManager.deleteUser("user@gmail.com", "password");

			assertThrows(UserNotExistException.class,() -> {resetManager.changePassword(t, "newpass");});	
		}
		catch(EmailNotExistException|UserNotExistException e){
			fail();
		}		


		
	}
	/**
	* test if changing password to invalid throws InvalidPasswordException
	*/
	@Test
	void testChangePasswordInvalidPassword(){

		try{
			String t = linkManager.create("user@gmail.com");
			assertThrows(InvalidPasswordException.class,() -> {resetManager.changePassword(t, "");});
			assertThrows(InvalidPasswordException.class,() -> {resetManager.changePassword(t, null);});
		}
		catch(EmailNotExistException e){
			fail();
		}		


	}

}
