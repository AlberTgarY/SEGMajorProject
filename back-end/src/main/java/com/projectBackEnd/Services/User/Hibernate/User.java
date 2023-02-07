package main.java.com.projectBackEnd.Services.User.Hibernate;

import main.java.com.projectBackEnd.TableEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;

/**
 * User objects are database entities for the table 'Users' defined in this class.
 * Each User has an ID, email, password and name.
 *
 * Inspiration : https://examples.javacodegeeks.com/enterprise-java/hibernate/hibernate-annotations-example/
 */
@Entity
@Table(name = User.TABLENAME)
public class User implements TableEntity<User> {

	// Table columns (attributes)
	final static String TABLENAME = "Users";
	private final static String KEY = "id";
	private final static String EMAIL = "email";
	private final static String PASSWORD = "password";
	private final static String NAME = "name";

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = KEY)
   	private int primaryKey;

	@Column(name = EMAIL)
	@Type(type="text")
	private String email;

	@Column(name = PASSWORD)
	@Type(type="text")
	private String password;

	@Column(name = NAME)
	@Type(type="text")
	private String name;


	/**
	 * Main constructor
	 * @param email		The email of the user
	 * @param password	The password of the user
	 * @param name		The name of the user
	 */
	public User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
	}

	/**
	 * Empty constructor
	 */
	public User(){}

	/**
	 * Get the ID of the user
	 * @return ID primary key
	 */
	public Serializable getPrimaryKey() { return primaryKey; }

	/**
	 * Get the email of the user
	 * @return Email of the user
	 */
	public String getEmail(){
		return email;
	}

	/**
	 * Get the password of the user
	 * @return Password of the user
	 */
	public String getPassword(){
		return password;
	}

	/**
	 * Set the email of the user
	 * @param email The new email
	 */
	public void setEmail(String email){
		this.email = email;
	}

	/**
	 * Set the password of the user
	 * @param password The new password
	 */
	public void setPassword(String password){
		this.password = password;
	}

	/**
	 * Get the name of the user
	 * @return Name of user
	 */
	public String getName(){
		return name;
	}

	/**
	 * Set the name of the user
	 * @param name The new name of the user
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * Produce a string depicting this object
	 * @return the generated string
	 */
	@Override
	public String toString() {
		return "User: " + this.primaryKey + ", " + this.email + ", " + this.password + "," + this.name;
	}

	/**
	 * Copy the values of the input TableEntity object
	 * @param userToCopy    User object to copy
	 * @return this, updated User object
	 */
  	@Override
    public User copy(User userToCopy) {
        setEmail(userToCopy.getEmail());
        setPassword(userToCopy.getPassword());
        setName(userToCopy.getName());
      	return this;
    }

}
