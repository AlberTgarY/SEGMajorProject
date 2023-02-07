package main.java.com.projectBackEnd.Services.User.Micronaut;

import io.micronaut.core.annotation.Introspected;

/**
 * This class is used to encapsulate a string in order to facilitate the making of high level string transactions
 * with the micronaut framework.
 */
@Introspected
public class StringBody {

	private String string;

	/**
	 * Default constructor
	 */
	public StringBody(){}

	/**
	 * Main constructor
	 * @param string The central string to be stored in the class variable
	 */
	public StringBody(String string){
		this.string = string;
	}

	/**
	 * Gets the string
	 * @return  string
	 */
	public String getString(){
		return string;
	}

	/**
	 * Set the string as the input string
	 * @param string	New string value
	 */
	public void setString(String string){
		this.string = string;
	}

}
