package main.java.com.projectBackEnd.Services.Session;


/**
 *  Methods used by SessionManager for database queries for Session objects
 */
public interface SessionManagerInterface {

	String getNewSession(String email, int timeout);

	String getEmail(String token) throws NoSessionException;

	boolean verifySession(String token);

	void terminateSession(String token);

}

