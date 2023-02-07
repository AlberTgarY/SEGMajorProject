package main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions;

/**
 * This class models a custom exception thrown during validation checking when a user enters an invalid password
 */
public class InvalidPasswordException extends Exception {

    /**
     * Class constructor
     * @param message Message to be sent back to the client
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
