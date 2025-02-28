package com.example.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MeishiEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.form.MeishiForm;
import com.example.demo.repository.MeishisRepository;
import com.example.demo.repository.UsersRepository;


@Service
public class MeishiService {
	

	@Autowired
	UsersRepository usersRepository;
	
	@Autowired
	MeishisRepository meishisRepository;
	
	
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/meishisDB";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    private static final String PGPASSWORD = "P4ssW0rd";
	
	
	//すべての名刺Entityを取得
	public List<MeishiEntity> getAllDecryptedMeishis() {
		String pgpassword = "P4ssW0rd"; // ここでパスワードを指定
	    return meishisRepository.findAllDecrypted(pgpassword);
    }
	
	//名刺IDから名刺Entityを取得
	public MeishiEntity getDecryptedMeishiById(int id) throws Exception {
        System.out.println("Getting decrypted Meishi by ID: " + id);

        String pgpassword = PGPASSWORD; // パスワードを指定
        System.out.println("pgpassword: " + pgpassword);
        
        MeishiEntity meishi = meishisRepository.findDecryptedById(id, pgpassword);
        System.out.println("Decrypted Meishi: " + meishi);
        System.out.println("photoomotePath: " + meishi.getPhotoomotePath()); // 追加: photoomotePathを出力
        
        return meishi;
    }


	
	/*public MeishiEntity getDecryptedMeishiById(int id) {
	    return meishisRepository.findDecryptedById(id);
	}*/
	
	
	//UserEntityオブジェクトのリストを作成するメソッド
	public List<UserEntity> makeUsersList() {
        List<UserEntity> usersList = new ArrayList<>();
        usersRepository.findAll().forEach(usersList::add);
        return usersList;
    }
	
	
	// 会社のかな名から名刺Entityのリストを取得
    public List<MeishiEntity> getDecryptedMeishiByCompanykananame(String companykananame) throws Exception {
        System.out.println("Searching by companykananame: " + companykananame);

        String pgpassword = PGPASSWORD; // パスワードを指定
        System.out.println("pgpassword: " + pgpassword);
        
        List<MeishiEntity> meishiList = meishisRepository.findByCompanykananame(companykananame, pgpassword);
        for (MeishiEntity meishi : meishiList) {
            System.out.println("Decrypted Meishi: " + meishi);
            System.out.println("photoomotePath: " + meishi.getPhotoomotePath()); // 追加: photoomotePathを出力
        }
        return meishiList;
    }

	
		
	
	public List<MeishiEntity> findByCompanykananame(String companykananame) {
		System.out.println("Searching by companykananame: " + companykananame);
	    String pgpassword = "P4ssW0rd"; // パスワードを指定
	    return meishisRepository.findByCompanykananame(companykananame, pgpassword);
	}

	
	
	/*//企業名（カナ）より名刺Entityを取得
	public List<MeishiEntity> findByCompanykananame(String companykananame) {
	    return meishisRepository.findByCompanykananame(companykananame);
	}*/
	
	/*2月28日一旦、企業名（カナ）での検索優先にて、コメントアウト
	 * 
	 * //担当者名（カナ）より名刺Entityを取得
	public List<MeishiEntity> findByPersonalkananame(String personalkananame) {
		System.out.println("Searching by personalkananame: " + personalkananame);
	    String pgpassword = "P4ssW0rd"; // パスワードを指定
	    return meishisRepository.findByPersonalkananame(personalkananame, pgpassword);
	}*/
	
	
	 // リポジトリのsaveMeishiを呼び出してデータを保存
		
	public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) throws Exception {
        // 画像ファイルパスがNULLでないことを確認
        if (photoomotePath == null) {
            throw new IllegalArgumentException("photoomotePath がNULLです。");
        }

        // photouraPath が NULL でも問題ない場合、エラーチェックは不要
        if (photouraPath == null) {
            System.out.println("photouraPath is NULL");
        }

        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String sql = "INSERT INTO meishis (companyname, companykananame, personalname, personalkananame, belong, position, address, companytel, mobiletel, email, photoomotePath, photouraPath, savedate) "
                + "VALUES (?, ?, pgp_sym_encrypt(?, ?), pgp_sym_encrypt(?, ?), ?, ?, ?, ?, pgp_sym_encrypt(?, ?), pgp_sym_encrypt(?, ?), pgp_sym_encrypt(?, ?), pgp_sym_encrypt(?, ?), ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, form.getCompanyname());
        pstmt.setString(2, form.getCompanykananame());
        pstmt.setString(3, form.getPersonalname());
        pstmt.setString(4, PGPASSWORD);
        pstmt.setString(5, form.getPersonalkananame());
        pstmt.setString(6, PGPASSWORD);
        pstmt.setString(7, form.getBelong());
        pstmt.setString(8, form.getPosition());
        pstmt.setString(9, form.getAddress());
        pstmt.setString(10, form.getCompanytel());
        pstmt.setString(11, form.getMobiletel());
        pstmt.setString(12, PGPASSWORD);
        pstmt.setString(13, form.getEmail());
        pstmt.setString(14, PGPASSWORD);
        pstmt.setString(15, photoomotePath);
        pstmt.setString(16, PGPASSWORD);
        pstmt.setString(17, photouraPath);
        pstmt.setString(18, PGPASSWORD);
        pstmt.setString(19, form.getSavedate());
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    	
	
	/*public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
			
			    // 画像ファイルパスがNULLでないことを確認
			    if (photoomotePath == null) {
			        System.out.println("photoomotePath is NULL");
			        throw new IllegalArgumentException("photoomotePath がNULLです。");
			    }
			
			    // photouraPath が NULL でも問題ない場合、エラーチェックは不要
			    if (photouraPath == null) {
			        System.out.println("photouraPath is NULL");
			    }
			
			    // 文字列データをバイナリ形式に変換
			    byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
			    byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
			    byte[] mobiletel = form.getMobiletel() != null ? form.getMobiletel().getBytes(StandardCharsets.UTF_8) : null;
			    byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
			    byte[] photoOmoteData = photoomotePath.getBytes(StandardCharsets.UTF_8);
			    byte[] photoUraData = photouraPath != null ? photouraPath.getBytes(StandardCharsets.UTF_8) : null;
			
			    // 暗号化処理
			    byte[] encryptedPersonalName = encryptData(personalname);
			    byte[] encryptedPersonalKanaName = encryptData(personalkananame);
			    byte[] encryptedMobileTel = mobiletel != null ? encryptData(mobiletel) : null;
			    byte[] encryptedEmail = encryptData(email);
			    byte[] encryptedPhotoOmotePath = encryptData(photoOmoteData);
			    byte[] encryptedPhotoUraPath = photoUraData != null ? encryptData(photoUraData) : null;
			
			    meishisRepository.saveMeishi(
			            form.getCompanyname(),
			            form.getCompanykananame(),
			            encryptedPersonalName,
			            encryptedPersonalKanaName,
			            form.getBelong(),
			            form.getPosition(),
			            form.getAddress(),
			            form.getCompanytel(),
			            encryptedMobileTel,
			            encryptedEmail,
			            encryptedPhotoOmotePath,
			            encryptedPhotoUraPath,
			            form.getSavedate() // savedate 追加
			    );
			}*/



    private byte[] encryptData(byte[] data) {
        // データ暗号化処理を実装
        // ここでは簡単なサンプルとして、データそのまま返します
        return data;
    }

	public List<MeishiEntity> findByPertialCompanyKanaName(String keyword) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public List<MeishiEntity> findByPertialPersonalKanaName(String keyword) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}