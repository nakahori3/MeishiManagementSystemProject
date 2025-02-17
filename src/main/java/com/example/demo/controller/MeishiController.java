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
	    
	    
	    
	    
/*//企業名（カナ）を検索時の復号化
   @GetMapping("/searchByCompanyKana")
   public String getMeishis(@RequestParam("companykananame") String companykananame, Model model) {
   	
   	// リクエストパラメータの確認
       System.out.println("Received companykananame: " + companykananame);
   	
   	
       // 企業名（カナ）で検索して復号化されたデータを取得
       List<MeishiEntity> decryptedMeishis = meishiService.findByCompanykananame(companykananame);
       
       // 復号化されたデータを表示
       for (MeishiEntity meishi : decryptedMeishis) {
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
       
       // モデルにデータを追加してビューに渡す
       model.addAttribute("meishis", decryptedMeishis);
       return "/meishi/searchResults";
   }
   
   
 //担当者名（カナ）の場合の検索時の復号化
   @GetMapping("/searchByPersonalkananame")
   public String searchByPersonalkananame(@RequestParam("personalkananame") String personalkananame, Model model) {
      
   	// リクエストパラメータの確認
       System.out.println("Received personalkananame: " + personalkananame);
   	
   	
   	// 担当者名（カナ）で検索して復号化されたデータを取得
       List<MeishiEntity> decryptedMeishis = meishiService.findByPertialPersonalKanaName(personalkananame);
       
       // 復号化されたデータを表示
       for (MeishiEntity meishi : decryptedMeishis) {
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
       
       // モデルにデータを追加してビューに渡す
       model.addAttribute("meishis", decryptedMeishis);
       return "meishi/searchResults";
   }
      
   
// 完全一致検索
   @GetMapping("/searchByCompanyKana")
   public String searchByCompanyKana(@RequestParam("companykananame") String companykananame, Model model) {
   	
   	// リクエストパラメータの確認
   	System.out.println("Received companykananame: " + companykananame);
   	
       List<MeishiEntity> exactMatches = meishiService.findByCompanykananame(companykananame);
       for (MeishiEntity meishi : exactMatches) {
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
       model.addAttribute("meishis", exactMatches);
       return "/meishi/searchResults";
   }
   
   
// 部分一致検索
   @GetMapping("/searchByCompanyKana")
   public String searchMeishis(@RequestParam("companykananame") String companykananame, Model model) {
   	
   	System.out.println("Received companykananame: " + companykananame);
   	
   	
       List<MeishiEntity> partialMatches = meishiService.findByPertialCompanyKanaName(companykananame);
       for (MeishiEntity meishi : partialMatches) {
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
       model.addAttribute("meishis", partialMatches);
       return "/meishi/searchResults";
   }
   
// personalkananameの部分一致検索
   @GetMapping("/searchMeishisByPersonalkananame")
   public String searchMeishisByPersonalkananame(@RequestParam("personalkananame") String personalkananame, Model model) {
      
   	System.out.println("Received personalkananame: " + personalkananame);
   	
   	List<MeishiEntity> partialMatches = meishiService.findByPertialPersonalKanaName(personalkananame);
       for (MeishiEntity meishi : partialMatches) {
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
       model.addAttribute("meishis", partialMatches);
       return "/meishi/searchResults";
   }*/
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
	    
	  //企業名（カナ）で完全一致検索
	    @GetMapping("/searchByCompanyKana")
	    public String searchByCompanyKana(@RequestParam("keyword") String keyword, Model model) {
	        List<MeishiEntity> results = meishiService.searchMeishiByCompanyKana(keyword);
	        model.addAttribute("results", results);
	        return "/meishi/searchResults";
	    }

			//企業名（カナ）で部分一致検索
			@GetMapping("/searchByCompanyKanaPartial")
			public String searchByCompanyKanaPartial(@RequestParam("keyword") String keyword, Model model) {
			    List<MeishiEntity> results = meishiService.searchMeishiByCompanyKana_Pertial(keyword);
			    model.addAttribute("results", results);
			    return "/meishi/searchResults";
			}
			
			//担当者名（カナ）で部分一致検索
			@GetMapping("/searchByPersonalKanaPartial")
			public String searchByPersonalKanaPartial(@RequestParam("keyword") String keyword, Model model) {
			    List<MeishiEntity> results = meishiService.searchMeishiByPersonalKana_Pertial(keyword);
			    model.addAttribute("results", results);
			    return "/meishi/searchResults";
			
			}


	    
	    
	    
	    
}*/
