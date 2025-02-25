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
		public String meishiConfirm(@Validated MeishiForm meishiForm, BindingResult result, Model model, HttpSession session) {
			if (result.hasErrors()) {
				// バリデーションエラーありの場合、新規登録画面
				model.addAttribute("meishiForm", meishiForm);
				return "/meishi/registerMeishi";
			}
			
			// ファイルアップロード処理
	        String photoomotePath = saveFile(UPLOAD_DIR, meishiForm.getPhotoomote());
	        String photouraPath = saveFile(UPLOAD_DIR, meishiForm.getPhotoura());
	        
			if (photoomotePath == null || photouraPath == null) {
			    // ファイル保存に失敗した場合、新規登録画面にリダイレクト
			    model.addAttribute("message", "ファイルの保存に失敗しました。もう一度お試しください。");
			    return "/meishi/registerMeishi";
			}
			
			// ファイルパスをセッションに保存
			session.setAttribute("photoomotePath", photoomotePath);
			session.setAttribute("photouraPath", photouraPath);
	        
	     // ファイル名をモデルに追加
	        model.addAttribute("photoomoteFileName", meishiForm.getPhotoomote().getOriginalFilename());
	        model.addAttribute("photouraFileName", meishiForm.getPhotoura().getOriginalFilename());

			/*// サービス層に渡す
			meishiService.saveMeishi(meishiForm,  photoomotePath, photouraPath);*/
			
			
			//エラーなしの場合、確認画面へ遷移
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
	            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	            String fileName = timestamp + "_" + originalFileName;
	            Path filePath = uploadPath.resolve(fileName);
	            Files.copy(file.getInputStream(), filePath);
				/*String fileName = file.getOriginalFilename();
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(file.getInputStream(), filePath);*/
	            return filePath.toString();
	        } catch (IOException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
		
		
		//名刺情報とDBへの接続
		
		@PostMapping("/completeMeishi")
		public String meishiComplete(@Validated MeishiForm meishiForm, 
				HttpSession session,
				/*@RequestParam("photoomote") String photoomotePath, 
				 @RequestParam("photoura") String photouraPath, */
		                             Model model) {
		    try {
		        // Meishiエンティティを作成して保存
		        LocalDateTime today = LocalDateTime.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		        String formatDate = today.format(formatter);
		        
				// セッションからファイルパスを取得
				   String photoomotePath = (String) session.getAttribute("photoomotePath");
				   String photouraPath = (String) session.getAttribute("photouraPath");
		        		
		        meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
		    } catch (Exception e) {
		        e.printStackTrace();
		        model.addAttribute("message", "Error occurred while saving the files");
		        return "/meishi/registerMeishi";
		    }

		    return "/meishi/completeMeishi";
		}

		

		/*  private String saveFile(String uploadDir, MultipartFile file) throws IOException {
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
		  
		  }*/
				    
	    
	    
	    
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
}
	  