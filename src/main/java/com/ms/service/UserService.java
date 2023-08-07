package com.ms.service;

import java.util.List;

import com.ms.dto.ActivateAccount;
import com.ms.dto.Login;
import com.ms.dto.User;

public interface UserService {

	boolean registerEmployee(User user);

	boolean activateAccount(ActivateAccount ac);

	List<User> getAllUsers();

	User getUserById(Integer id);

	User getUserByEmail(String email);

	boolean deleteUser(Integer id);

	boolean changeAccountStatus(Integer userId , String status);

	String login(Login login);

	String forgotPassword(String email);

}
