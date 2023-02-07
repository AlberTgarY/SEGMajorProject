package main.java.com.projectBackEnd.Services.User.Micronaut;

import io.micronaut.core.annotation.Introspected;

/**
 * This class encapsulates a user entity, excluding the ID.
 */
@Introspected
public class UserBody {

	private String name;
	private String password;
	private String email;

	/**
	 * Default constructor
	 */
	public UserBody(){}

	/**
	 * Constructor with name setting
	 * @param email		User email
	 * @param password	User password
	 * @param name		User name
	 */
	public UserBody(String email, String password,String name){
		this.email = email;
		this.password = password;
		this.name = name;	
	}

	/**
	 * Constructor without name setting
	 * @param email		User email
	 * @param password	User password
	 */
	public UserBody(String email, String password){
		this.email = email;
		this.password = password;
	}

	/**
	 * Get the user email
	 * @return Email of this user
	 */
	public String getEmail(){return email; }

	/**
	 * Get the user password
	 * @return Password of this user
	 */
	public String getPassword(){
		return password;
	}

	/**
	 * Sets the user email
	 * @param email Email to set
	 */
	public void setEmail(String email){
		this.email = email;
	}

	/**
	 * Set the user password as the given password
	 * @param password New value for password to set
	 */
	public void setPassword(String password){
		this.password = password;
	}

	/**
	 * Get the user name
	 * @return Name
	 */
	public String getName(){
		return name;
	}

	/**
	 * Set the user name as the given user name
	 * @param name New value for the username to set
	 */
	public void setName(String name){
		this.name = name;
	}


}
