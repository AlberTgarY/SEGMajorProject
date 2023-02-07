package main.java.com.projectBackEnd;

/**
 * This class models a custom exception thrown during validation when an object attempted to be added violates key constraints.
 */
public class DuplicateKeysException extends Exception {

    /**
     * Class constructor
     * @param message The message to be sent back to the client
     */
    public DuplicateKeysException(String message) {
        super(message);
    }
}