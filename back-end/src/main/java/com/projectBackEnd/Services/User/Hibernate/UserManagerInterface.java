package main.java.com.projectBackEnd.Services.User.Hibernate;

import main.java.com.projectBackEnd.Services.User.Hibernate.Exceptions.*;

import java.util.List;

/**
 *  Methods used by UserManager for database queries.
 */
public interface UserManagerInterface {

	void addUser(String email,String password, String name)
			throws EmailExistsException, InvalidEmailException, IncorrectNameException, InvalidPasswordException;

	String verifyUser(String email,String password);

	boolean verifyEmail(String email);

	void changePassword(String email, String newPassword)
			throws UserNotExistException,InvalidPasswordException;

	void changeEmail(String oldEmail, String newEmail)
			throws UserNotExistException,EmailExistsException;

	void changeName(String email, String name)
			throws UserNotExistException, IncorrectNameException;

	String getName(String email)
			throws UserNotExistException;

	List<User> getUsers();

	void deleteUser(String email, String password)
			throws UserNotExistException;

}

