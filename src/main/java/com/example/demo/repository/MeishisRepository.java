package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.MeishiEntity;

public interface MeishisRepository extends JpaRepository<MeishiEntity, Integer>{
	
	@Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE id = :id", nativeQuery = true)
    MeishiEntity findDecryptedById(@Param("id") Long id);

    @Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis", nativeQuery = true)
    List<MeishiEntity> findAllDecrypted();
    
    @Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame);

    @Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE pgp_sym_decrypt(personalkananame, get_passwd()) = :personalkananame", nativeQuery = true)
    List<MeishiEntity> findByPersonalkananame(@Param("personalkananame") String personalkananame);
    

}
