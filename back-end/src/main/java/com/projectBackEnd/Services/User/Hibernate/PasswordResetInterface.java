package main.java.com.projectBackEnd.Services.User.Hibernate;
import main.java.com.projectBackEnd.Services.ResetLinks.EmailNotExistException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.InvalidPasswordException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.ServerErrorException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.TokenNotExistException;
import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.UserNotExistException;

/**
 * The methods invoked by API to reset a user password
 */
public interface PasswordResetInterface {

	void sendPasswordResetLink(String email)
			throws EmailNotExistException, ServerErrorException;

	void changePassword(String token,String password)
			throws TokenNotExistException, UserNotExistException, InvalidPasswordException;
}
