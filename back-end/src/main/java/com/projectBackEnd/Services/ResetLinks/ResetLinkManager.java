package main.java.com.projectBackEnd.Services.ResetLinks;
import main.java.com.projectBackEnd.EntityManager;
import main.java.com.projectBackEnd.HibernateUtility;
import main.java.com.projectBackEnd.Services.User.Hibernate.UserManager;
import java.util.List;

/**
 *  ResetLinkManager is managed to reset, delete and retrieve the data from all Link(s)
 *  It mainly responsible for resetting an existing Link.
 *  This class implements ResetLinkManagerInterface and extends EntityManager
 */
public class ResetLinkManager extends EntityManager implements ResetLinkManagerInterface{
	private static ResetLinkManagerInterface resetLinkManager;

	/**
	 * Private Constructor of ResetLinkManager, implementing the singleton design pattern
	 */	
	private ResetLinkManager(){
		super();
		setSubclass(Link.class);
		HibernateUtility.addAnnotation(Link.class);
		resetLinkManager = this;
	}
	

	/**
	 * Get the ResetLinkManagerInterface
	 * @return ResetLinkManagerInterface
	 */
	public static ResetLinkManagerInterface getResetLinkManager(){
		if(resetLinkManager != null)
			return resetLinkManager;
		
		else
			return new ResetLinkManager();
	}

	/**
	 * This method will return the new token generated for link being reset.
	 * @param email user's email
	 * @return new Token for authorization
	 * @throws EmailNotExistException When the Email doesn't exist
	 */
	public String create(String email) throws EmailNotExistException{
		if(UserManager.getUserManager().verifyEmail(email)){
			Link reset = new Link(email);
			insertTuple(reset);
			return reset.getToken();
		}
		else
			throw new EmailNotExistException("there is no user with email: " + email);
	}
	
	/**
	 * Find the corresponding email according to the token input from all links.
	 * @param token The corresponding token
	 * @return email matched
	 */
	public String getEmail(String token){
		List<Link> links = getAll();
		for(Link l: links){
			if(l.getToken().equals(token)){
				return l.getEmail();
			}
		}
		return null;
	}
	
	/**
	 * Delete a specific Link
	 * @param token the token of link wanted to be deleted
	 */
	public void delete(String token){
		List<Link> links = getAll();
		for(Link l: links){
			if(l.getToken().equals(token)){
				delete(l);
				break;
			}
		}

	}
	
	/**
	 * Check if a Link exists
	 * @param token the token of link wanted to be checked
	 * @return boolean result
	 */
	public boolean exist(String token){
		List<Link> links = getAll();
		for(Link l: links){
			if(l.getToken().equals(token)){
				return true;
			}
		}
		return false;
	}

}
