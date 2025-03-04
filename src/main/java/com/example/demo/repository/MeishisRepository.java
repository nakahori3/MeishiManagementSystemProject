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
                   "pgp_sym_decrypt(personalname::bytea, :pgpassword)::text AS personalname, " +
                   "pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text AS personalkananame, " +
                   "belong, position, address, companytel, " +
                   "pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::text AS mobiletel, " +
                   "pgp_sym_decrypt(email::bytea, :pgpassword)::text AS email, " +
                   "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::text AS photoomotePath, " +
                   "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::text ELSE NULL END AS photouraPath, " +
                   "savedate FROM meishis", nativeQuery = true)
    List<MeishiEntity> findAllDecrypted(@Param("pgpassword") String pgpassword);

    @Query(value = "SELECT id, companyname, companykananame, " +
                   "pgp_sym_decrypt(personalname::bytea, :pgpassword)::text AS personalname, " +
                   "pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text AS personalkananame, " +
                   "belong, position, address, companytel, " +
                   "pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::text AS mobiletel, " +
                   "pgp_sym_decrypt(email::bytea, :pgpassword)::text AS email, " +
                   "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::text AS photoomotePath, " +
                   "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::text ELSE NULL END AS photouraPath, " +
                   "savedate FROM meishis WHERE id = :id", nativeQuery = true)
    MeishiEntity findDecryptedById(@Param("id") int id, @Param("pgpassword") String pgpassword);

    @Query(value = "SELECT id, companyname, companykananame, " +
                   "pgp_sym_decrypt(personalname::bytea, :pgpassword)::text AS personalname, " +
                   "pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text AS personalkananame, " +
                   "belong, position, address, companytel, " +
                   "pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::text AS mobiletel, " +
                   "pgp_sym_decrypt(email::bytea, :pgpassword)::text AS email, " +
                   "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::text AS photoomotePath, " +
                   "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::text ELSE NULL END AS photouraPath, " +
                   "savedate FROM meishis WHERE companykananame = :companykananame", nativeQuery = true)
    List<MeishiEntity> findByCompanykananame(@Param("companykananame") String companykananame, @Param("pgpassword") String pgpassword);

    
    @Query(value = "SELECT id, companyname, companykananame, " +
            "pgp_sym_decrypt(personalname::bytea, :pgpassword)::text AS personalname, " +
            "pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text AS personalkananame, " +
            "belong, position, address, companytel, " +
            "pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::text AS mobiletel, " +
            "pgp_sym_decrypt(email::bytea, :pgpassword)::text AS email, " +
            "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::text AS photoomotePath, " +
            "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::text ELSE NULL END AS photouraPath, " +
            "savedate FROM meishis WHERE pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text LIKE CONCAT('%', :personalkananame, '%')", nativeQuery = true)
List<MeishiEntity> findByPartialPersonalKanaName(@Param("personalkananame") String personalkananame, @Param("pgpassword") String pgpassword);

     
    @Query(value = "SELECT id, companyname, companykananame, " +
            "pgp_sym_decrypt(personalname::bytea, :pgpassword)::text AS personalname, " +
            "pgp_sym_decrypt(personalkananame::bytea, :pgpassword)::text AS personalkananame, " +
            "belong, position, address, companytel, " +
            "pgp_sym_decrypt(mobiletel::bytea, :pgpassword)::text AS mobiletel, " +
            "pgp_sym_decrypt(email::bytea, :pgpassword)::text AS email, " +
            "pgp_sym_decrypt(photoomotePath::bytea, :pgpassword)::text AS photoomotePath, " +
            "CASE WHEN photouraPath IS NOT NULL THEN pgp_sym_decrypt(photouraPath::bytea, :pgpassword)::text ELSE NULL END AS photouraPath, " +
            "savedate FROM meishis WHERE companykananame LIKE CONCAT('%', :companykananame, '%')", nativeQuery = true)
List<MeishiEntity> findByPartialCompanyKanaName(@Param("companykananame") String companykananame, @Param("pgpassword") String pgpassword);



}
