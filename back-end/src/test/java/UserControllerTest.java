package test.java;


import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

import main.java.com.projectBackEnd.Services.User.Hibernate.*;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.*;
import main.java.com.projectBackEnd.Services.User.Micronaut.*;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;
import main.java.com.projectBackEnd.Services.ResetLinks.*;


import javax.inject.Inject;


import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


import main.java.com.projectBackEnd.HibernateUtility;
import main.java.com.projectBackEnd.EntityManager;
import java.util.List;
/**
* Class to unit test interactions between rest calls and system with respect to user functionality
*/
@MicronautTest
class UserControllerTest{
    

	
	@Inject
    @Client("/")
    private HttpClient client;
	private static final UserManagerInterface userManager = UserManager.getUserManager();
	private static final ResetLinkManagerInterface linkManager = ResetLinkManager.getResetLinkManager();	
	private static final SessionManagerInterface sessionManager = SessionManager.getSessionManager();
	private static String token;
	/**
	* Set database to test database
	*/
    @BeforeAll
    static void setUpDatabase() {
        HibernateUtility.setResource("testhibernate.cfg.xml");
	
    }
	/**
	* Closes the database and deletes any user, link or session objects
	*/
    @AfterAll
    static void closeDatabase() {
		((EntityManager)userManager).deleteAll();
		((EntityManager)linkManager).deleteAll();
		((EntityManager)sessionManager).deleteAll();
        HibernateUtility.shutdown();
    }
	/**
	* Deletes all session, user or link objects and add user for authorisation
	*/
    @BeforeEach
    void setUp() {
		((EntityManager)userManager).deleteAll();
		((EntityManager)linkManager).deleteAll();
		((EntityManager)sessionManager).deleteAll();
	try{
		userManager.addUser("unique_email@email.com","pass","not me");
		token = userManager.verifyUser("unique_email@email.com","pass");
	}
	catch(Exception e){
		fail();
	}
    }
	/**
	* Test if creating a valid user behaves correctly
	*/
	@Test
	void testCreateUser(){
		assertEquals(HttpStatus.CREATED,addUser("username@mail.com","password","na me",token).getStatus());
		String token1 = userManager.verifyUser("username@mail.com","password");
		assertNotNull(token1);
		sessionManager.terminateSession(token1);

		assertEquals(HttpStatus.CREATED,addUser("name@mail.com","passw@/-ord","name",token).getStatus());
		String token2 = userManager.verifyUser("name@mail.com","passw@/-ord");
		assertNotNull(token2);
		sessionManager.terminateSession(token2);

		assertEquals(HttpStatus.CREATED,addUser("nam123e@mail.com","pas321sw@/-ord","na-me",token).getStatus());
		String token3 = userManager.verifyUser("nam123e@mail.com","pas321sw@/-ord");
		assertNotNull(token3);
		sessionManager.terminateSession(token3);	
	
		assertEquals(HttpStatus.CREATED,addUser("nam123e2@mail.com","pa  s321sw@/-ord","nam e",token).getStatus());
		String token4 = userManager.verifyUser("nam123e2@mail.com","pa  s321sw@/-ord");
		assertNotNull(token4);
		sessionManager.terminateSession(token4);

	}
	/**
	* Testing if creating users with passwords with with whitespace only or null throws an exception
	*/
	@Test
	void testCreateUserIncorrectPassword(){
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl","","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown1.getStatus());
	
		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl",null,"name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown2.getStatus());

		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl","	 ","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown3.getStatus());

     	HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class, () -> {
            	client.toBlocking().retrieve(HttpRequest.POST("/user/create",new UserBody("name@name.pl","","lmao")).header("X-API-Key",token));
        });
		assertEquals(HttpStatus.BAD_REQUEST, thrown4.getStatus());
		assertEquals("invalid password", thrown4.getResponse().getBody().get());
	}

	/**
	* Test if creating a user with names whose symbols are whitespace only or null returns exception
	*/
	@Test
	void testCreateUserIncorrectName(){
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl","password","",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown1.getStatus());


		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl","password",null,token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown2.getStatus());

		HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@name.pl","password"," 	",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown4.getStatus());

     	HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
            	client.toBlocking().retrieve(HttpRequest.POST("/user/create",new UserBody("name@name.pl","password",null)).header("X-API-Key",token));
        });
		assertEquals(HttpStatus.BAD_REQUEST, thrown3.getStatus());
		assertEquals("invalid name", thrown3.getResponse().getBody().get());

	}
	/**
	* Test if creating user while not having a correct session token returns an HTTP error
	*/
	@Test
	void testCreateUserUnauthorized(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			addUser("username@mail.com","password","na me","");
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());

		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
			addUser("username@mail.com","password","na me","321bYUdsd36782F14ASd3DSa12vbuyds");
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown2.getStatus());
	}

	/**
	* Test if creating a user account with an incorrect email returns an exception
	*/
	@Test
	void testCreateUserIncorrectEmail(){
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name","password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown1.getStatus());
		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class,()->{
			addUser("name@","password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown2.getStatus());
		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class,()->{
			addUser("@name","password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown3.getStatus());
		HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class,()->{
			addUser("@","password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown4.getStatus());
		HttpClientResponseException thrown5 = assertThrows(HttpClientResponseException.class,()->{
			addUser("@","password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown5.getStatus());
		HttpClientResponseException thrown6 = assertThrows(HttpClientResponseException.class,()->{
			addUser("","password","name",token);
		});	
		assertEquals(HttpStatus.BAD_REQUEST, thrown6.getStatus());
		HttpClientResponseException thrown7 = assertThrows(HttpClientResponseException.class,()->{
			addUser(null,"password","name",token);
		});
		assertEquals(HttpStatus.BAD_REQUEST, thrown7.getStatus());


        HttpClientResponseException thrown8 = assertThrows(HttpClientResponseException.class, () -> {
            	client.toBlocking().retrieve(HttpRequest.POST("/user/create",new UserBody(null,"pas1234//--+sword","name")).header("X-API-Key",token));
        });
		assertEquals(HttpStatus.BAD_REQUEST, thrown8.getStatus());
		assertEquals("invalid email address", thrown8.getResponse().getBody().get());

	}

	/**
	* Test if creating an user with duplicate email creates an exception
	*/
	@Test
	void testCreateDuplicateUser(){
		try{
			userManager.addUser("username@mail.com","password","name");
		}
		catch(InvalidEmailException | EmailExistsException | IncorrectNameException | InvalidPasswordException e){
			fail();
		}
		String token1 = userManager.verifyUser("username@mail.com","password");
		assertNotNull(token1);
		sessionManager.terminateSession(token1);
	
		

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            	client.toBlocking().retrieve(HttpRequest.POST("/user/create",new UserBody("username@mail.com","password","name")).header("X-API-Key",token));
        });
		assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
		assertEquals("user already exsits", thrown.getResponse().getBody().get());
	}

	/**
	* Test if logging in behaves correctly with correct inputs
	*/
	@Test
	void TestLoginCorrect(){
		assertEquals(HttpStatus.CREATED, addUser("mail@mail.com", "pass", "name",token).getStatus());
		HttpResponse<String> response = login("mail@mail.com", "pass");
		assertEquals(HttpStatus.OK, response.getStatus());

		assertEquals(HttpStatus.CREATED, addUser("mail1@mail.com", "pa  s321sw@/-ord", "name",token).getStatus());
		String response1 = getToken("mail1@mail.com", "pa  s321sw@/-ord");
		assertNotNull(response1);

		assertEquals(HttpStatus.CREATED, addUser("mail2@mail.com", "pas321sw@/-ord", "name",token).getStatus());
		String response2 = getToken("mail2@mail.com", "pas321sw@/-ord");
		assertNotNull(response2);

		((EntityManager)sessionManager).deleteAll();
	}

	/**
	* Test if logging in with null or empty credentials throws an http exception
	*/
	@Test
	void TestLoginIncorrect(){
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody(null,null,"name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());

        HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody("","","name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());

        HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody(null,"pass","name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown2.getStatus());

	    HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody("pass",null,"name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown3.getStatus());

        HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody("","pass","name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown4.getStatus());

	    HttpClientResponseException thrown5 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody("pass","","name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown5.getStatus());

	    HttpClientResponseException thrown6 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody("email@email.com","password","name")));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown6.getStatus());

	}

	/**
	* Test if logging out with correct details behaves correctly
	*/
	@Test
	void testLogout(){
		addUser("admin@admin.com","admin","not me",token);
		String token = getToken("admin@admin.com","admin");
		assertEquals(HttpStatus.OK, client.toBlocking().exchange(HttpRequest.GET("/user/logout").header("X-API-Key", token)).getStatus());
		assertFalse(sessionManager.verifySession(token));
	}

	/**
	* Test if logging out while not having a correct session token throws an http exception
	*/
	@Test
	void testLogoutUnauthorized(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.GET("/user/logout").header("X-API-Key", "321bYUdsd36782F14ASd3DSa12vbuyds"));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.GET("/user/logout").header("X-API-Key", ""));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());
	}

	/**
	* Test if adding users and than getting it all creates matching result
	*/
	@Test
	void testGetAll(){

		assertEquals(HttpStatus.CREATED,addUser("admin@admin.com","admin","not me",token).getStatus());
		assertEquals(HttpStatus.CREATED,addUser("admin1@admin.com","admin","not me",token).getStatus());
		assertEquals(HttpStatus.CREATED,addUser("admin2@admin.com","admin","not me",token).getStatus());
		String token = getToken("admin@admin.com","admin");
		List<User> users = client.toBlocking().retrieve(HttpRequest.GET("/user").header("X-API-Key", token), Argument.of(List.class, User.class));
		assertEquals(users.size(),4);
		for(User u:users){
			assertEquals(u.getName(),"not me");
			assertNull(u.getPassword());
		}
		
	}

	/**
	* Test if getting all users while not having a correct session token returns an HTTP error
	*/
	@Test
	void testGetAllUnauthorized(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.GET("/user").header("X-API-Key", ""));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.GET("/user").header("X-API-Key", "321bYUdsd36782F14ASd3DSa12vbuyds"));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());
	}

	/**
	* Testing if delete with correct details behaves correctly
	*/
 	@Test
	void testDeleteCorrect(){
			addUser("username@mail.com","password","na me",token);
			assertEquals(HttpStatus.OK,delete("username@mail.com","password").getStatus());
			assertNull(userManager.verifyUser("username@mail.com","password"));

			addUser("name@mail.com","passw@/-ord","name",token);
			assertEquals(HttpStatus.OK,delete("name@mail.com","passw@/-ord").getStatus());
			assertNull(userManager.verifyUser("name@mail.com","passw@/-ord"));

			addUser("nam123e@mail.com","pas32sw@/-ord","na-me",token);
			assertEquals(HttpStatus.OK,delete("nam123e@mail.com","pas32sw@/-ord").getStatus());
			assertNull(userManager.verifyUser("nam123e@mail.com","pas32sw@/-ord"));

			addUser("nam123e@mail.com","pas32  1sw@/-ord","na-me",token);
			assertEquals(HttpStatus.OK,delete("nam123e@mail.com","pas32  1sw@/-ord").getStatus());
			assertNull(userManager.verifyUser("nam123e@mail.com","pas32  1sw@/-ord"));
	}

	/**
	* Test if deleting users with incorrect details returns a HTTP error
	*/
	@Test
	void testDeleteIncorrect(){
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody("","")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown.getStatus());

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody(null,null)));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown1.getStatus());

		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody("aa",null)));
		});
		assertEquals(HttpStatus.NOT_FOUND, thrown2.getStatus());

		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody(null,"aa")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());

		HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody("","aa")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown4.getStatus());

	    HttpClientResponseException thrown5 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody("aa","")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown5.getStatus());

	    HttpClientResponseException thrown6 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody("valid@email.com","correct_password")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown6.getStatus());
	}

	/**
	* Test if sending a password reset request behaves correctly with correct details
	*/
	@Test
	void testGetPasswordResetCorrect(){
		assertEquals(HttpStatus.CREATED,addUser("test@gmail.com","password","name",token).getStatus());
		assertTrue(userManager.verifyEmail("test@gmail.com"));

		HttpResponse response = client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody("test@gmail.com")));
		assertEquals(HttpStatus.OK , response.getStatus());
	}
	/**
	* Check if sending a password reset request to not existing email or empty, null email returns HTTP error
	*/
	@Test
	void testGetPasswordResetIncorrect(){

		assertEquals(HttpStatus.OK , client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody(""))).getStatus());

		assertEquals(HttpStatus.OK , client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody(null))).getStatus());

		assertEquals(HttpStatus.OK , client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody("test@gmail.com"))).getStatus());
		
		assertEquals(HttpStatus.OK , client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody("test@"))).getStatus());

		assertEquals(HttpStatus.OK , client.toBlocking().exchange(HttpRequest.POST("/user/password_reset_request",new StringBody("@gmail.com"))).getStatus());
	}
	/**
	* Test if changing password with correct token behaves correctly
	*/
	@Test
	void testPasswordResetCorrect(){
		try{
			userManager.addUser("mail@mail.com","password","name");
		}
		catch(InvalidEmailException|EmailExistsException|IncorrectNameException|InvalidPasswordException e){
			fail();
		}
		try{
			String token = ResetLinkManager.getResetLinkManager().create("mail@mail.com");
			assertTrue(ResetLinkManager.getResetLinkManager().exist(token));
			HttpResponse response = client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(token,"test")));
			assertEquals(HttpStatus.OK , response.getStatus());
			assertFalse(ResetLinkManager.getResetLinkManager().exist(token));
			assertNotNull(userManager.verifyUser("mail@mail.com","test"));
		}
		catch(EmailNotExistException e){
			fail();
		}	
	}
	/**
	* Test if changing password with wrong token returns an exception
	*/
	@Test
	 void testPasswordResetIncorrect(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody("","test")));
		});
	 	assertEquals(HttpStatus.NOT_FOUND , thrown.getStatus());

 		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new
			PasswordResetBody("veryCorreCstTokenveryCorreCstTokenveryCorreCstTokenveryCorreCstTokenveryCorreCstToken","test")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown1.getStatus());

 		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(null,"test")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown2.getStatus());

 		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody("veryCorreCstToken","test")));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());

		try{
			userManager.addUser("mail@mail.com","password","name");
		} catch(InvalidEmailException|EmailExistsException|IncorrectNameException|InvalidPasswordException e){
			fail();
		}

		try{
			String token = ResetLinkManager.getResetLinkManager().create("mail@mail.com");
			assertTrue(ResetLinkManager.getResetLinkManager().exist(token));
 			HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(token,"")));
			});
			assertEquals(HttpStatus.BAD_REQUEST , thrown4.getStatus());
			assertNull(userManager.verifyUser("mail@mail.com","test"));
			HttpClientResponseException thrown5 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(token,null)));
			});
			assertEquals(HttpStatus.BAD_REQUEST , thrown5.getStatus());
			HttpClientResponseException thrown6 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(token,"	 ")));
			});
			assertEquals(HttpStatus.BAD_REQUEST , thrown6.getStatus());
			assertNull(userManager.verifyUser("mail@mail.com","test"));


		} catch(EmailNotExistException e){
			fail();
		}	
	}
	@Test
	void testPasswordResetUserNotMatched(){
		try{
			userManager.addUser("mail@mail.com","password","name");
		} catch(InvalidEmailException|EmailExistsException|IncorrectNameException|InvalidPasswordException e){
			fail();
		}

		try{
			String token = ResetLinkManager.getResetLinkManager().create("mail@mail.com");
			assertTrue(ResetLinkManager.getResetLinkManager().exist(token));
			userManager.deleteUser("mail@mail.com","password");
 			HttpClientResponseException thrown4 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/password_reset_change",new PasswordResetBody(token,"")));
			});
			


		} catch(EmailNotExistException|UserNotExistException e){
			fail();
		}	
	}
	/**
	* Test if getting a name from correct accounts behaves correctly
	*/
	@Test
	void testGetUserDetailsCorrect(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token0 = userManager.verifyUser("email@email.com", "password");
		UserBody u = getUserDetails(token0);
		assertEquals("name",u.getName());
		assertEquals("email@email.com",u.getEmail());
		sessionManager.terminateSession(token0);

		assertEquals(HttpStatus.CREATED, addUser("email1@email.com", "password","na me",token).getStatus());
		String token1 = userManager.verifyUser("email1@email.com", "password");
		UserBody u1 = getUserDetails(token1);
		assertEquals("na me",u1.getName());
		assertEquals("email1@email.com",u1.getEmail());
		sessionManager.terminateSession(token1);

		assertEquals(HttpStatus.CREATED, addUser("email2@email.com", "password","na-me",token).getStatus());
		String token2 = userManager.verifyUser("email2@email.com", "password");
		UserBody u2 = getUserDetails(token2);
		assertEquals("na-me",u2.getName());
		assertEquals("email2@email.com",u2.getEmail());
		sessionManager.terminateSession(token2);
	}
	/**
	* Test if getting a name while not being logged in returns unauthorized HTTP exception
	*/
	@Test
	void testGetUserDetailsIncorrect(){
			HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.GET("/user/user_details").header("X-API-Key","243fdsfdvqnSDFdsaSDF"));
			});
			assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());

			HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.GET("/user/user_details").header("X-API-Key",""));
			});
			assertEquals(HttpStatus.UNAUTHORIZED , thrown2.getStatus());	
			assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
			String token0 = userManager.verifyUser("email@email.com", "password");
			try{
				userManager.deleteUser("email@email.com", "password");			
			}
			catch(UserNotExistException e){
				fail();
			}
			HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.GET("/user/user_details").header("X-API-Key",token0));
			});
			assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());	
	}
	/**
	* Testing if changing email with correct user details behaves correctly
	*/
	@Test
	void testChangeEmailCorrect(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());

		String token = userManager.verifyUser("email@email.com", "password");

		assertNotNull(token);
		assertEquals(HttpStatus.OK,changeEmail(token,"newemail@email.com").getStatus());
		sessionManager.terminateSession(token);

		String token1 = userManager.verifyUser("newemail@email.com", "password");

		assertNotNull(token1);
		assertEquals(HttpStatus.OK,changeEmail(token1,"email@email.com").getStatus());
		sessionManager.terminateSession(token1);

		String token2 = userManager.verifyUser("email@email.com", "password");
		assertNotNull(token2);
		sessionManager.terminateSession(token2);


	}

	/**
	* Test if changing an email to a user that does not exist returns an error
	*/
	@Test
	void testChangeEmailUserNotExist(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/change_email",new StringBody("email@email.com")).header("X-API-Key",""));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());	
		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/change_email",new StringBody("email@email.com")).header("X-API-Key","verycorrecttoken"));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());

		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token0 = userManager.verifyUser("email@email.com", "password");
		try{
			userManager.deleteUser("email@email.com", "password");			
		}
		catch(UserNotExistException e){
			fail();
		}
		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/change_email",new StringBody("email@email.com")).header("X-API-Key",token0));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());		
	}
	/**
	* Test if changing an email to an existing one returns an error
	*/
	@Test
	void testChangeEmailEmailExist(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		assertEquals(HttpStatus.CREATED, addUser("email1@email.com", "password","name",token).getStatus());
		String token = userManager.verifyUser("email@email.com", "password");
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_email",new StringBody("email1@email.com")).header("X-API-Key",token));
			});
		assertEquals(HttpStatus.BAD_REQUEST , thrown.getStatus());	
		sessionManager.terminateSession(token);
	}

	/**
	* Test if changing a name to a correct one behaves correctly
	*/
	@Test
	void testChangeNameCorrect(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token = userManager.verifyUser("email@email.com", "password");
		try{
			assertEquals(HttpStatus.OK,changeName(token,"na me").getStatus());
			assertEquals("na me",userManager.getName("email@email.com"));

			assertEquals(HttpStatus.OK,changeName(token,"na-me").getStatus());
			assertEquals("na-me",userManager.getName("email@email.com"));

		}
		catch(UserNotExistException e){
			fail();
		}

		sessionManager.terminateSession(token);
	}
	/**
	* Test if changing a name of an user that does not exist throws a HTTP exception
	*/
	@Test
	void testChangeNameUserNotExist(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody("name")).header("X-API-Key",""));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());	

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody("name")).header("X-API-Key","vERyCorreCtToken123456"));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());


		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token0 = userManager.verifyUser("email@email.com", "password");
		try{
			userManager.deleteUser("email@email.com", "password");			
		}
		catch(UserNotExistException e){
			fail();
		}
		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody("name")).header("X-API-Key",token0));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());		
	}
	/**
	* Test if changing a name to invalid throws an http exception
	*/
	@Test
	void testChangeNameInvalid(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token = userManager.verifyUser("email@email.com", "password");

		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody("")).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown.getStatus());	


		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody(null)).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown1.getStatus());

		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody(" 	")).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown2.getStatus());		
		sessionManager.terminateSession(token);
	}
	/**
	* Test if changing a password with correct setting behaves correctly
	*/
	@Test
	void testChangePasswordCorrect(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token = userManager.verifyUser("email@email.com", "password");

		assertEquals(HttpStatus.OK,changePassword(token,"newPassoword").getStatus());
		sessionManager.terminateSession(token);
		
		String token1 = userManager.verifyUser("email@email.com","newPassoword");
		assertNotNull(token1);

		assertEquals(HttpStatus.OK,changePassword(token1,"newPassoword").getStatus());
		sessionManager.terminateSession(token1);

		String token2 = userManager.verifyUser("email@email.com","newPassoword");
		assertNotNull(token1);

		assertEquals(HttpStatus.OK,changePassword(token2,"newPas   :}{?><\\|soword").getStatus());
		sessionManager.terminateSession(token2);

		String token3 = userManager.verifyUser("email@email.com","newPas   :}{?><\\|soword");
		assertNotNull(token3);
		sessionManager.terminateSession(token3);


	}
	/**
	* Test if changing a password of a not existing user throws a HTTP exception
	*/
	@Test
	void testChangePasswordUserNotExist(){
		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody("pass")).header("X-API-Key",""));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown.getStatus());	

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody("pass")).header("X-API-Key","312dsahdu32br23o87"));
		});
		assertEquals(HttpStatus.UNAUTHORIZED , thrown1.getStatus());	


		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token0 = userManager.verifyUser("email@email.com", "password");
		try{
			userManager.deleteUser("email@email.com", "password");			
		}
		catch(UserNotExistException e){
			fail();
		}
		HttpClientResponseException thrown3 = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody("pass")).header("X-API-Key",token0));
		});
		assertEquals(HttpStatus.NOT_FOUND , thrown3.getStatus());	
	}
	/**
	* Test if changing a password to invalid password throws a HTTP exception
	*/
	@Test
	void testChangePasswordInvalid(){
		assertEquals(HttpStatus.CREATED, addUser("email@email.com", "password","name",token).getStatus());
		String token = userManager.verifyUser("email@email.com", "password");

		HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody(null)).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown.getStatus());	

		HttpClientResponseException thrown1 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody("")).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown1.getStatus());	


		HttpClientResponseException thrown2 = assertThrows(HttpClientResponseException.class, () -> {
				client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody(" 	")).header("X-API-Key",token));
		});
		assertEquals(HttpStatus.BAD_REQUEST , thrown2.getStatus());	
		sessionManager.terminateSession(token);
	}
	/**
	* test if change email body getters ands setters work correctly
	*/
	@Test
	void testChangeEmailBody(){
		ChangeEmailBody b = new ChangeEmailBody("","");
		ChangeEmailBody b1 = new ChangeEmailBody();
		assertNotNull(b);
		assertNotNull(b1);
		assertEquals(b.getOldEmail(),"");
		assertEquals(b.getNewEmail(),"");
		b.setOldEmail("1");
		b.setNewEmail("1");
		assertEquals(b.getOldEmail(),"1");
		assertEquals(b.getNewEmail(),"1");

	}	
	/**
	 * Create a post request to add user
	 * @return HTTP response from the request
	 */
	private HttpResponse addUser(String email, String password, String name,String token){
		return client.toBlocking().exchange(HttpRequest.POST("/user/create",new UserBody(email,password,name)).header("X-API-Key", token));
	}

	/**
	 * Creates a post request to login a user
	 * @return HTTP response from the request
	 */
	private HttpResponse login(String email, String password){
		return client.toBlocking().exchange(HttpRequest.POST("/user/login",new UserBody(email,password)));

	}

	/**
	 * Gets a token from given user credentials
	 * @return Session token from login request with given credentials
	 */
	private String getToken(String email, String password){
		return client.toBlocking().retrieve(HttpRequest.POST("/user/login",new UserBody(email,password)),String.class);

	}

	/**
	 * Creates a delete request to delete user
	 * @return HTTP response from the request
	 */
	private HttpResponse delete(String email, String password){
		return client.toBlocking().exchange(HttpRequest.DELETE("/user/delete",new UserBody(email,password)));
	}

	/**
	 * Creates a get request to get name of the user with given session token
	 * @return UserBody of the user from http response, containing user's name and email
	 */
	private UserBody getUserDetails(String token){
		return client.toBlocking().retrieve(HttpRequest.GET("/user/user_details").header("X-API-Key",token), UserBody.class);
	}

	/**
	 * Creates a request to change email of user
	 * @return HTTP response from the request
	 */
	private HttpResponse changeEmail(String token, String email){
		return client.toBlocking().exchange(HttpRequest.PUT("/user/change_email",new StringBody(email)).header("X-API-Key",token));
	}

	/**
	 * Creates a request to change name of the user
	 * @return HTTP response from the request
	 */
	private HttpResponse changeName(String token, String name){
		return client.toBlocking().exchange(HttpRequest.PUT("/user/change_name",new StringBody(name)).header("X-API-Key",token));
	}

	/**
	 * Creates a request to change name of the user
	 * @return HTTP response from the request
	 */
	private HttpResponse changePassword(String token, String password){
		return client.toBlocking().exchange(HttpRequest.PUT("/user/change_password",new StringBody(password)).header("X-API-Key",token));
	}

}
