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
	
	    		
		@ModelAttribute
		public MeishiForm setupMeishiForm() {
			return new MeishiForm();
		}
		
		//トップ画面へ遷移
		@GetMapping("/return_top")
		public String top() {
			return "/top";
		}
		
		// 名刺情報登録画面へ遷移
		@GetMapping("/inputMeishi")
		public String registerMeishi(Model model) {
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
				
				return "/meishi/registerMeishi";
			}
			//エラーなしの場合、確認画面へ遷移
			return "/meishi/confirmMeishi";
		}
		
		//名刺情報とDBへの接続
		@PostMapping("/completeMeishi")
		public String meishiComplete(@Validated  MeishiForm meishiForm, @RequestParam("photoomoteFile") MultipartFile photoomoteFile, @RequestParam("photouraFile") MultipartFile photouraFile, Model model) {
			
			// 画像を保存するディレクトリ
	        String uploadDir = "C:\\\\Users\\\\NSA059\\\\Desktop\\\\名刺管理ソフト\\\\upload\\\\";

	        // ファイルの保存処理
	        try {
	            String photoomotePath = saveFile(uploadDir, photoomoteFile);
	            String photouraPath = saveFile(uploadDir, photouraFile);

	            // Meishiエンティティを作成して保存
	            LocalDateTime today = LocalDateTime.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	            String formatDate = today.format(formatter);

	            meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
	        } catch (IOException e) {
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
			
			
			
			
			/*// 画像を保存するディレクトリ
	        String uploadDir = "C:\\Users\\NSA059\\Desktop\\名刺管理ソフト\\upload\\";
			
			
	    // ファイルの保存処理
	     try {
	          String photoomotePath = saveFile(uploadDir, photoomoteFile);
	          String photouraPath = saveFile(uploadDir, photouraFile);
			
			// データベースに登録する値を保持するインスタンス
			MeishiEntity meishi = new MeishiEntity();

			//現在日時を取得
			LocalDateTime today = LocalDateTime.now();
			// 表示形式を指定
			DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			String formatDate = today.format(fomatter);

			// 画面から受け取った値をデータベースに保存するインスタンスに渡す
			meishi.setCompanyname(meishiForm.getCompanyname());
			meishi.setCompanykananame(meishiForm.getCompanykananame());
			meishi.setPersonalname(meishiForm.getPersonalname());
			meishi.setPersonalkananame(meishiForm.getPersonalkananame());
			meishi.setBelong(meishiForm.getBelong());
			meishi.setPosition(meishiForm.getPosition());
			meishi.setAddress(meishiForm.getAddress());
			meishi.setCompanytel(meishiForm.getCompanytel());
			meishi.setMobiletel(meishiForm.getMobiletel());
			meishi.setEmail(meishiForm.getEmail());
			meishi.setPhotoomote(photoomotePath);
			meishi.setPhotoura(photouraPath);
			meishi.setSavedate(formatDate);

			//データベースに登録する
			meishisRepository.save(meishi);
			return "/meishi/completeMeishi";
		} catch (IOException e) {
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
    }*/
	        
	
}
