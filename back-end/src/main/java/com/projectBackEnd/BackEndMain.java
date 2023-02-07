package main.java.com.projectBackEnd;
import io.micronaut.runtime.Micronaut;
import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;

/**
 * Main method : runs the server
 */
public class BackEndMain {

	public static void main(String []args){
		try {
			UserManager.getUserManager().addUser("test1@test.com", "test1", "test1");
		} catch (Exception e) {}
		Micronaut.run(BackEndMain.class);
	}
}