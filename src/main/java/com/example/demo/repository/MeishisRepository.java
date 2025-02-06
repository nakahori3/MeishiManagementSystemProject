package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MeishiEntity;

public interface MeishisRepository extends JpaRepository<MeishiEntity, Integer>{
	
	@Modifying
    @Transactional
    @Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomote, photoura, savedate) VALUES (:companyname, :companykananame, pgp_sym_encrypt(:personalname, get_passwd()), pgp_sym_encrypt(:personalkananame, get_passwd()), :belong, :position, :address, :companytel, pgp_sym_encrypt(:mobiletel, get_passwd()), pgp_sym_encrypt(:email, get_passwd()), pgp_sym_encrypt(:photoomote, get_passwd()), pgp_sym_encrypt(:photoura, get_passwd()), :savedate)", nativeQuery = true)
    void saveMeishi(
        @Param("companyname") String companyname,
        @Param("companykananame") String companykananame,
        @Param("personalname") byte[] personalname,
        @Param("personalkananame") byte[] personalkananame,
        @Param("belong") String belong,
        @Param("position") String position,
        @Param("address") String address,
        @Param("companytel") String companytel,
        @Param("mobiletel") byte[] mobiletel,
        @Param("email") byte[] email,
        @Param("photoomote") byte[] photoomote,
        @Param("photoura") byte[] photoura,
        @Param("savedate") String savedate
    );
	
	
	@Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis", nativeQuery = true)
    List<MeishiEntity> findAllDecrypted();
	
	@Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE id = :id", nativeQuery = true)
    MeishiEntity findDecryptedById(@Param("id") int id);
   
    @Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame);

    @Query(value = "SELECT id, companyname, companykananame, pgp_sym_decrypt(personalname, get_passwd()) AS personalname, pgp_sym_decrypt(personalkananame, get_passwd()) AS personalkananame, belong, position, address, companytel, pgp_sym_decrypt(mobiletel, get_passwd()) AS mobiletel, pgp_sym_decrypt(email, get_passwd()) AS email, pgp_sym_decrypt(photoomote, get_passwd()) AS photoomote, pgp_sym_decrypt(photoura, get_passwd()) AS photoura, savedate FROM meishis WHERE pgp_sym_decrypt(personalkananame, get_passwd()) = :personalkananame", nativeQuery = true)
    List<MeishiEntity> findByPersonalkananame(@Param("personalkananame") String personalkananame);
    

}
