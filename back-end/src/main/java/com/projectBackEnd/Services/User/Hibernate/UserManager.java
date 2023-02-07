package main.java.com.projectBackEnd.Services.User.Hibernate;

import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.*;
import org.apache.commons.validator.routines.EmailValidator;
import main.java.com.projectBackEnd.EntityManager;
import main.java.com.projectBackEnd.HibernateUtility;
import main.java.com.projectBackEnd.Services.Session.SessionManager;
import java.security.MessageDigest; 
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException; 
import java.util.List;
import java.math.BigInteger;

/**
 * UserManager defines methods to interact with the Users table in the database.
 * This class extends the EntityManager - supplying it with the rest of its interface methods.
 *
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
public class UserManager extends EntityManager implements UserManagerInterface {

	private static UserManagerInterface userManager;
	private static final int TIMEOUT = 18000; //Amount of time for which session will be valid (seconds)

	/**
	 * Private constructor implementing the Singleton design pattern
	 */
    private UserManager() {
        super();
        setSubclass(User.class);
        HibernateUtility.addAnnotation(User.class);
		userManager = this;
    }

	/**
	 * Get get the user manager
	 * @return userManager - if none has been defined, create a new UserManager object
	 */
	public static UserManagerInterface getUserManager(){
		if(userManager != null) return userManager;
		else return new UserManager();
	}

	/**
	 * Add a new user to the database
	 * @param email		The email of the new user
	 * @param password	The password of the new user
	 * @param name		The name of the new user
	 * @throws EmailExistsException Email already exists
	 * @throws InvalidEmailException Invalid email
	 * @throws IncorrectNameException Invalid name
	 * @throws InvalidPasswordException Invalid password
	 */
	public void addUser(String email, String password, String name) throws EmailExistsException, InvalidEmailException, IncorrectNameException,InvalidPasswordException {

		if(!EmailValidator.getInstance().isValid(email))
			throw new InvalidEmailException("email: " + email + " is invalid");

		if(getAll().stream().filter(u->((User)u).getEmail().equals(email)).count() > 0)
			throw new EmailExistsException("email: " + email + " already exsists");

		if(name == null || name.trim().isEmpty())
			throw new IncorrectNameException("incorrect name");

		if(password == null || password.trim().isEmpty())
			throw new InvalidPasswordException("invalid password");
	
		User user = new User(email,hash(password),name);
		insertTuple(user);
	}

	/**
	 * Check the attributes of the user and verifies
	 * @param email		User email
	 * @param password	User password
	 * @return A new session, with verified user
	 */
	public String verifyUser(String email,String password){
		if(getAll().stream().filter(u->(((User)u).getEmail().equals(email) && ((User)u).getPassword().equals(hash(password)))).count() > 0)
			return SessionManager.getSessionManager().getNewSession(email,TIMEOUT);
		else return null;
			
	}

	/**
	 * Change a user's password
	 * @param email			Email of the user
	 * @param newPassword	New value for the user's password
	 * @throws UserNotExistException Invalid user
	 * @throws InvalidPasswordException Invalid password
	 */
	public void changePassword(String email, String newPassword) throws UserNotExistException,InvalidPasswordException {

		if(newPassword == null || newPassword.trim().isEmpty()) throw new InvalidPasswordException("invalid password");
		List<User> users = getAll();
		User user = null;
		for(User u:users) if(u.getEmail().equals(email)) user = u;

		if(user == null) throw new UserNotExistException("there is no user with email: " + email);

		user.setPassword(hash(newPassword));
		update(user);
		
	}

	/**
	 * Change the email of a user
	 * @param oldEmail Old email of user
	 * @param newEmail New email of user
	 * @throws UserNotExistException Invalid user
	 * @throws EmailExistsException Pre-existing user
	 */
	public void changeEmail(String oldEmail, String newEmail) throws UserNotExistException,EmailExistsException { 
		List<User> users = getAll();
		User user = null;
		for(User u:users){
			if(u.getEmail().equals(oldEmail)) user = u;
			if(u.getEmail().equals(newEmail)) throw new EmailExistsException("email: " + newEmail + " already exists");
		}
		if(user == null) throw new UserNotExistException("there is no user with email: " + oldEmail);
		user.setEmail(newEmail);
		update(user);
	}

	/**
	 * Change the name of a user
	 * @param email Email of user
	 * @param name	Name of user
	 * @throws UserNotExistException Invalid user
	 * @throws IncorrectNameException Invalid name
	 */
	public void changeName(String email,String name) throws UserNotExistException, IncorrectNameException {

		List<User> users = getAll();
		User user = null;
		for(User u:users) if(u.getEmail().equals(email)) user = u;

		if(user == null) throw new UserNotExistException("there is no user with email: " + email);
		if(name == null || name.trim().isEmpty()) throw new IncorrectNameException("incorrect name");

		user.setName(name);
		update(user);
	}

	/**
	 * Filter through all users to find given user
	 * @param email The email of the user to search for
	 * @return The found user's name - or exception
	 * @throws UserNotExistException The user was not found
	 */
	public String getName(String email) throws UserNotExistException {

		List<User> users = getAll();
		for(User u:users) if(u.getEmail().equals(email)) return u.getName();
		throw new UserNotExistException("No such user");

	}

	/**
	 * Check that a user exists in the database
	 * @param email	User email to verify
	 * @return Verification status
	 */
	public boolean verifyEmail(String email){
		return (getAll().stream().filter(u->((User)u).getEmail().equals(email)).count() > 0);
	}

	/**
	 * Get a list of all the users stored in the database, without the passwords
	 * @return List of users
	 */
	public List<User> getUsers(){
		List<User> users = getAll();
		for(User u: users){
			u.setPassword("");
		}
		return users;
	}


	/**
	 * Hash a given password to add to the database
	 * @param password The password to hash
	 * @return The hashed password
	 */
	private String hash(String password) {

		String salt = "fX66CeuGKjmdkguhPEzp";
		int split = password.length()/3;
		String withSalt = password.substring(0,split) + salt + password.substring(split, password.length());
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
	 * Remove a user from the database
	 * @param email		Email of the user
	 * @param password	Password of the user
	 * @throws UserNotExistException Invalid user
	 */
	public void deleteUser(String email, String password) throws UserNotExistException{
		String token = verifyUser(email,password);
		if(token != null){
			List<User> users = getAll();

			SessionManager.getSessionManager().terminateSession(token);
			for(User u: users){
				if(u.getEmail().equals(email)){
					delete(u);
					return;
				}
			}
		}
		throw new UserNotExistException("user details incorrect");
	}
	


}
