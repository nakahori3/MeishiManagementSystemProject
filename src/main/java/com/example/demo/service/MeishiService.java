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
	
	
	 // リポジトリのsaveMeishiを呼び出してデータを保存
	
	public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {

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
	}


	
	
	
	
	
		/*public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
		
		    // 画像ファイルパスがNULLでないことを確認
		    if (photoomotePath == null || photouraPath == null) {
		        throw new IllegalArgumentException("画像ファイルパスがNULLです。");
		    }
		
		    // 文字列データをバイナリ形式に変換
		    byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
		    byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
		    byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
		    byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
		
		    // 画像ファイルパスをバイナリ形式に変換
		    byte[] photoOmoteData = photoomotePath.getBytes(StandardCharsets.UTF_8);
		    byte[] photoUraData = photouraPath.getBytes(StandardCharsets.UTF_8);
		
		    // 暗号化処理
		    byte[] encryptedPersonalName = encryptData(personalname);
		    byte[] encryptedPersonalKanaName = encryptData(personalkananame);
		    byte[] encryptedMobileTel = encryptData(mobiletel);
		    byte[] encryptedEmail = encryptData(email);
		    byte[] encryptedPhotoOmotePath = encryptData(photoOmoteData);
		    byte[] encryptedPhotoUraPath = encryptData(photoUraData);
		
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
                
	
	
	
	/*public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
	    // 文字列データをバイナリ形式に変換
	    byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
	    byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
	    byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
	    byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
	    byte[] photoomote = photoomotePath.getBytes(StandardCharsets.UTF_8);
	    byte[] photoura = photouraPath.getBytes(StandardCharsets.UTF_8);
	    
	    
	 // 画像ファイルパスをバイナリ形式に変換
	    byte[] photoOmoteData = photoomotePath != null ? photoomotePath.getBytes(StandardCharsets.UTF_8) : null;
	    byte[] photoUraData = photouraPath != null ? photouraPath.getBytes(StandardCharsets.UTF_8) : null;
	
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