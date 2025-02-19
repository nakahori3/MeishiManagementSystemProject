package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

import lombok.RequiredArgsConstructor;

@Controller
@Transactional
@RequiredArgsConstructor
public class MeishiController {

    @Autowired
    MeishiService meishiService;

    @Autowired
    MeishisRepository meishisRepository;

    private static final Logger logger = LoggerFactory.getLogger(MeishiController.class);

    @ModelAttribute("meishiForm")
    public MeishiForm setupMeishiForm() {
        return new MeishiForm();
    }

    // 名刺情報登録画面へ遷移
    @GetMapping("/inputMeishi")
    public String registerMeishi(Model model) {
        model.addAttribute("meishiForm", new MeishiForm());
        return "/meishi/registerMeishi";
    }

    // 名刺情報検索画面へ遷移
    @GetMapping("/searchMeishi")
    public String searchMeishi(Model model) {
        return "/meishi/searchMeishi";
    }

    // データベースからMeishiEntityの一覧を取得する
    public List<MeishiEntity> getAllDecryptedMeishis() {
        return meishiService.getAllDecryptedMeishis();
    }

    // エラーありの場合、新規登録画面
    // エラーなしの場合、確認画面へ遷移
    @PostMapping("/confirmMeishi")
    public String meishiConfirm(@Validated MeishiForm meishiForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // バリデーションエラーありの場合、新規登録画面
            model.addAttribute("meishiForm", meishiForm);
            return "/meishi/registerMeishi";
        }
        // エラーなしの場合、確認画面へ遷移
        model.addAttribute("meishiForm", meishiForm);
        return "/meishi/confirmMeishi";
    }

    
    
 // 名刺情報とDBへの接続
    @PostMapping("/completeMeishi")
    public String meishiComplete(@Validated MeishiForm meishiForm,
                                 @RequestParam("photoomote") MultipartFile photoomoteFile,
                                 @RequestParam("photoura") MultipartFile photouraFile,
                                 Model model) {
        try {
            logger.info("Received photoomote file: " + photoomoteFile.getOriginalFilename());
            logger.info("Received photoura file: " + photouraFile.getOriginalFilename());

            // 今日の日付を保存
            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String formatDate = today.format(formatter);

            meishiService.saveMeishi(meishiForm, photoomoteFile, photouraFile, formatDate);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Error occurred while saving the files");
            return "/meishi/registerMeishi";
        }

        return "/meishi/completeMeishi";
    }

    
    
    
	/*// 名刺情報とDBへの接続
	@PostMapping("/completeMeishi")
	public String meishiComplete(@Validated MeishiForm meishiForm,
	                             @RequestParam("photoomote") MultipartFile photoomoteFile,
	                             @RequestParam("photoura") MultipartFile photouraFile,
	                             Model model) {
	    try {
	        logger.info("Received photoomote file: " + photoomoteFile.getOriginalFilename());
	        logger.info("Received photoura file: " + photouraFile.getOriginalFilename());
	
			 String uploadDir = "src/main/resources/static/images";
	        String photoomotePath = saveFile(uploadDir, photoomoteFile);
	        String photouraPath = saveFile(uploadDir, photouraFile);
	
	        // 今日の日付を保存
	        LocalDateTime today = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	        String formatDate = today.format(formatter);
	
	        meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath, formatDate);
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", "Error occurred while saving the files");
	        return "/meishi/registerMeishi";
	    }
	
	    return "/meishi/completeMeishi";
	}*/

    
    
    
    private String saveFile(String uploadDir, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        logger.info("Saved file: " + filePath.toString());
        return "/images/" + fileName;
    }

    // 検索結果の処理と復号化
    @GetMapping("/searchResults")
    public String searchResults(@RequestParam("searchType") String searchType,
                                @RequestParam("keyword") String keyword,
                                Model model) {
        System.out.println("Received searchType: " + searchType);
        System.out.println("Received keyword: " + keyword);

        List<MeishiEntity> searchResults;
        if (searchType.equals("companyKanaExact")) {
            searchResults = meishiService.findByCompanykananame(keyword);
        } else if (searchType.equals("companyKanaPartial")) {
            searchResults = meishiService.findByPertialCompanyKanaName(keyword);
        } else if (searchType.equals("personalKanaPartial")) {
            searchResults = meishiService.findByPertialPersonalKanaName(keyword);
        } else {
            searchResults = null;
        }

        if (searchResults != null) {
            model.addAttribute("meishis", searchResults);
        }

        return "/meishi/searchResults";
    }
}






/*package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

import lombok.RequiredArgsConstructor;

@Controller
@Transactional
@RequiredArgsConstructor
public class MeishiController {
	
		@Autowired
		MeishiService meishiService;

		@Autowired
        MeishisRepository meishisRepository;
		
		private String savedate;
	
	    		
		@ModelAttribute("meishiForm")
		public MeishiForm setupMeishiForm() {
			return new MeishiForm();
		}
		
			
		// 名刺情報登録画面へ遷移
		@GetMapping("/inputMeishi")
		public String registerMeishi(Model model) {
			model.addAttribute("meishiForm", new MeishiForm());
			return "/meishi/registerMeishi";
		}
		
		// 名刺情報検索画面へ遷移
		@GetMapping("/searchMeishi")
		public String serachMeishi(Model model) {
			return "/meishi/searchMeishi";
		}
		
		
		
		
//------------名刺情報登録------------------------
	
		
	//データベースからMeishiEntityの一覧を取得する
		public List<MeishiEntity> getAllDecryptedMeishis() {
	        return meishiService.getAllDecryptedMeishis();
	    }
		
		private static final Logger logger = LoggerFactory.getLogger(MeishiController.class);
		
		
		//エラーありの場合、新規登録画面
		//エラーなしの場合、確認画面へ遷移

		@PostMapping("/confirmMeishi")
		public String meishiConfirm(@Validated MeishiForm meishiForm, BindingResult result, Model model) {
			if (result.hasErrors()) {
				// バリデーションエラーありの場合、新規登録画面
				model.addAttribute("meishiForm", meishiForm);
				return "/meishi/registerMeishi";
			}
			//エラーなしの場合、確認画面へ遷移
			return "/meishi/confirmMeishi";
		}
		
		//名刺情報とDBへの接続
		
		@PostMapping("/completeMeishi")
		public String meishiComplete(@Validated MeishiForm meishiForm, 
		                             @RequestParam("photoomote") String photoomotePath, 
		                             @RequestParam("photoura") String photouraPath, 
		                             Model model) {
		    try {
		        // Meishiエンティティを作成して保存
		        LocalDateTime today = LocalDateTime.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		        String formatDate = today.format(formatter);

		        meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
		    } catch (Exception e) {
		        e.printStackTrace();
		        model.addAttribute("message", "Error occurred while saving the files");
		        return "/meishi/registerMeishi";
		    }

		    return "/meishi/completeMeishi";
		}

		

	    private String saveFile(String uploadDir, MultipartFile file) throws IOException {
	        if (file.isEmpty()) {
	            return null;
	        }
	        Path uploadPath = Paths.get(uploadDir);
	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }
	        String fileName = file.getOriginalFilename();
	        Path filePath = uploadPath.resolve(fileName);
	        Files.copy(file.getInputStream(), filePath);
	        return filePath.toString();
	    
	    }
				    
	    
	    
	    
//------------名刺情報検索------------------------	    
	    
	    
	 // 検索結果の処理と復号化
	    @GetMapping("/searchResults")
	    public String searchResults(@RequestParam("searchType") String searchType,
	                                @RequestParam("keyword") String keyword,
	                                Model model) {
	        System.out.println("Received searchType: " + searchType);
	        System.out.println("Received keyword: " + keyword);

	        List<MeishiEntity> searchResults;
	        if (searchType.equals("companyKanaExact")) {
	            searchResults = meishiService.findByCompanykananame(keyword);
	        } else if (searchType.equals("companyKanaPartial")) {
	            searchResults = meishiService.findByPertialCompanyKanaName(keyword);
	        } else if (searchType.equals("personalKanaPartial")) {
	            searchResults = meishiService.findByPertialPersonalKanaName(keyword);
	        } else {
	            searchResults = null;
	        }

	        if (searchResults != null) {
	            // 復号化されたデータを表示
	            for (MeishiEntity meishi : searchResults) {
	                System.out.println("Personal Name: " + meishi.getPersonalname());
	                System.out.println("Personal Kana Name: " + meishi.getPersonalkananame());
	                System.out.println("Belong: " + meishi.getBelong());
	                System.out.println("Position: " + meishi.getPosition());
	                System.out.println("Address: " + meishi.getAddress());
	                System.out.println("Company Tel: " + meishi.getCompanytel());
	                System.out.println("Mobile Tel: " + meishi.getMobiletel());
	                System.out.println("Email: " + meishi.getEmail());
	                System.out.println("Photo Omote: " + meishi.getPhotoomote());
	                System.out.println("Photo Ura: " + meishi.getPhotoura());
	            }
	            model.addAttribute("meishis", searchResults);
	        }

	        return "/meishi/searchResults";
	    }
}*/

