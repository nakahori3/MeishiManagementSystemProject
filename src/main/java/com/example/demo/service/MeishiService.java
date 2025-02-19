package com.example.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    private static final String UPLOAD_DIR = "src/main/resources/static/images";

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
    public void saveMeishi(MeishiForm form, MultipartFile photoomoteFile, MultipartFile photouraFile, String formatDate) {
        try {
            // ファイルを保存するディレクトリを作成
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // ファイルを保存
            String photoomotePath = saveFile(photoomoteFile);
            String photouraPath = saveFile(photouraFile);

            // 名刺フォームにファイルパスを設定
            form.setPhotoomote(photoomotePath);
            form.setPhotoura(photouraPath);

            // 文字列データをバイナリ形式に変換
            byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
            byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
            byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
            byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
            byte[] photoomote = photoomotePath.getBytes(StandardCharsets.UTF_8);
            byte[] photoura = photouraPath.getBytes(StandardCharsets.UTF_8);

            // 暗号化処理はデータベースで行うため、変換後のデータを文字列として扱う
            String encryptedPersonalName = new String(personalname, StandardCharsets.UTF_8);
            String encryptedPersonalKanaName = new String(personalkananame, StandardCharsets.UTF_8);
            String encryptedMobileTel = new String(mobiletel, StandardCharsets.UTF_8);
            String encryptedEmail = new String(email, StandardCharsets.UTF_8);
            String encryptedPhotoOmote = new String(photoomote, StandardCharsets.UTF_8);
            String encryptedPhotoUra = new String(photoura, StandardCharsets.UTF_8);

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
                encryptedPhotoUra,
                formatDate
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        Files.write(filePath, file.getBytes());
        return "/images/" + file.getOriginalFilename();
    }

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

        // 暗号化処理はデータベースで行うため、変換後のデータを文字列として扱う
        String encryptedPersonalName = new String(personalname, StandardCharsets.UTF_8);
        String encryptedPersonalKanaName = new String(personalkananame, StandardCharsets.UTF_8);
        String encryptedMobileTel = new String(mobiletel, StandardCharsets.UTF_8);
        String encryptedEmail = new String(email, StandardCharsets.UTF_8);
        String encryptedPhotoOmote = new String(photoomote, StandardCharsets.UTF_8);
        String encryptedPhotoUra = new String(photoura, StandardCharsets.UTF_8);

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

	public void saveMeishi(MeishiForm meishiForm, String photoomotePath, String photouraPath, String formatDate) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}*/








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
*/







