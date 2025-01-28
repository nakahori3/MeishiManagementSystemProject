package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.AuthorityEntity;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity,Integer> {

	AuthorityEntity findByUsername(String username);
	AuthorityEntity findByAuthority(String authority);
	AuthorityEntity  findAuthorityById(Integer id);
	Iterable<AuthorityEntity> findAllByOrderByIdAsc();
	
	

}