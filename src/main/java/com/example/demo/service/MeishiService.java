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
	
	
	//リポジトリのsaveMeishiを呼び出してデータを保存
	
	public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
		
		String personalnameString = form.getPersonalname();
	    String personalkananameString = form.getPersonalkananame();
	    String mobiletelString = form.getMobiletel();
	    String emailString = form.getEmail();
		
		byte[] personalname = (personalnameString != null) ? personalnameString.substring(0, Math.min(personalnameString.length(), 255)).getBytes(StandardCharsets.UTF_8) : null;
	    byte[] personalkananame = (personalkananameString != null) ? personalkananameString.substring(0, Math.min(personalkananameString.length(), 255)).getBytes(StandardCharsets.UTF_8) : null;
	    byte[] mobiletel = (mobiletelString != null) ? mobiletelString.substring(0, Math.min(mobiletelString.length(), 255)).getBytes(StandardCharsets.UTF_8) : null;
	    byte[] email = (emailString != null) ? emailString.substring(0, Math.min(emailString.length(), 255)).getBytes(StandardCharsets.UTF_8) : null;
	    byte[] photoomote = (photoomotePath != null) ? photoomotePath.getBytes(StandardCharsets.UTF_8) : null;
	    byte[] photoura = (photouraPath != null) ? photouraPath.getBytes(StandardCharsets.UTF_8) : null;
        meishisRepository.saveMeishi(
            form.getCompanyname(),
            form.getCompanykananame(),
            personalname,
            personalkananame,
            form.getBelong(),
            form.getPosition(),
            form.getAddress(),
            form.getCompanytel(),
            mobiletel,
            email,
            photoomote,
            photoura,
            form.getSavedate()
        );
    }
	
	
	
	/*public void saveMeishi(MeishiForm form, String photoomotePath, String photouraPath) {
	    meishisRepository.saveMeishi(
	        form.getCompanyname(),
	        form.getCompanykananame(),
	        form.getPersonalname(),
	        form.getPersonalkananame(),
	        form.getBelong(),
	        form.getPosition(),
	        form.getAddress(),
	        form.getCompanytel(),
	        form.getMobiletel(),
	        form.getEmail(),
	        form.getPhotoomote(),
	        form.getPhotoura(),
	        form.getSavedate()
	    );
	}*/
	
	

}
