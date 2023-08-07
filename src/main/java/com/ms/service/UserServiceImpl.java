package com.ms.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.ms.dto.ActivateAccount;
import com.ms.dto.EmailDetails;
import com.ms.dto.Login;
import com.ms.dto.User;
import com.ms.entity.UserMaster;
import com.ms.repo.UserRepo;
import com.ms.utils.EmailUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo repo;

	@Autowired
    private EmailUtil util;
	
	@Override
	public boolean registerEmployee(User user) {
		UserMaster userMaster = new UserMaster();

		BeanUtils.copyProperties(user, userMaster);
		userMaster.setAccStatus("In_Active");
		// generating random password
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
		String pwd = RandomStringUtils.random(15, characters);

		userMaster.setPassword(pwd);
		UserMaster save = repo.save(userMaster);
		
		//send mail;
		EmailDetails details = new EmailDetails();
		details.setRecipient(userMaster.getEmail());
		details.setSubject("Unlock Account");
		String fileName = "Reg_Mail_Body.txt";
		details.setMsgBody(readEmailBody(userMaster.getFullName(), userMaster.getPassword(), fileName));

		boolean status = util.sendMail(details);
		if(status)
			System.out.println("mail sent");
		else
			System.out.println("mail not sent");
		
		return save.getUserId() != null;
	}

	

	@Override
	public boolean activateAccount(ActivateAccount ac) {
		UserMaster um = new UserMaster();
		um.setEmail(ac.getEmail());
		um.setPassword(ac.getTempPwd());

		Example<UserMaster> of = Example.of(um);
		List<UserMaster> findAll = repo.findAll();
		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setAccStatus("Active");
			repo.save(userMaster);
			return true;
		}

	}

	@Override
	public List<User> getAllUsers() {
		List<UserMaster> findAll = repo.findAll();

		List<User> userList = new ArrayList<>();
		findAll.forEach(entity -> {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			userList.add(user);
		});
		return userList;
	}

	@Override
	public User getUserById(Integer id) {
		Optional<UserMaster> findById = repo.findById(id);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			User user = new User();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		} else {
			return null;
		}
	}

	@Override
	public User getUserByEmail(String email) {
		UserMaster findByEmail = repo.findByEmail(email);
		User user = new User();
		BeanUtils.copyProperties(findByEmail, user);
		return user;
	}

	@Override
	public boolean deleteUser(Integer id) {
		try {
			repo.deleteById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String status) {
		Optional<UserMaster> findById = repo.findById(userId);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(status);
			repo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {
		UserMaster findByEmailAndPassword = repo.findByEmailAndPassword(login.getEmail(), login.getPassword());
		String msg = " ";
		if (findByEmailAndPassword != null) {
			if (findByEmailAndPassword.getAccStatus().equals("Active")) {
				msg = "login success";
			} else {
				msg = "User not enabled";
			}
		} else {
			msg = "Invalid credentials";
		}

		return msg;
	}

	@Override
	public String forgotPassword(String email) {
		UserMaster entity = repo.findByEmail(email);
		if (entity == null) {
			return "Invalid email";
		}
		
		EmailDetails details = new EmailDetails();
		details.setRecipient(email);
		details.setSubject("Forgot Password");
		String fileName = "Recover_pwd_email_body.txt";
		details.setMsgBody(readEmailBody(entity.getFullName(), entity.getPassword(), fileName));
		
		boolean sendMail = util.sendMail(details);
		
		if(sendMail)
			return "Password sent to your registered email.";
		
		return null;
	}
	
	private String readEmailBody(String name,String pwd,String fileName) {
		String url = " ";
		String body = null;
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);
			StringBuffer buffer = new StringBuffer();
			String line = br.readLine();
			
			while(line != null) {
				buffer.append(line);
				line = br.readLine();
			}
			br.close();
			body = buffer.toString();
			body = body.replace("{fullName}", name);
			body = body.replace("{tempPwd}", pwd);
			body = body.replace("{url}", url);
			body = body.replace("{pwd}", pwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}

}
