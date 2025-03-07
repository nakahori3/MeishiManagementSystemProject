package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.MeishiEntity;
import com.example.demo.form.MeishiForm;
import com.example.demo.repository.MeishisRepository;
import com.example.demo.service.MeishiService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@Transactional
@RequiredArgsConstructor
@SessionAttributes({"personalname", "personalkananame", "mobiletel", "email", "photoomotePath", "photouraPath"})
public class MeishiController {

    private static final String UPLOAD_DIR = "src/main/resources/static/images";

    @Autowired
    MeishiService meishiService;

    @Autowired
    MeishisRepository meishisRepository;

    private static final Logger logger = LoggerFactory.getLogger(MeishiController.class);

    @ModelAttribute("meishiForm")
    public MeishiForm setupMeishiForm() {
        return new MeishiForm();
    }

    @GetMapping("/inputMeishi")
    public String registerMeishi(Model model) {
        model.addAttribute("meishiForm", new MeishiForm());
        return "/meishi/registerMeishi";
    }

    @GetMapping("/searchMeishi")
    public String serachMeishi(Model model) {
        return "/meishi/searchMeishi";
    }

    public List<MeishiEntity> getAllDecryptedMeishis() {
        return meishiService.getAllDecryptedMeishis();
    }

       
    
    @PostMapping("/confirmMeishi")
    public String meishiConfirm(@Validated @ModelAttribute MeishiForm meishiForm, BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            System.out.println("Validation errors occurred: " + result.getAllErrors());
            model.addAttribute("meishiForm", meishiForm);
            return "/meishi/registerMeishi";
        }

        String photoomotePath = saveFile(UPLOAD_DIR, meishiForm.getPhotoomote());
        System.out.println("photoomotePath: " + photoomotePath);

        String photouraPath = saveFile(UPLOAD_DIR, meishiForm.getPhotoura());
        System.out.println("photouraPath: " + photouraPath);

        if (photoomotePath == null) {
            System.out.println("Failed to save photoomote file");
            model.addAttribute("message", "表面のファイルの保存に失敗しました。もう一度お試しください。");
            return "/meishi/registerMeishi";
        }

        // ファイルパスを meishiForm に設定
        meishiForm.setPhotoomotePath(photoomotePath);
        meishiForm.setPhotouraPath(photouraPath);

        session.setAttribute("photoomotePath", photoomotePath);
        session.setAttribute("photouraPath", photouraPath);
        session.setAttribute("meishiForm", meishiForm);

        System.out.println("Set session attributes: photoomotePath=" + photoomotePath + ", photouraPath=" + photouraPath);

        model.addAttribute("photoomoteFileName", meishiForm.getPhotoomote().getOriginalFilename());
        model.addAttribute("photouraFileName", meishiForm.getPhotoura() != null ? meishiForm.getPhotoura().getOriginalFilename() : "ファイルが選択されていません。");

        return "/meishi/confirmMeishi";
    }


    private String saveFile(String uploadDir, MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            System.out.println("Original file name: " + originalFileName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = timestamp + "_" + originalFileName;
            Path filePath = uploadPath.resolve(fileName);
            System.out.println("Saving file to path: " + filePath.toString());
            Files.copy(file.getInputStream(), filePath);
            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("/completeMeishi")
    public String meishiComplete(HttpSession session, Model model) {
        MeishiForm meishiForm = (MeishiForm) session.getAttribute("meishiForm");
        String photoomotePath = (String) session.getAttribute("photoomotePath");
        String photouraPath = (String) session.getAttribute("photouraPath");

        System.out.println("Retrieved session attributes: meishiForm=" + meishiForm + ", photoomotePath=" + photoomotePath + ", photouraPath=" + photouraPath);

        if (meishiForm == null) {
            System.out.println("meishiForm is null");
            model.addAttribute("message", "フォームデータが見つかりません。もう一度お試しください。");
            return "/meishi/registerMeishi";
        }

        if (photoomotePath == null) {
            System.out.println("photoomotePath is null");
            model.addAttribute("message", "表面のファイルパスが見つかりません。もう一度お試しください。");
            return "/meishi/registerMeishi";
        }

        try {
            meishiForm.setPhotoomotePath(photoomotePath);
            if (photouraPath != null) {
                meishiForm.setPhotouraPath(photouraPath);
            }

            // 現在の年月日を取得して設定
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String formatDate = today.format(formatter);
            meishiForm.setSavedate(formatDate);

            System.out.println("Saving meishiForm: " + meishiForm);
            meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
        } catch (Exception e) {
            System.out.println("Error occurred while saving the files: " + e.getMessage());
            model.addAttribute("message", "ファイルの保存中にエラーが発生しました。もう一度お試しください。");
            return "/meishi/registerMeishi";
        }

        return "/meishi/completeMeishi";
    }

  
    	    
    //------------名刺情報検索------------------------	 
    
  //名刺情報の検索と復号化
    
    
        @GetMapping("/searchResults")
        public String searchResults(@RequestParam("searchType") String searchType,
                                @RequestParam("keyword") String keyword,
                                @RequestParam(value = "pgpassword", defaultValue = "P4ssW0rd") String pgpassword,
                                Model model) {
        System.out.println("Received searchType: " + searchType);
        System.out.println("Received keyword: " + keyword);
        System.out.println("Received pgpassword: " + pgpassword);

        List<MeishiEntity> searchResults;
        try {
            if (searchType.equals("companyKanaExact")) {
                System.out.println("Exact match search for companyKanaName");
                searchResults = meishiService.findByCompanykananame(keyword, pgpassword);
            } else if (searchType.equals("companyKanaPartial")) {
                System.out.println("Partial match search for companyKanaName");
                searchResults = meishiService.findByPartialCompanyKanaName(keyword, pgpassword);
            } else if (searchType.equals("personalKanaPartial")) {
                System.out.println("Partial match search for personalKanaName");
                searchResults = meishiService.findByPartialPersonalKanaName(keyword, pgpassword);
            } else {
                searchResults = List.of();
            }

            System.out.println("Search results: " + searchResults.size() + " results found.");

            if (!searchResults.isEmpty()) {
                for (MeishiEntity meishi : searchResults) {
                    System.out.println("Found result: " + meishi);

                    System.out.println("Before decryption:");
                    System.out.println("Personal Name (bytea): " + meishi.getPersonalname());
                    System.out.println("Personal Kana Name (bytea): " + meishi.getPersonalkananame());
                    System.out.println("Mobile Tel (bytea): " + meishi.getMobiletel());
                    System.out.println("Email (bytea): " + meishi.getEmail());

                    System.out.println("After decryption:");
                    System.out.println("Personal Name: " + meishi.getPersonalname());
                    System.out.println("Personal Kana Name: " + meishi.getPersonalkananame());
                    System.out.println("Belong: " + meishi.getBelong());
                    System.out.println("Position: " + meishi.getPosition());
                    System.out.println("Address: " + meishi.getAddress());
                    System.out.println("Company Tel: " + meishi.getCompanytel());
                    System.out.println("Mobile Tel: " + meishi.getMobiletel());
                    System.out.println("Email: " + meishi.getEmail());
                    System.out.println("Photo OmotePath: " + meishi.getPhotoomotePath());
                    System.out.println("Photo UraPath: " + meishi.getPhotouraPath());

                    try {
                        if (meishi.getPhotoomotePath() != null) {
                            String omoteFileName = meishi.getPhotoomotePath().substring(meishi.getPhotoomotePath().lastIndexOf("\\") + 1);
                            String omoteImagePath = "/images/" + omoteFileName;
                            meishi.setOmoteImagePath(omoteImagePath);
                        }
                    } catch (Exception e) {
                        System.err.println("Error during photoomotePath processing: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // グループ化して表示するための準備
                Map<String, List<MeishiEntity>> groupedResults = searchResults.stream()
                    .collect(Collectors.groupingBy(MeishiEntity::getCompanyname));

                model.addAttribute("groupedResults", groupedResults);
            } else {
                model.addAttribute("errorMessage", "検索結果がありません。");
            }
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "検索中にエラーが発生しました。");
        }

        return "meishi/searchResults";
    }


 //--------------名刺情報　詳細画面-------------------------
     @PostMapping("/detailperson")
	public String detailperson(Model model,
	                           @RequestParam(name = "id", required = false) Integer id,
	                           @RequestParam(name = "personalname", required = false) String personalname,
	                           @RequestParam(name = "personalkananame", required = false) String personalkananame,
	                           @RequestParam(name = "mobiletel", required = false) String mobiletel,
	                           @RequestParam(name = "email", required = false) String email,
	                           @RequestParam(name = "photoomotePath", required = false) String photoomotePath,
	                           @RequestParam(name = "photouraPath", required = false) String photouraPath) {
	    
	    // 受け取った値の確認
	    System.out.println("詳細画面 - 受け取ったid: " + id);
	    System.out.println("詳細画面 - 受け取ったpersonalname: " + personalname);
	    System.out.println("詳細画面 - 受け取ったpersonalkananame: " + personalkananame);
	    System.out.println("詳細画面 - 受け取ったmobiletel: " + mobiletel);
	    System.out.println("詳細画面 - 受け取ったemail: " + email);
	    System.out.println("詳細画面 - 受け取ったphotoomotePath: " + photoomotePath);
	    System.out.println("詳細画面 - 受け取ったphotouraPath: " + photouraPath);
	
	    // モデルに復号化された項目を追加（セッションに保存）
	    model.addAttribute("id", id);
	    model.addAttribute("personalname", personalname);
	    model.addAttribute("personalkananame", personalkananame);
	    model.addAttribute("mobiletel", mobiletel);
	    model.addAttribute("email", email);
	    model.addAttribute("photoomotePath", photoomotePath);
	    model.addAttribute("photouraPath", photouraPath);
	
	    // データベースから名刺Entityの情報を取得する
	    Optional<MeishiEntity> meishiOpt = meishisRepository.findById(id);
	
	    if (meishiOpt.isPresent()) {
	        MeishiEntity meishi = meishiOpt.get();
	        System.out.println("詳細画面 - 名刺情報が見つかりました: " + meishi);
	
	        // 暗号化されていない項目をモデルに追加
	        model.addAttribute("companyname", meishi.getCompanyname());
	        model.addAttribute("companykananame", meishi.getCompanykananame());
	        model.addAttribute("belong", meishi.getBelong());
	        model.addAttribute("position", meishi.getPosition());
	        model.addAttribute("address", meishi.getAddress());
	        model.addAttribute("companytel", meishi.getCompanytel());
	
	        // 画像パスの処理
	        try {
	            if (photoomotePath != null && !photoomotePath.isEmpty()) {
	                String omoteFileName = photoomotePath.substring(photoomotePath.lastIndexOf("\\") + 1);
	                String omoteImagePath = "/images/" + omoteFileName;
	                meishi.setOmoteImagePath(omoteImagePath);
	                model.addAttribute("omoteImagePath", omoteImagePath);
	                System.out.println("詳細画面 - 処理されたomoteImagePath: " + omoteImagePath);
	            }
	            if (photouraPath != null && !photouraPath.isEmpty()) {
	                String uraFileName = photouraPath.substring(photouraPath.lastIndexOf("\\") + 1);
	                String uraImagePath = "/images/" + uraFileName;
	                meishi.setUraImagePath(uraImagePath);
	                model.addAttribute("uraImagePath", uraImagePath);
	                System.out.println("詳細画面 - 処理されたuraImagePath: " + uraImagePath);
	            }
	        } catch (Exception e) {
	            System.err.println("詳細画面 - 写真パス処理中のエラー: " + e.getMessage());
	            e.printStackTrace();
	        }
	    } else {
	        System.err.println("詳細画面 - 該当する名刺情報が見つかりません: " + id);
	    }
	
	    return "meishi/detailperson";
	}

 //-----------名刺情報削除ボタン----------------
     
        @PostMapping("/deleteMeishi")
        public String deleteMeishi(@RequestParam(name = "id") Integer id, Model model) {
            logger.info("Start of deleteMeishi method");
            logger.info("Received ID: {}", id);

            meishiService.deleteMeishiById(id);
            logger.info("Meishi deleted from database");

            model.addAttribute("message", "削除が完了しました。");
            logger.info("End of deleteMeishi method");

            return "/meishi/deleteMeishi"; // deleteMeishi.html に遷移
        }
   



//--------名刺情報の修正画面-------------------------------        
        
        
        @PostMapping("/fixMeishi")
        public String fixMeishi(@ModelAttribute("id") Integer id,
                                @ModelAttribute("personalname") String personalname,
                                @ModelAttribute("personalkananame") String personalkananame,
                                @ModelAttribute("mobiletel") String mobiletel,
                                @ModelAttribute("email") String email,
                                @ModelAttribute("photoomotePath") String photoomotePath,
                                @ModelAttribute("photouraPath") String photouraPath,
                                Model model) {

            // デバッグ用ログ
            System.out.println("修正画面 - セッションから取得したid: " + id);
            System.out.println("修正画面 - セッションから取得したpersonalname: " + personalname);
            System.out.println("修正画面 - セッションから取得したpersonalkananame: " + personalkananame);
            System.out.println("修正画面 - セッションから取得したmobiletel: " + mobiletel);
            System.out.println("修正画面 - セッションから取得したemail: " + email);
            System.out.println("修正画面 - セッションから取得したphotoomotePath: " + photoomotePath);
            System.out.println("修正画面 - セッションから取得したphotouraPath: " + photouraPath);
            
            
            // データベースから暗号化されていない項目を取得
            Optional<MeishiEntity> meishiOpt = meishisRepository.findById(id);
            if (!meishiOpt.isPresent()) {
                System.err.println("修正画面 - 該当する名刺情報が見つかりません: " + id);
                return "meishi/fixMeishi";
            }
            MeishiEntity meishi = meishiOpt.get();

            // 必要であればさらにモデルに追加
            model.addAttribute("id", id);
            model.addAttribute("personalname", personalname);
            model.addAttribute("personalkananame", personalkananame);
            model.addAttribute("mobiletel", mobiletel);
            model.addAttribute("email", email);
            model.addAttribute("photoomotePath", photoomotePath);
            model.addAttribute("photouraPath", photouraPath);

            // ファイル名を抽出してモデルに追加
            String photoomoteFileName = (photoomotePath != null && !photoomotePath.isEmpty())
                    ? photoomotePath.substring(photoomotePath.lastIndexOf("\\") + 1) // Windows対応
                    : null;

            String photouraFileName = (photouraPath != null && !photouraPath.isEmpty())
                    ? photouraPath.substring(photoomotePath.lastIndexOf("\\") + 1)
                    : null;

            model.addAttribute("photoomoteFileName", photoomoteFileName);
            model.addAttribute("photouraFileName", photouraFileName);
            
         // 追加のデータベース項目
            model.addAttribute("companyname", meishi.getCompanyname());
            model.addAttribute("companykananame", meishi.getCompanykananame());
            model.addAttribute("belong", meishi.getBelong());
            model.addAttribute("position", meishi.getPosition());
            model.addAttribute("address", meishi.getAddress());
            model.addAttribute("companytel", meishi.getCompanytel());

            return "meishi/fixMeishi";
        }

        
        
        @PostMapping("/fixMeishiConfirm")
        public String fixMeishiConfirm(@RequestParam(name = "id") int id,
                                        @ModelAttribute MeishiForm meishiForm,
                                        HttpSession session,
                                        Model model) {

            System.out.println("修正確認画面 - メソッド開始");

            // セッションから既存のパスを取得
            String photoomotePath = (String) session.getAttribute("photoomotePath");
            String photouraPath = (String) session.getAttribute("photouraPath");

            // ファイル名を抽出
            System.out.println("修正確認画面 - photoomotePathからファイル名を抽出開始");
            String photoomoteFileName = (photoomotePath != null && !photoomotePath.isEmpty())
                    ? photoomotePath.substring(Math.max(photoomotePath.lastIndexOf("/"), photoomotePath.lastIndexOf("\\")) + 1)
                    : null;
            System.out.println("修正確認画面 - 抽出したphotoomoteFileName: " + photoomoteFileName);

            System.out.println("修正確認画面 - photouraPathからファイル名を抽出開始");
            String photouraFileName = (photouraPath != null && !photouraPath.isEmpty())
                    ? photouraPath.substring(Math.max(photouraPath.lastIndexOf("/"), photouraPath.lastIndexOf("\\")) + 1)
                    : null;
            System.out.println("修正確認画面 - 抽出したphotouraFileName: " + photouraFileName);

            // モデルにファイル名を渡す
            model.addAttribute("photoomoteFileName", photoomoteFileName);
            model.addAttribute("photouraFileName", photouraFileName);

            // データベースから暗号化されていない情報を取得
            Optional<MeishiEntity> meishiOpt = meishisRepository.findById(id);
            if (meishiOpt.isPresent()) {
                MeishiEntity meishi = meishiOpt.get();

                // 暗号化されていない情報をモデルに追加
                model.addAttribute("companyname", meishi.getCompanyname());
                model.addAttribute("companykananame", meishi.getCompanykananame());
                model.addAttribute("belong", meishi.getBelong());
                model.addAttribute("position", meishi.getPosition());
                model.addAttribute("address", meishi.getAddress());
                model.addAttribute("companytel", meishi.getCompanytel());
            } else {
                System.err.println("修正確認画面 - 該当する名刺情報が見つかりません: " + id);
                return "/meishi/fixMeishi"; // エラーハンドリングとして修正画面に戻す
            }

            // その他のフォームデータをモデルに追加
            model.addAttribute("meishiForm", meishiForm);

            System.out.println("修正確認画面 - メソッド終了");
            return "/meishi/fixMeishiConfirm";
        }


      //檀家情報とDBへの接続と修正の登録
    	@PostMapping("/fixMeishiComplete")
    	public String fixMeishiComplete(@RequestParam(name = "id") int id, 
    									HttpSession session, Model model) {
    	
    	        MeishiForm meishiForm = (MeishiForm) session.getAttribute("meishiForm");
    	        String photoomotePath = (String) session.getAttribute("photoomotePath");
    	        String photouraPath = (String) session.getAttribute("photouraPath");

    	        System.out.println("Retrieved session attributes: meishiForm=" + meishiForm + ", photoomotePath=" + photoomotePath + ", photouraPath=" + photouraPath);

    	        if (meishiForm == null) {
    	            System.out.println("meishiForm is null");
    	            model.addAttribute("message", "フォームデータが見つかりません。もう一度お試しください。");
    	            return "/meishi/registerMeishi";
    	        }

    	        if (photoomotePath == null) {
    	            System.out.println("photoomotePath is null");
    	            model.addAttribute("message", "表面のファイルパスが見つかりません。もう一度お試しください。");
    	            return "/meishi/registerMeishi";
    	        }

    	        try {
    	            meishiForm.setPhotoomotePath(photoomotePath);
    	            if (photouraPath != null) {
    	                meishiForm.setPhotouraPath(photouraPath);
    	            }

  
    	            System.out.println("SavingFiXing meishiForm: " + meishiForm);    	            
    	            meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
    	        } catch (Exception e) {
    	            System.out.println("Error occurred while saving the files: " + e.getMessage());
    	            model.addAttribute("message", "ファイルの保存中にエラーが発生しました。もう一度お試しください。");
    	            return "/meishi/registerMeishi";
    	        }

    	        return "/meishi/fixMeishiComplete";
    	    }

        
    
}


    
    
    	  