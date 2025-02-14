package com.example.demo.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MeishiEntity;

public interface MeishisRepository extends JpaRepository<MeishiEntity, Integer> {

	@Modifying
    @Transactional
    @Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomote, photoura, savedate) VALUES (:companyname, :companykananame, :personalname, :personalkananame, :belong, :position, :address, :companytel, :mobiletel, :email, :photoomote, :photoura, CURRENT_DATE)", nativeQuery = true)
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
        @Param("photoura") byte[] photoura
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



/*package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.MeishiEntity;

public interface MeishisRepository extends JpaRepository<MeishiEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomote, photoura, savedate) VALUES (:companyname, :companykananame, pgp_sym_encrypt(:personalname, get_passwd()), pgp_sym_encrypt(:personalkananame, get_passwd()), :belong, :position, :address, :companytel, pgp_sym_encrypt(:mobiletel, get_passwd()), pgp_sym_encrypt(:email, get_passwd()), pgp_sym_encrypt(:photoomote, get_passwd()), pgp_sym_encrypt(:photoura, get_passwd()), CURRENT_DATE)", nativeQuery = true)
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
        @Param("photoura") byte[] photoura
    );
    
    
  
@Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis", nativeQuery = true)
List<MeishiEntity> findAllDecrypted();

@Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE id = :id", nativeQuery = true)
MeishiEntity findDecryptedById(@Param("id") int id);

@Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame);

@Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE convert_from(pgp_sym_decrypt(personalkananame, get_passwd()), 'UTF8') = :personalkananame", nativeQuery = true)
List<MeishiEntity> findByPersonalkananame(@Param("personalkananame") String personalkananame);

//企業名（カナ）の完全一致
@Query(value = "SELECT * FROM meishis WHERE companykananame = :keyword", nativeQuery = true)
List<MeishiEntity> findByExactCompanyKanaName(@Param("keyword") String keyword);

//企業名（カナ）の部分一致
@Query(value = "SELECT * FROM meishis WHERE companykananame LIKE %:keyword%", nativeQuery = true)
List<MeishiEntity> findByPertialCompanyKanaName(@Param("keyword") String keyword);

//担当者名（カナ）の部分一致
@Query(value = "SELECT * FROM meishis WHERE personalkananame LIKE %:keyword%", nativeQuery = true)
List<MeishiEntity> findByPertialPersonalKanaName(@Param("keyword") String keyword);
}*/



