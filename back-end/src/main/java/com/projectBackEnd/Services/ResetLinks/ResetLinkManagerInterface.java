package main.java.com.projectBackEnd.Services.ResetLinks;


/**
 * The interface for all ResetLinkManagers
 */
public interface ResetLinkManagerInterface{
	void delete(String token);
	String create(String email) throws EmailNotExistException;
	String getEmail(String token);
	boolean exist(String token);
}
