package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.MeishiEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.form.MeishiForm;
import com.example.demo.repository.MeishisRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.util.PGPUtil;

@Service
public class MeishiService {
	
	private static final String ENCRYPTION_KEY = "P4ssW0rd";

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    MeishisRepository meishisRepository;

    // すべての名刺Entityを取得
    public List<MeishiEntity> getAllDecryptedMeishis() {
        return meishisRepository.findAllDecrypted();
    }

    // 名刺IDから名刺Entityを取得
    public MeishiEntity getDecryptedMeishiById(int id) {
        return meishisRepository.findDecryptedById(id);
    }

    // UserEntityオブジェクトのリストを作成するメソッド
    public List<UserEntity> makeUsersList() {
        List<UserEntity> usersList = new ArrayList<>();
        usersRepository.findAll().forEach(usersList::add);
        return usersList;
    }

    // 企業名（カナ）より名刺Entityを取得
    public List<MeishiEntity> findByCompanykananame(String companykananame) {
        return meishisRepository.findByCompanykananame(companykananame);
    }

    // 担当者名（カナ）より名刺Entityを取得
    public List<MeishiEntity> findByPersonalkananame(String personalkananame) {
        return meishisRepository.findByPertialPersonalKanaName(personalkananame);
    }

    // リポジトリのsaveMeishiを呼び出してデータを保存
    public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
        // 文字列データをバイナリ形式に変換
        byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
        byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
        byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
        byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
        byte[] photoomote = photoomotePath.getBytes(StandardCharsets.UTF_8);
        byte[] photoura = photouraPath.getBytes(StandardCharsets.UTF_8);

        // 暗号化処理
        byte[] encryptedPersonalName;
        byte[] encryptedPersonalKanaName;
        byte[] encryptedMobileTel;
        byte[] encryptedEmail;
        byte[] encryptedPhotoOmote;
        byte[] encryptedPhotoUra;
        try {
            encryptedPersonalName = PGPUtil.encrypt(personalname, ENCRYPTION_KEY);
            encryptedPersonalKanaName = PGPUtil.encrypt(personalkananame, ENCRYPTION_KEY);
            encryptedMobileTel = PGPUtil.encrypt(mobiletel, ENCRYPTION_KEY);
            encryptedEmail = PGPUtil.encrypt(email, ENCRYPTION_KEY);
            encryptedPhotoOmote = PGPUtil.encrypt(photoomote, ENCRYPTION_KEY);
            encryptedPhotoUra = PGPUtil.encrypt(photoura, ENCRYPTION_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

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
            encryptedPhotoOmote,
            encryptedPhotoUra
        );
    }
    
    
    
	/*public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
	    // 文字列データをバイナリ形式に変換
	    byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
	    byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
	    byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
	    byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
	    byte[] photoomote = photoomotePath.getBytes(StandardCharsets.UTF_8);
	    byte[] photoura = photouraPath.getBytes(StandardCharsets.UTF_8);
	
	    // 暗号化処理（例）
	    byte[] encryptedPersonalName = encryptData(personalname);
	    byte[] encryptedPersonalKanaName = encryptData(personalkananame);
	    byte[] encryptedMobileTel = encryptData(mobiletel);
	    byte[] encryptedEmail = encryptData(email);
	    byte[] encryptedPhotoOmote = encryptData(photoomote);
	    byte[] encryptedPhotoUra = encryptData(photoura);
	
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
	        encryptedPhotoOmote,
	        encryptedPhotoUra
	    );
	}*/
    
    
    private byte[] encryptData(byte[] data) {
        try {
            // 暗号化処理
            return PGPUtil.encrypt(data, ENCRYPTION_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] decryptData(byte[] data) {
        try {
            // 復号化処理
            return PGPUtil.decrypt(data, ENCRYPTION_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

	/*private byte[] encryptData(byte[] data) {
	    // データ暗号化処理を実装
	    // ここでは簡単なサンプルとして、データそのまま返します
	    return data;
	}*/

    // 企業名（カナ）で完全一致検索
    public List<MeishiEntity> searchMeishiByCompanyKananame(String keyword) {
        return meishisRepository.findByCompanykananame(keyword);
    }

    // 企業名（カナ）で部分一致検索
    public List<MeishiEntity> findByPertialCompanyKanaName(String companykananame) {
        return meishisRepository.findByPertialCompanyKanaName(companykananame);
    }

    // 担当者名（カナ）で部分一致検索
    public List<MeishiEntity> findByPertialPersonalKanaName(String personalkananame) {
        return meishisRepository.findByPertialPersonalKanaName(personalkananame);
    }
}








/*package com.example.demo.service;

import java.nio.charset.StandardCharsets;
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
	
	
	//すべての名刺Entityを取得
	public List<MeishiEntity> getAllDecryptedMeishis() {
        return meishisRepository.findAllDecrypted();
    }
	
	//名刺IDから名刺Entityを取得
	public MeishiEntity getDecryptedMeishiById(int id) {
        return meishisRepository.findDecryptedById(id);
    }
	
	
	//UserEntityオブジェクトのリストを作成するメソッド
	public List<UserEntity> makeUsersList() {
        List<UserEntity> usersList = new ArrayList<>();
        usersRepository.findAll().forEach(usersList::add);
        return usersList;
    }
	
	
	//企業名（カナ）より名刺Entityを取得
	public List<MeishiEntity> findByCompanykananame(String companykananame) {
        return meishisRepository.findByCompanykananame(companykananame);
    }
	
	//担当者名（カナ）より名刺Entityを取得
	public List<MeishiEntity> findByPersonalkananame(String personalkananame) {
        return meishisRepository.findByPersonalkananame(personalkananame);
    }
	
	
	//リポジトリのsaveMeishiを呼び出してデータを保存
	
	public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
        // 文字列データをバイナリ形式に変換
        byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
        byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
        byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
        byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
        byte[] photoomote = photoomotePath.getBytes(StandardCharsets.UTF_8);
        byte[] photoura = photouraPath.getBytes(StandardCharsets.UTF_8);

        // 暗号化処理（例）
        byte[] encryptedPersonalName = encryptData(personalname);
        byte[] encryptedPersonalKanaName = encryptData(personalkananame);
        byte[] encryptedMobileTel = encryptData(mobiletel);
        byte[] encryptedEmail = encryptData(email);
        byte[] encryptedPhotoOmote = encryptData(photoomote);
        byte[] encryptedPhotoUra = encryptData(photoura);

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
            encryptedPhotoOmote,
            encryptedPhotoUra
        );
    }

    private byte[] encryptData(byte[] data) {
        // データ暗号化処理を実装
        // ここでは簡単なサンプルとして、データそのまま返します
        return data;
    }



    
  //企業名（カナ）で完全一致検索
    public List<MeishiEntity> searchMeishiByCompanyKananame(String keyword) {
        return meishisRepository.findByExactCompanyKanaName(keyword);
    }
    
    //企業名（カナ）で部分一致検索
    public List<MeishiEntity> findByPertialCompanyKanaName(String companykananame) {
        return meishisRepository.findByPertialCompanyKanaName(companykananame);
    }
	
    //担当者名（カナ）で部分一致検索
    public List<MeishiEntity> findByPertialPersonalKanaName(String personalkananame) {
        return meishisRepository.findByPertialPersonalKanaName(personalkananame);
    }

    
   
	
}
	*/