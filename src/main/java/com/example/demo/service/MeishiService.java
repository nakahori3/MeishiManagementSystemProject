package com.example.demo.service;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // すべての名刺Entityを取得
    public List<MeishiEntity> getAllDecryptedMeishis() {
        String pgpassword = PGPASSWORD; // ここでパスワードを指定
        return meishisRepository.findAllDecrypted(pgpassword);
    }

    // 名刺IDから名刺Entityを取得
    public MeishiEntity getDecryptedMeishiById(int id) throws Exception {
        System.out.println("Getting decrypted Meishi by ID: " + id);

        String pgpassword = PGPASSWORD; // パスワードを指定
        System.out.println("pgpassword: " + pgpassword);

        MeishiEntity meishi = meishisRepository.findDecryptedById(id, pgpassword);
        System.out.println("Decrypted Meishi: " + meishi);
        System.out.println("photoomotePath: " + meishi.getPhotoomotePath()); // 追加: photoomotePathを出力

        // 画像ファイル名を設定
        String photoPath = meishi.getPhotoomotePath();
        String fileName = photoPath.substring(photoPath.lastIndexOf("\\") + 1);
        meishi.setPhotoomotePath(fileName);

        System.out.println("Extracted fileName: " + fileName); // 抽出されたファイル名を出力

        return meishi;
    }

    // UserEntityオブジェクトのリストを作成するメソッド
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

            // 画像ファイル名を設定
            String photoPath = meishi.getPhotoomotePath();
            String fileName = photoPath.substring(photoPath.lastIndexOf("\\") + 1);
            meishi.setPhotoomotePath(fileName);

            System.out.println("Extracted fileName: " + fileName); // 抽出されたファイル名を出力
        }
        return meishiList;
    }

    @Transactional
    public List<MeishiEntity> findByCompanykananame(String companykananame, String pgpassword) {
        System.out.println("Searching by companykananame: " + companykananame);
        return meishisRepository.findByCompanykananame(companykananame, pgpassword);
    }

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

    // 企業名（カナ）より名刺Entityを取得
    @Transactional
    public List<MeishiEntity> findByPartialCompanyKanaName(String companykananame, String pgpassword) {
        try {
            System.out.println("Searching by companyKanaPartial grouped by companyname: " + companykananame);
            List<MeishiEntity> results = meishisRepository.findByPartialCompanyKanaName(companykananame, pgpassword);
            System.out.println("Search results: " + results.size() + " results found.");
            for (MeishiEntity result : results) {
                System.out.println("Found: " + result.getCompanykananame());
            }
            return results;
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public List<MeishiEntity> findByPartialPersonalKanaName(String personalkananame, String pgpassword) {
        try {
            System.out.println("Searching by personalKanaPartial: " + personalkananame);
            List<MeishiEntity> results = meishisRepository.findByPartialPersonalKanaName(personalkananame, pgpassword);
            System.out.println("Search results: " + results.size() + " results found.");
            for (MeishiEntity result : results) {
                System.out.println("Found: " + result.getPersonalkananame());
            }
            return results;
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            throw e;
        }
    }
    
    public Optional<MeishiEntity> getMeishiById(int newMeishiId) {
		return meishisRepository.findById(newMeishiId);
	}
    
    
    // 名刺情報の削除
    public MeishiService(MeishisRepository meishisRepository) {
        this.meishisRepository = meishisRepository;
    }

    @Transactional
    public void deleteMeishiById(Integer id) {
        System.out.println("Start of deleteMeishiById method");
        meishisRepository.deleteById(id);
        System.out.println("End of deleteMeishiById method");
    }

	public MeishiEntity findById(int id) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
	
	// 名寄せ機能：担当者名で重複している名刺を取得
	public List<MeishiEntity> findDuplicateMeishi(List<MeishiEntity> meishiList) {
        return meishiList.stream()
                .collect(Collectors.groupingBy(MeishiEntity::getPersonalkananame))
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
	
	

	//CSV出力：復号化とCSVフォーマットへの書き込み
	
	
	public void writeCsv(PrintWriter writer, List<MeishiEntity> filteredResults) throws Exception {
	    writer.write("\uFEFF"); // BOM を追加して文字化けを防止
	    try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
	            "ID", "Company Name", "Company Kana Name", "Personal Name",
	            "Personal Kana Name", "Belong", "Position", "Address",
	            "Company Tel", "Mobile Tel", "Email", "Photo Omote Path",
	            "Photo Ura Path", "Save Date"))) {

	        for (MeishiEntity meishi : filteredResults) {
	            csvPrinter.printRecord(
	                    meishi.getId(),
	                    meishi.getCompanyname(),
	                    meishi.getCompanykananame(),
	                    meishi.getPersonalname(),
	                    meishi.getPersonalkananame(),
	                    meishi.getBelong(),
	                    meishi.getPosition(),
	                    meishi.getAddress(),
	                    "'" + meishi.getCompanytel(),
	                    "'" + meishi.getMobiletel(),
	                    meishi.getEmail(),
	                    meishi.getPhotoomotePath(),
	                    meishi.getPhotouraPath(),
	                    meishi.getSavedate()
	            );
	        }
	    }
	}

}


