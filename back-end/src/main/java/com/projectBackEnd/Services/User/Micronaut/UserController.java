package main.java.com.projectBackEnd.Services.User.Micronaut;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;

import main.java.com.projectBackEnd.Services.Session.SessionManager;
import main.java.com.projectBackEnd.Services.Session.SessionManagerInterface;
import main.java.com.projectBackEnd.Services.Session.NoSessionException;

import main.java.com.projectBackEnd.Services.User.Hibernate.*;


import main.java.com.projectBackEnd.Services.ResetLinks.EmailNotExistException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.*;

import java.util.List;

/**
 * User Controller is a REST API endpoint.
 * It deals with the interactions between the server and the Users table in the database.
 * It provides HTTP requests for each of the queries that carry out the creation, deletion, retrieval, updating
 * and logging in and out of users
 */
@Controller("/user")
public class UserController {


    private final UserManagerInterface userManager = UserManager.getUserManager();
    private final SessionManagerInterface sessionManager = SessionManager.getSessionManager();


	/**
	 * Insert a new user into the database with user via an HTTP POST method
	 * @param user	User to add to the database
	 * @return HTTP response with relevant information resulting from the insertion of the user
	 */
	@Post("/create")
	public HttpResponse createUser(@Header("X-API-Key") String session, @Body UserBody user) {
		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		try {
			userManager.addUser(user.getEmail(),user.getPassword(),user.getName());
			return HttpResponse.created("user created"); 
		} catch(EmailExistsException e) {
			return HttpResponse.badRequest("user already exsits");
		} catch(InvalidEmailException e) {
			return HttpResponse.badRequest("invalid email address");
		} catch(InvalidPasswordException e) {
			return HttpResponse.badRequest("invalid password");
		} catch(IncorrectNameException e) {
			return HttpResponse.badRequest("invalid name");
		}
		
	}

	/**
	 * Login for a user via an HTTP Post method
	 * @param user	User to log in
	 * @return HTTP response with relevant information resulting from the logging in of the user
	 */
	@Post("/login")
	public HttpResponse<String> login(@Body UserBody user) {

		String token = userManager.verifyUser(user.getEmail(), user.getPassword());
		if(token != null) return HttpResponse.ok(token);
		else return HttpResponse.unauthorized();

	}

	/**
	 * Logout for a user via an HTTP Get method
	 * @param session	Current session
	 * @return HTTP response resulting from the logging out of the user
	 */
	@Get("/logout")
	public HttpResponse logout(@Header("X-API-Key") String session) {

		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		sessionManager.terminateSession(session);
		return HttpResponse.ok();

	}

	/**
	 * Get a list of all users stored in the database via an HTTP Get request
	 * @param session	Current session
	 * @return	HTTP response resulting from the Get request of all users
	 */
	@Get("/")
	public HttpResponse<List<User>> getAllUsers(@Header("X-API-Key") String session){
		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		return HttpResponse.ok(userManager.getUsers());
	}


	/**
	 * Remove a user from the database via an HTTP Delete request
	 * @param user	Name of the user to remove
	 * @return HTTP response with relevant information resulting from the deletion of the user
	 */
	@Delete("/delete")
	public HttpResponse deleteUser(@Body UserBody user) {
		try {
			userManager.deleteUser(user.getEmail(), user.getPassword());
			return HttpResponse.ok();
		} catch(UserNotExistException e){
			return HttpResponse.notFound("user does not exsist");
		}
	}

	/**
	 * Request a password reset via an HTTP Post request
	 * @param body	StringBody containing the reset link for the user
	 * @return HTTP response with relevant information resulting from the password reset request
	 */
	@Post("/password_reset_request")
	public HttpResponse<String> getPasswordReset(@Body StringBody body){

		try {
			PasswordReset.getPasswordResetManager().sendPasswordResetLink(body.getString());
		} catch(EmailNotExistException|ServerErrorException e){
			//frontend does not want an exception
		} finally {
			return HttpResponse.ok();
		}
	}

	/**
	 * Changes the password of a user after a reset request via an HTTP Put request
	 * @param body	StringBody containing the new password and the token for the user
	 * @return HTTP response with relevant information resulting on the password change
	 */
	@Put("/password_reset_change") 
	public HttpResponse<String> passwordReset(@Body PasswordResetBody body) {

		try{
        		PasswordReset.getPasswordResetManager().changePassword(body.getToken(), body.getPassword());
			return HttpResponse.ok();
		} catch(TokenNotExistException e){
			return HttpResponse.notFound("incorrect token");
		} catch(UserNotExistException e){
			return HttpResponse.notFound("token did not match any user");
		} catch(InvalidPasswordException e){
			return HttpResponse.badRequest("invalid password");
		}
	}

	/**
	 * Get the details of the user on the current session via an HTTP Get request
	 * @param session	Current session
	 * @return HTTP response resulting from the Get request
	 */
	@Get("/user_details")
	public HttpResponse<UserBody> getUserDetails(@Header("X-API-Key") String session){
		if(!sessionManager.verifySession(session))
			return HttpResponse.unauthorized();
		try{
			String email = sessionManager.getEmail(session);
			return HttpResponse.ok(new UserBody(email,"",userManager.getName(email)));

		}
		catch(NoSessionException e){
			return HttpResponse.unauthorized();
		}
		catch(UserNotExistException e){
			return HttpResponse.notFound();
		}
	}

	/**
	 * Change the email of the user on the current session via an HTTP Put request
	 * @param session	Current session
	 * @param body		New value for the user email
	 * @return HTTP response with relevant information resulting from the email change
	 */
	@Put("/change_email") 
	public HttpResponse<String> changeEmail(@Header("X-API-Key") String session, @Body StringBody body) {

		if(!sessionManager.verifySession(session)) return HttpResponse.unauthorized();
		else {
			try {
				String email = sessionManager.getEmail(session);
				userManager.changeEmail(email, body.getString());
				return HttpResponse.ok();
			} catch(NoSessionException e){
				return HttpResponse.unauthorized();
			} catch(UserNotExistException e){
				return HttpResponse.notFound("user does not exist");
			} catch(EmailExistsException e){
				return HttpResponse.badRequest("email already exists");
			}
		}
	}

	/**
	 * Change the name of the user on the current session via an HTTP Put request
	 * @param session	Current session
	 * @param body		New value for the username
	 * @return HTTP response resulting from the user name change
	 */
	@Put("/change_name") 
	public HttpResponse<String> changeName(@Header("X-API-Key") String session, @Body StringBody body){
		if(!sessionManager.verifySession(session))
			return HttpResponse.unauthorized();
		else{
			try{
				String email = sessionManager.getEmail(session);
				userManager.changeName(email, body.getString());
				return HttpResponse.ok();
			} catch(NoSessionException e){
				return HttpResponse.unauthorized();
			} catch(UserNotExistException e){
				return HttpResponse.notFound("user does not exist");
			} catch(IncorrectNameException e){
				return HttpResponse.badRequest("incorrect name");
			}
		}
	}


	/**
	 * Change the password of the user on the current session via an HTTP Put request
	 * @param session	Current session
	 * @param body		New password for the user
	 * @return HTTP response resulting from the password change
	 */
	@Put("/change_password") 
	public HttpResponse<String> changePassword(@Header("X-API-Key") String session, @Body StringBody body) {
		if(!sessionManager.verifySession(session))
			return HttpResponse.unauthorized();
		else{
			try{
				String email = sessionManager.getEmail(session);
				userManager.changePassword(email, body.getString());
				return HttpResponse.ok();
			} catch(NoSessionException e){
				return HttpResponse.unauthorized();
			} catch(UserNotExistException e){
				return HttpResponse.notFound("user does not exist");
			} catch(InvalidPasswordException e){
				return HttpResponse.badRequest("invalid password");
			}
		}
	}


}

