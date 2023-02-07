package main.java.com.projectBackEnd.Services.User.Micronaut;

import io.micronaut.core.annotation.Introspected;

/**
 * This class encapsulates the operations involved in resetting a password
 */
@Introspected
public class PasswordResetBody {

	private String password;
	private String token; //Token used for user validation


	/**
	 * Default constructor
	 */
	public PasswordResetBody(){}


	/**
	 * Main constructor
	 * @param token		Validation token
	 * @param password	Password to reset
	 */
	public PasswordResetBody(String token, String password){
		this.token = token;
		this.password = password;
	}

	/**
	 * Get the user password
	 * @return The password for this password
	 */
	public String getPassword(){
		return password;
	}

	/**
	 * Set the user password as the given password
	 * @param password The new password
	 */
	public void setPassword(String password){this.password = password; }

	/**
	 * Get the authentication token
	 * @return The token
	 */
	public String getToken(){
		return token;
	}

	/**
	 * Set the token value as the given token
	 * @param token The new token value
	 */
	public void setToken(String token){
		this.token= token;
	}
}
