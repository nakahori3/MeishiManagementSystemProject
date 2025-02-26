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
	
	@Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote::bytea, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura::bytea, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis", nativeQuery = true)
    List<MeishiEntity> findAllDecrypted();

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote::bytea, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura::bytea, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE id = :id", nativeQuery = true)
    MeishiEntity findDecryptedById(@Param("id") int id);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote::bytea, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura::bytea, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote::bytea, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura::bytea, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE personalkananame LIKE %:personalkananame%", nativeQuery = true)
    List<MeishiEntity> findByPertialPersonalKanaName(@Param("personalkananame") String personalkananame);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomote::bytea, get_passwd()), 'UTF8') AS photoomote, convert_from(pgp_sym_decrypt(photoura::bytea, get_passwd()), 'UTF8') AS photoura, savedate FROM meishis WHERE companykananame LIKE %:companykananame%", nativeQuery = true)
    List<MeishiEntity> findByPertialCompanyKanaName(@Param("companykananame") String companykananame);
    
    List<MeishiEntity> findByPersonalkananame(String personalkananame);
}
    
	