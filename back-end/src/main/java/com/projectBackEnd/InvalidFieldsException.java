package main.java.com.projectBackEnd;

/**
 * This class models a custom exception thrown during validation when an object attempted to be added has invalid fields
 */
public class InvalidFieldsException extends Exception {

    /**
     * Class constructor
     * @param message The message to be sent back to the client
     */
    public InvalidFieldsException(String message) {
        super(message);
    }
}