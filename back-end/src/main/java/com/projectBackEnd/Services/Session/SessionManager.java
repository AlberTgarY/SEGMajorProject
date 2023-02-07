package main.java.com.projectBackEnd.Services.Session;
import main.java.com.projectBackEnd.EntityManager;
import main.java.com.projectBackEnd.HibernateUtility;

import java.util.List;
import java.sql.Timestamp;


/**
 * SessionManager defines methods for Session objects to interact with the database.
 * This class extends the EntityManager.
 *
 * Inspired by :
 * https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
public class SessionManager extends EntityManager implements SessionManagerInterface {

	private static SessionManagerInterface sessionManager;
	public static final int tokenLength = 50;
	/**
	 * Private constructor implementing the singleton design pattern
	 */
	private SessionManager() {

		super();
		setSubclass(Session.class);
		HibernateUtility.addAnnotation(Session.class);
		sessionManager = this;

	}


	/**
	 * Get session manager interface
	 * @return sessionManager ; if none has been defined, create a new SessionManager
	 */
	public static SessionManagerInterface getSessionManager() {
		if(sessionManager != null) return sessionManager;
		else return new SessionManager();
	}


	/**
	 * Create a new session
	 * @param email		User email
	 * @param timeout	Session timeout
	 * @return token of the created session
	 */
	public String getNewSession(String email, int timeout) {

		Session s = new Session(email,timeout);
		insertTuple(s);
		return s.getToken();

	}


	/**
	 * Get the Email address of the session corresponding to the given Token
	 * @param token	Primary key 'Token'
	 * @return Email address
	 */
	public String getEmail(String token) throws NoSessionException {

		List<Session> sessions = getAll();
		for (Session s: sessions) if(s.getToken().equals(token)) return s.getEmail();
		throw new NoSessionException("no such session");

	}


	/**
	 * Verify the session corresponding to the given Token
	 * @param token	Primary key of the session
	 * @return true if the session is open; else false
	 */
	public boolean verifySession(String token) {

		if(token == null || token.length() != tokenLength) return false;

		List<Session> sessions = getAll();
		Timestamp now;
		for (Session s: sessions) {
			if(s.getToken().equals(token)) {
				now = new Timestamp(System.currentTimeMillis());
				if(s.getTimeout().after(now)) return true;
				else {
					delete(s);
					return false;
				}
			}

		}
		return false;
	}


	/**
	 * Terminate the sessions corresponding to the given token
	 * @param token	Primary key 'Token'
	 */
	public void terminateSession(String token) {
		List<Session> sessions = getAll();
		try {
			deleteAllPast(getEmail(token));
		} catch(NoSessionException e) {	/*ignore*/	}
		for (Session session: sessions) if(session.getToken().equals(token)) delete(session);
	}

	/**
	 * Delete all the sessions that are timed out
	 * @param email	User email
	 */
	private void deleteAllPast(String email){

		List<Session> sessions = getAll();
		Timestamp now;

		for (Session s: sessions) {
			if(s.getEmail().equals(email)) {
				now = new Timestamp(System.currentTimeMillis());
				if(!s.getTimeout().after(now)) delete(s);
			}
		}
	}


}
