package main.java.com.projectBackEnd.Services.User.Hibernate;
import main.java.com.projectBackEnd.Services.ResetLinks.ResetLinkManager;
import main.java.com.projectBackEnd.Services.ResetLinks.EmailNotExistException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.InvalidPasswordException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.ServerErrorException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.TokenNotExistException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.UserNotExistException;

/**
 * This class is an implementation of the PasswordResetInterface which carries out the lower level requests
 * involved in resetting a user password
 */
public class PasswordReset implements PasswordResetInterface {

	private static PasswordResetInterface passwordResetManager;

	/**
	 * Private constructor implementing Singleton design pattern
	 */
	private PasswordReset(){
		passwordResetManager = this;
	}

	/**
	 * Get the password reset manager
	 * @return passwordResetManager - if none has been defined, create a new PasswordReset object
	 */
	public static PasswordResetInterface getPasswordResetManager(){
		if(passwordResetManager != null) return passwordResetManager;
		else return new PasswordReset();
	}

	/**
	 * Sends a reset verification link to a provided email
	 * @param email	The email to send the link to
	 * @throws EmailNotExistException	Non-existing email provided
	 * @throws ServerErrorException        Internal server error
	 */
	public void sendPasswordResetLink(String email) throws EmailNotExistException, ServerErrorException {

		String token = ResetLinkManager.getResetLinkManager().create(email);
		String title = "Password Reset Request";
		String content = "please click the link to reset the password: \n" + token + "\n if you did not request password reset please ignore this message";
		if(!SendMail.send(email, title, content))
		 	throw new ServerErrorException("error has occured");
	
	}

	/**
	 * Invokes the password-changing business logic methods in the manager
	 * @param token		The validation token used to authenticate the user
	 * @param password	The new password
	 * @throws TokenNotExistException    Invalid token
	 * @throws UserNotExistException    Invalid user
	 * @throws InvalidPasswordException Invalid password
	 */
	public void changePassword(String token, String password) throws TokenNotExistException, UserNotExistException, InvalidPasswordException {

		if(password == null || password.isEmpty()) throw new InvalidPasswordException("invalid password");
		String email = ResetLinkManager.getResetLinkManager().getEmail(token);
		if(ResetLinkManager.getResetLinkManager().exist(token)){
			ResetLinkManager.getResetLinkManager().delete(token);
			UserManager.getUserManager().changePassword(email,password);
		}
		else throw new TokenNotExistException("incorrect token");
	}
}
