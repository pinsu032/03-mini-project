package com.ms.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.entity.UserMaster;

public interface UserRepo extends JpaRepository<UserMaster, Integer> {

	UserMaster findByEmail(String email);
	
	UserMaster findByEmailAndPassword(String email,String password);
}
