package main.java.com.projectBackEnd.Services.Session;

import main.java.com.projectBackEnd.TableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Random;
import java.sql.Timestamp;

import java.io.Serializable;


/**
 * Session objects are database entities for the table 'Sessions' defined in this class.
 * They have a Token, a date of the session been created, a Timeout, and an Email
 * within the site, a title, and some content.
 *
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
@Entity
@Table(name = Session.TABLENAME)
public class Session implements TableEntity<Session> {

	// 'Session' database table name and columns
	final static String TABLENAME = "Sessions";
	private final static String TOKEN = "Token";
	private final static String DATE = "Date";
	private final static String TIMEOUT = "Timeout";
	private final static String EMAIL = "Email";


	// The primary key token, used for authentication
	@Id
	@Column(name = TOKEN)
	private String token;

	@Column(name = DATE)
	private Timestamp date;

	@Column(name = TIMEOUT)
	private Timestamp timeout;

	@Column(name = EMAIL)
	private String email;


	/**
	 * Empty constructor
	 */
	public Session(){}


	/**
	 * Main constructor
	 * @param email		User email
	 * @param timeout	Timeout
	 */
	public Session(String email, int timeout) {

		this.email = email;
		this.timeout = new Timestamp(System.currentTimeMillis() + timeout * 1000);
		this.date = new Timestamp(System.currentTimeMillis());
		this.token = generateToken();

	}


	/**
	 * Get the primary key 'token'
	 * @return unique token
	 */
	public Serializable getPrimaryKey(){
		return token;
	}


	/**
	 * Generate a token for the session
	 * @return generated token
	 */
	private String generateToken() {

		Random rand = new Random();
		String s;
		String alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
		do {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < SessionManager.tokenLength;++i) sb.append(alphaNum.charAt(rand.nextInt(alphaNum.length())));
			s = sb.toString();
		} while(s == null || SessionManager.getSessionManager().verifySession(s));
		return s;

	}


	/**
	 * Get the token attribute
	 * @return Token
	 */
	public String getToken(){
		return token;
	}


	/**
	 * Get the Date attribute
	 * @return Date
	 */
	private Timestamp getDate(){
		return date;
	}


	/**
	 * Get the timeout attribute (in seconds)
	 * @return timeout
	 */
	Timestamp getTimeout(){
		return timeout;
	}


	/**
	 * Get the email associated to the session
	 * @return email
	 */
	public String getEmail(){
		return email;
	}


	/**
	 * Copy the values of input object
	 * @param sessionToCopy	Session to copy
	 * @return updated object
	 */
	public Session copy(Session sessionToCopy) {

		token = sessionToCopy.getToken();
		date = sessionToCopy.getDate();
		timeout = sessionToCopy.getTimeout();
		email = sessionToCopy.getEmail();
		return this;
	}


} 
