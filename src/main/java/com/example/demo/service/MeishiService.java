package com.example.demo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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
    
    
    public void saveEncryptedMeishi(MeishiForm meishiForm, byte[] encryptedPhotoomote, byte[] encryptedPhotoura, String encodedKey, String formatDate) {
        MeishiEntity meishiEntity = new MeishiEntity();
        meishiEntity.setCompanyname(meishiForm.getCompanyname());
        meishiEntity.setCompanykananame(meishiForm.getCompanykananame());
        meishiEntity.setPersonalname(encryptData(meishiForm.getPersonalname(), encodedKey));
        meishiEntity.setPersonalkananame(encryptData(meishiForm.getPersonalkananame(), encodedKey));
        meishiEntity.setBelong(meishiForm.getBelong());
        meishiEntity.setPosition(meishiForm.getPosition());
        meishiEntity.setAddress(meishiForm.getAddress());
        meishiEntity.setCompanytel(meishiForm.getCompanytel());
        meishiEntity.setMobiletel(encryptData(meishiForm.getMobiletel(), encodedKey));
        meishiEntity.setEmail(encryptData(meishiForm.getEmail(), encodedKey));
        meishiEntity.setPhotoomote(encryptedPhotoomote);
        meishiEntity.setPhotoura(encryptedPhotoura);
        meishiEntity.setSavedate(formatDate);

        meishisRepository.save(meishiEntity);
    }

    // ファイル保存メソッド
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save file: " + file.getOriginalFilename(), e);
        }
        return "/images/" + file.getOriginalFilename();
    }

    // 文字列データの暗号化
    private byte[] encryptData(String data, String encodedKey) {
        try {
            SecretKey key = decodeKey(encodedKey);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 暗号化キーのデコード
    private SecretKey decodeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
    
    
    
    
    
	/*public void saveMeishi(MeishiForm form, MultipartFile photoomoteFile, MultipartFile photouraFile, String formatDate) {
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
	        form.setPhotoomotePath(photoomotePath);  // 追加されたフィールドに設定
	        form.setPhotouraPath(photouraPath);  // 追加されたフィールドに設定
	
	        // 文字列データをバイナリ形式に変換
	        byte[] personalname = form.getPersonalname().getBytes(StandardCharsets.UTF_8);
	        byte[] personalkananame = form.getPersonalkananame().getBytes(StandardCharsets.UTF_8);
	        byte[] mobiletel = form.getMobiletel().getBytes(StandardCharsets.UTF_8);
	        byte[] email = form.getEmail().getBytes(StandardCharsets.UTF_8);
	        byte[] photoomote = Files.readAllBytes(Paths.get(UPLOAD_DIR, photoomoteFile.getOriginalFilename()));
	        byte[] photoura = Files.readAllBytes(Paths.get(UPLOAD_DIR, photouraFile.getOriginalFilename()));
	
	        // 暗号化処理を行う (ここでは例として Base64 エンコードを使用)
	        byte[] encryptedPersonalName = Base64.getEncoder().encode(personalname);
	        byte[] encryptedPersonalKanaName = Base64.getEncoder().encode(personalkananame);
	        byte[] encryptedMobileTel = Base64.getEncoder().encode(mobiletel);
	        byte[] encryptedEmail = Base64.getEncoder().encode(email);
	        byte[] encryptedPhotoOmote = Base64.getEncoder().encode(photoomote);
	        byte[] encryptedPhotoUra = Base64.getEncoder().encode(photoura);
	
	        MeishiEntity meishiEntity = new MeishiEntity();
	        meishiEntity.setCompanyname(form.getCompanyname());
	        meishiEntity.setCompanykananame(form.getCompanykananame());
	        meishiEntity.setPersonalname(encryptedPersonalName);
	        meishiEntity.setPersonalkananame(encryptedPersonalKanaName);
	        meishiEntity.setBelong(form.getBelong());
	        meishiEntity.setPosition(form.getPosition());
	        meishiEntity.setAddress(form.getAddress());
	        meishiEntity.setCompanytel(form.getCompanytel());
	        meishiEntity.setMobiletel(encryptedMobileTel);
	        meishiEntity.setEmail(encryptedEmail);
	        meishiEntity.setPhotoomote(encryptedPhotoOmote);
	        meishiEntity.setPhotoura(encryptedPhotoUra);
	        meishiEntity.setSavedate(formatDate);
	
	        meishisRepository.save(meishiEntity);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	// saveFileメソッドの追加
	private String saveFile(MultipartFile file) throws IOException {
	    if (file.isEmpty()) {
	        return null;
	    }
	    Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
	    try {
	        Files.write(filePath, file.getBytes());
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new IOException("Failed to save file: " + file.getOriginalFilename(), e);
	    }
	    return "/images/"+ file.getOriginalFilename();
	}*/
    

    
	/*public void saveMeishi(MeishiForm form, MultipartFile photoomoteFile, MultipartFile photouraFile, String formatDate) {
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
	        form.setPhotoomotePath(photoomotePath);  // 追加されたフィールドに設定
	        form.setPhotouraPath(photouraPath);  // 追加されたフィールドに設定
	
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
	
	// saveFileメソッドの追加
	private String saveFile(MultipartFile file) throws IOException {
	    if (file.isEmpty()) {
	        return null;
	    }
	    Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
	    try {
	        Files.write(filePath, file.getBytes());
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new IOException("Failed to save file: " + file.getOriginalFilename(), e);
	    }
	    return "/images/" + file.getOriginalFilename();
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

	public void saveMeishi(MeishiForm meishiForm, String photoomotePath, String photouraPath, String formatDate) {
		// TODO 自動生成されたメソッド・スタブ
		
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


/*private String saveFile(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
        return null;
    }
    Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
    Files.write(filePath, file.getBytes());
    return "/images/" + file.getOriginalFilename();
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







