package main.java.com.projectBackEnd.Services.Session;

/**
 * This class models a custom exception thrown by the application when there is a session expected but none is present
 */
public class NoSessionException  extends Exception {

    /**
     * Class constructor
     * @param message The message to be sent back to the client
     */
    NoSessionException(String message) {
        super(message);
    }
}