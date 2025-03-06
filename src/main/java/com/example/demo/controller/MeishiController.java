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

            // モデルに暗号化された項目を追加
            model.addAttribute("id", id);
            model.addAttribute("personalname", personalname);
            model.addAttribute("personalkananame", personalkananame);
            model.addAttribute("mobiletel", mobiletel);
            model.addAttribute("email", email);
            model.addAttribute("photoomotePath", photoomotePath);
            model.addAttribute("photouraPath", photouraPath);

            // 受け取った値の確認
            System.out.println("Received personalname: " + personalname);
            System.out.println("Received personalkananame: " + personalkananame);
            System.out.println("Received mobiletel: " + mobiletel);
            System.out.println("Received email: " + email);
            System.out.println("Received photoomotePath: " + photoomotePath);
            System.out.println("Received photouraPath: " + photouraPath);

            // データベースから名刺Entityの情報を取得する
            Optional<MeishiEntity> meishiOpt = meishisRepository.findById(id);

            if (meishiOpt.isPresent()) {
                MeishiEntity meishi = meishiOpt.get();

                // 暗号化されていない項目をモデルに追加
                model.addAttribute("companyname", meishi.getCompanyname());
                model.addAttribute("companykananame", meishi.getCompanykananame());
                model.addAttribute("belong", meishi.getBelong());
                model.addAttribute("position", meishi.getPosition());
                model.addAttribute("address", meishi.getAddress());
                model.addAttribute("companytel", meishi.getCompanytel());

                // データベースから取得した値の確認
                System.out.println("Database companyname: " + meishi.getCompanyname());
                System.out.println("Database companykananame: " + meishi.getCompanykananame());
                System.out.println("Database belong: " + meishi.getBelong());
                System.out.println("Database position: " + meishi.getPosition());
                System.out.println("Database address: " + meishi.getAddress());
                System.out.println("Database companytel: " + meishi.getCompanytel());

                // 画像パスの処理
                try {
                    if (photoomotePath != null && !photoomotePath.isEmpty()) {
                        String omoteFileName = photoomotePath.substring(photoomotePath.lastIndexOf("\\") + 1);
                        String omoteImagePath = "/images/" + omoteFileName;
                        meishi.setOmoteImagePath(omoteImagePath);
                        model.addAttribute("omoteImagePath", omoteImagePath);
                        System.out.println("Processed omoteImagePath: " + omoteImagePath);
                    }
                    if (photouraPath != null && !photouraPath.isEmpty()) {
                        String uraFileName = photouraPath.substring(photouraPath.lastIndexOf("\\") + 1);
                        String uraImagePath = "/images/" + uraFileName;
                        meishi.setUraImagePath(uraImagePath);
                        model.addAttribute("uraImagePath", uraImagePath);
                        System.out.println("Processed uraImagePath: " + uraImagePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error during photo path processing: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("No MeishiEntity found for id: " + id);
            }

            System.out.println("id選択時は、" + id);

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
        
	/*@DeleteMapping("/deleteMeishi")
	public String meishiDelete(@RequestParam(name = "id") Integer id,
		Model model) {
		System.out.println("id" + id);
		model.addAttribute("id", id);
	   
		//名刺エンティティを削除する
	    meishiService.deleteMeishiById(id);
	    System.out.println("meishisテーブルの該当行を削除"); 
	    model.addAttribute("message","削除が完了しました。"); 
	    return "/meishi/deleteMeshi";
	   }*/
    
        
        
    
    
}
  
    
    
    	  