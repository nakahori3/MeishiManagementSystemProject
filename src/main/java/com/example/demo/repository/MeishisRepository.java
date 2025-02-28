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
    @Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomotePath, photouraPath, savedate) VALUES (:companyname, :companykananame, pgp_sym_encrypt(:personalname, :pgpassword), pgp_sym_encrypt(:personalkananame, :pgpassword), :belong, :position, :address, :companytel, pgp_sym_encrypt(:mobiletel, :pgpassword), pgp_sym_encrypt(:email, :pgpassword), pgp_sym_encrypt(:photoomotePath, :pgpassword), pgp_sym_encrypt(:photouraPath, :pgpassword), :savedate)", nativeQuery = true)
	/*@Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomotePath, photouraPath, savedate) VALUES (:companyname, :companykananame, pgp_sym_encrypt(:personalname, :pgpassword), pgp_sym_encrypt(:personalkananame, :pgpassword), :belong, :position, :address, :companytel, pgp_sym_encrypt(:mobiletel, :pgpassword), pgp_sym_encrypt(:email, :pgpassword), pgp_sym_encrypt(:photoomotePath, :pgpassword), pgp_sym_encrypt(:photouraPath, :pgpassword), :savedate)", nativeQuery = true)*/
    void saveMeishi(
        @Param("companyname") String companyname,
        @Param("companykananame") String companykananame,
        @Param("personalname") String personalname,
        @Param("personalkananame") String personalkananame,
        @Param("belong") String belong,
        @Param("position") String position,
        @Param("address") String address,
        @Param("companytel") String companytel,
        @Param("mobiletel") String mobiletel,
        @Param("email") String email,
        @Param("photoomotePath") String photoomotePath,
        @Param("photouraPath") String photouraPath,
        @Param("savedate") String savedate,
        @Param("pgpassword") String pgpassword
    );
    
    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) ELSE NULL END AS photouraPath, " +
    	       "savedate FROM meishis", nativeQuery = true)
    
	/*@Query(value = "SELECT id, companyname, companykananame, " +
		       "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		       "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		       "belong, position, address, companytel, " +
		       "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		       "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		       "convert_from(pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::bytea, 'UTF8') AS photoomotePath, " +
		       "CASE WHEN photouraPath IS NOT NULL THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') ELSE NULL END AS photouraPath, " +
		       "savedate FROM meishis", nativeQuery = true)*/
    List<MeishiEntity> findAllDecrypted(@Param("pgpassword") String pgpassword);

    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE " +
    	           "WHEN photouraPath IS NOT NULL " +
    	           "THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) " +
    	           "ELSE NULL " +
    	       "END AS photouraPath, " +
    	       "savedate FROM meishis WHERE id = :id", nativeQuery = true)
    
	/* @Query(value = "SELECT id, companyname, companykananame, " +
		    "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		    "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		    "belong, position, address, companytel, " +
		    "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		    "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		    "CASE " +
		        "WHEN photouraPath IS NOT NULL " +
		        "THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') " +
		        "ELSE NULL " +
		    "END AS photouraPath, " +
		    "savedate FROM meishis WHERE id = :id", nativeQuery = true)*/
    MeishiEntity findDecryptedById(@Param("id") int id, @Param("pgpassword") String pgpassword);

    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE " +
    	           "WHEN photouraPath IS NOT NULL " +
    	           "THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) " +
    	           "ELSE NULL " +
    	       "END AS photouraPath, " +
    	       "savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    
	/*@Query(value = "SELECT id, companyname, companykananame, " +
		    "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		    "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		    "belong, position, address, companytel, " +
		    "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		    "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		    "CASE " +
		        "WHEN photouraPath IS NOT NULL " +
		        "THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') " +
		        "ELSE NULL " +
		    "END AS photouraPath, " +
		    "savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)*/
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame, @Param("pgpassword") String pgpassword);

    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE " +
    	           "WHEN photouraPath IS NOT NULL " +
    	           "THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) " +
    	           "ELSE NULL " +
    	       "END AS photouraPath, " +
    	       "savedate FROM meishis WHERE personalkananame LIKE %:personalkananame%", nativeQuery = true)
    
	/*@Query(value = "SELECT id, companyname, companykananame, " +
		    "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		    "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		    "belong, position, address, companytel, " +
		    "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		    "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		    "CASE " +
		        "WHEN photouraPath IS NOT NULL " +
		        "THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') " +
		        "ELSE NULL " +
		    "END AS photouraPath, " +
		    "savedate FROM meishis WHERE personalkananame LIKE %:personalkananame%", nativeQuery = true)*/
    List<MeishiEntity> findByPertialPersonalKanaName(@Param("personalkananame") String personalkananame, @Param("pgpassword") String pgpassword);

    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE " +
    	           "WHEN photouraPath IS NOT NULL " +
    	           "THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) " +
    	           "ELSE NULL " +
    	       "END AS photouraPath, " +
    	       "savedate FROM meishis WHERE companykananame LIKE %:companykananame%", nativeQuery = true)
    
	/*@Query(value = "SELECT id, companyname, companykananame, " +
		    "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		    "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		    "belong, position, address, companytel, " +
		    "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		    "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		    "CASE " +
		        "WHEN photouraPath IS NOT NULL " +
		        "THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') " +
		        "ELSE NULL " +
		    "END AS photouraPath, " +
		    "savedate FROM meishis WHERE companykananame LIKE %:companykananame%", nativeQuery = true)*/
    List<MeishiEntity> findByPertialCompanyKanaName(@Param("companykananame") String companykananame, @Param("pgpassword") String pgpassword);
    
    
    
    @Query(value = "SELECT id, companyname, companykananame, " +
    	       "pgp_sym_decrypt(personalname::bytea, :pgpassword) AS personalname, " +
    	       "pgp_sym_decrypt(personalkananame::bytea, :pgpassword) AS personalkananame, " +
    	       "belong, position, address, companytel, " +
    	       "pgp_sym_decrypt(mobiletel::bytea, :pgpassword) AS mobiletel, " +
    	       "pgp_sym_decrypt(email::bytea, :pgpassword) AS email, " +
    	       "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword) AS photoomotePath, " +
    	       "CASE " +
    	           "WHEN photouraPath IS NOT NULL " +
    	           "THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword) " +
    	           "ELSE NULL " +
    	       "END AS photouraPath, " +
    	       "savedate FROM meishis WHERE personalkananame = :personalkananame", nativeQuery = true)
    
	/* @Query(value = "SELECT id, companyname, companykananame, " +
		    "convert_from(pgp_sym_decrypt(personalname::bytea, :pgpassword)::bytea, 'UTF8') AS personalname, " +
		    "convert_from(pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::bytea, 'UTF8') AS personalkananame, " +
		    "belong, position, address, companytel, " +
		    "convert_from(pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::bytea, 'UTF8') AS mobiletel, " +
		    "convert_from(pgp_sym_decrypt(email::bytea, :pgpassword)::bytea, 'UTF8') AS email, " +
		    "CASE " +
		        "WHEN photouraPath IS NOT NULL " +
		        "THEN convert_from(pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::bytea, 'UTF8') " +
		        "ELSE NULL " +
		    "END AS photouraPath, " +
		    "savedate FROM meishis WHERE personalkananame = :personalkananame", nativeQuery = true)*/
    List<MeishiEntity> findByPersonalkananame(@Param("personalkananame") String personalkananame, @Param("pgpassword") String pgpassword);
}



/*public interface MeishisRepository extends JpaRepository<MeishiEntity, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomotePath, photouraPath, savedate) VALUES (:companyname, :companykananame, :personalname, :personalkananame, :belong, :position, :address, :companytel, :mobiletel, :email, :photoomotePath, :photouraPath, :savedate)", nativeQuery = true)
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
        @Param("photoomotePath") byte[] photoomotePath,
        @Param("photouraPath") byte[] photouraPath,
        @Param("savedate") String savedate
    );

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomotePath::bytea, get_passwd()), 'UTF8') AS photoomotePath, convert_from(pgp_sym_decrypt(photouraPath::bytea, get_passwd()), 'UTF8') AS photouraPath, savedate FROM meishis", nativeQuery = true)
    List<MeishiEntity> findAllDecrypted();

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomotePath::bytea, get_passwd()), 'UTF8') AS photoomotePath, convert_from(pgp_sym_decrypt(photouraPath::bytea, get_passwd()), 'UTF8') AS photouraPath, savedate FROM meishis WHERE id = :id", nativeQuery = true)
    MeishiEntity findDecryptedById(@Param("id") int id);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomotePath::bytea, get_passwd()), 'UTF8') AS photoomotePath, convert_from(pgp_sym_decrypt(photouraPath::bytea, get_passwd()), 'UTF8') AS photouraPath, savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomotePath::bytea, get_passwd()), 'UTF8') AS photoomotePath, convert_from(pgp_sym_decrypt(photouraPath::bytea, get_passwd()), 'UTF8') AS photouraPath, savedate FROM meishis WHERE personalkananame LIKE %:personalkananame%", nativeQuery = true)
    List<MeishiEntity> findByPertialPersonalKanaName(@Param("personalkananame") String personalkananame);

    @Query(value = "SELECT id, companyname, companykananame, convert_from(pgp_sym_decrypt(personalname::bytea, get_passwd()), 'UTF8') AS personalname, convert_from(pgp_sym_decrypt(personalkananame::bytea, get_passwd()), 'UTF8') AS personalkananame, belong, position, address, companytel, convert_from(pgp_sym_decrypt(mobiletel::bytea, get_passwd()), 'UTF8') AS mobiletel, convert_from(pgp_sym_decrypt(email::bytea, get_passwd()), 'UTF8') AS email, convert_from(pgp_sym_decrypt(photoomotePath::bytea, get_passwd()), 'UTF8') AS photoomotePath, convert_from(pgp_sym_decrypt(photouraPath::bytea, get_passwd()), 'UTF8') AS photouraPath, savedate FROM meishis WHERE companykananame LIKE %:companykananame%", nativeQuery = true)
    List<MeishiEntity> findByPertialCompanyKanaName(@Param("companykananame") String companykananame);

    List<MeishiEntity> findByPersonalkananame(String personalkananame);
}*/

