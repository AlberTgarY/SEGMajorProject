package main.java.com.projectBackEnd.Services.User.Micronaut;

import io.micronaut.core.annotation.Introspected;

/**
 * This class encapsulates the operations involved in changing an email of a user
 */
@Introspected
public class ChangeEmailBody {

	private String oldEmail;
	private String newEmail;

	/**
	 * Main constructor
	 * @param oldEmail Old email of user
	 * @param newEmail New email of user
	 */
	public ChangeEmailBody(String oldEmail,String newEmail){
		this.oldEmail = oldEmail;
		this.newEmail = newEmail;
	}

	/**
	 * Default constructor
	 */
	public ChangeEmailBody(){}

	/**
	 * Gets the old email attribute
	 * @return Old email attribute
	 */
	public String getOldEmail(){
		return oldEmail;
	}

	/**
	 * Gets the new email attribute
	 * @return new email attribute
	 */
	public String getNewEmail(){
		return newEmail;
	}

	/**
	 * Sets the old email attribute
	 * @param email The email to set
	 */
	public void setOldEmail(String email){
		this.oldEmail = email;
	}

	/**
	 * Sets the new email attribute
	 * @param email The email to set
	 */
	public void setNewEmail(String email){
		this.newEmail = email;
	}
}
