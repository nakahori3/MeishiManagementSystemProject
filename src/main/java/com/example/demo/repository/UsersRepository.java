package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.UserEntity;

public interface UsersRepository extends CrudRepository<UserEntity,Integer>{
	
	UserEntity findByUsernameOrderByIdAsc(String username);
	UserEntity findUseridByUsername(String username);
	
	

}