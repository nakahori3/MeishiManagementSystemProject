package com.example.demo.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.example.demo.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;
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
    
	@Autowired
	private PdfService pdfService;
	
    
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
        return "meishi/searchMeishi";
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
                                Model model,
                                HttpSession session) {

        System.out.println("Received searchType: " + searchType);
        System.out.println("Received keyword: " + keyword);
        System.out.println("Received pgpassword: " + pgpassword);

        List<MeishiEntity> searchResults = List.of();

        try {
            // 検索タイプに応じた処理
            if ("companyKanaExact".equals(searchType)) {
                searchResults = meishiService.findByCompanykananame(keyword, pgpassword);
            } else if ("companyKanaPartial".equals(searchType)) {
                searchResults = meishiService.findByPartialCompanyKanaName(keyword, pgpassword);
            } else if ("personalKanaPartial".equals(searchType)) {
                searchResults = meishiService.findByPartialPersonalKanaName(keyword, pgpassword);
            }

            System.out.println("Search results: " + searchResults.size() + " results found.");

            if (!searchResults.isEmpty()) {
                // パスや画像処理の調整
                searchResults.forEach(meishi -> {
                    try {
                        // 表面画像パスの設定
                        if (meishi.getPhotoomotePath() != null) {
                            String omoteFileName = meishi.getPhotoomotePath()
                                .substring(meishi.getPhotoomotePath().lastIndexOf("\\") + 1);
                            String omoteImagePath = "/images/" + omoteFileName;
                            meishi.setOmoteImagePath(omoteImagePath);
                        }

                        // 裏面画像パスの設定
                        if (meishi.getPhotouraPath() != null) {
                            String uraFileName = meishi.getPhotouraPath()
                                .substring(meishi.getPhotouraPath().lastIndexOf("\\") + 1);
                            String uraImagePath = "/images/" + uraFileName;
                            meishi.setUraImagePath(uraImagePath);
                        }
                    } catch (Exception e) {
                        System.err.println("Error during image path processing for Meishi ID: " + meishi.getId());
                        e.printStackTrace();
                    }
                });

                // 検索結果をセッションに保存
                session.setAttribute("searchResults", searchResults);

                // グループ化処理
                Map<String, List<MeishiEntity>> groupedResults = searchResults.stream()
                    .collect(Collectors.groupingBy(MeishiEntity::getCompanyname));

                model.addAttribute("groupedResults", groupedResults);
                return "meishi/searchResults"; // 検索結果がある場合
            } else {
            	return "meishi/emptyMeishi"; // 検索結果がない場合
            }
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "検索中にエラーが発生しました。");
            return "meishi/emptyMeishi"; // 検索結果がない場合
        }
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

            // idの値をログに出力
            System.out.println("修正確認画面：リクエストから取得したid: " + id);
            
         // セッションにidを保存
            session.setAttribute("id", id);
            System.out.println("修正確認画面：セッションに保存したid: " + id);
            
         // セッションにMeishiFormを保存
            session.setAttribute("meishiForm", meishiForm);
            System.out.println("修正確認画面：セッションに保存したmeishiForm: " + meishiForm);

            // セッションから既存のパスを取得
            String photoomotePath = (String) session.getAttribute("photoomotePath");
            String photouraPath = (String) session.getAttribute("photouraPath");

            // セッションから取得したパスもログに出力
            System.out.println("修正確認画面：セッションから取得したphotoomotePath: " + photoomotePath);
            System.out.println("修正確認画面：セッションから取得したphotouraPath: " + photouraPath);

            // ファイル名を抽出
            String photoomoteFileName = (photoomotePath != null && !photoomotePath.isEmpty())
                    ? photoomotePath.substring(Math.max(photoomotePath.lastIndexOf("/"), photoomotePath.lastIndexOf("\\")) + 1)
                    : null;
            System.out.println("修正確認画面 - 抽出したphotoomoteFileName: " + photoomoteFileName);

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

                // 取得した名刺情報をログに出力
                System.out.println("修正確認画面：データベースから取得した名刺情報: " + meishi);

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
        
        
        
        @PostMapping("/fixMeishiComplete")
        public String fixMeishiComplete(HttpSession session, Model model) {
        	
        	// セッションからidを取得
            Integer id = (Integer) session.getAttribute("id");
            if (id == null) {
                System.out.println("修正完了画面：セッションから取得したidはnullです");
                model.addAttribute("message", "IDが見つかりません。もう一度お試しください。");
                return "/meishi/registerMeishi";
            }
            
            System.out.println("修正完了画面：セッションから取得したid: " + id);

            // セッションからフォームデータを取得
            MeishiForm meishiForm = (MeishiForm) session.getAttribute("meishiForm");
            String photoomotePath = (String) session.getAttribute("photoomotePath");
            String photouraPath = (String) session.getAttribute("photouraPath");

            // セッションデータをログに出力
            System.out.println("修正完了画面：セッションから取得したmeishiForm: " + meishiForm);
            System.out.println("修正完了画面：セッションから取得したphotoomotePath: " + photoomotePath);
            System.out.println("修正完了画面：セッションから取得したphotouraPath: " + photouraPath);

            if (meishiForm == null) {
                System.out.println("修正完了画面：meishiForm is null");
                model.addAttribute("message", "フォームデータが見つかりません。もう一度お試しください。");
                return "/meishi/registerMeishi";
            }

            if (photoomotePath == null) {
                System.out.println("修正完了画面：photoomotePath is null");
                model.addAttribute("message", "表面のファイルパスが見つかりません。もう一度お試しください。");
                return "/meishi/registerMeishi";
            }

            try {
                // meishiFormにパスをセット
                meishiForm.setPhotoomotePath(photoomotePath);
                if (photouraPath != null) {
                    meishiForm.setPhotouraPath(photouraPath);
                }

                // 登録する名刺データをログ出力
                System.out.println("修正完了画面：登録するmeishiForm: " + meishiForm);
                meishiService.saveMeishi(meishiForm, photoomotePath, photouraPath);
            } catch (Exception e) {
                System.out.println("修正完了画面：Error occurred while saving the files: " + e.getMessage());
                model.addAttribute("message", "ファイルの保存中にエラーが発生しました。もう一度お試しください。");
                return "/meishi/registerMeishi";
            }

            return "/meishi/fixMeishiComplete";
        }

 
        
        
 //-------名寄せ機能-----------------
        
        @GetMapping("/nayose")
        public String showDuplicateMeishi(Model model) {
            // 復号化された全名刺データを取得
            List<MeishiEntity> decryptedMeishiList = meishiService.getAllDecryptedMeishis();

            // 重複登録されている担当者を抽出
            List<MeishiEntity> duplicateMeishi = decryptedMeishiList.stream()
                    .collect(Collectors.groupingBy(MeishiEntity::getPersonalkananame))
                    .values().stream()
                    .filter(list -> list.size() > 1) // 重複データのみフィルタ
                    .flatMap(List::stream) // フラットなリストに変換
                    .collect(Collectors.toList());

            // 画像パスを生成してセット
            for (MeishiEntity meishi : duplicateMeishi) {
                try {
                    if (meishi.getPhotoomotePath() != null) {
                        // パスからファイル名を抽出
                        String omoteFileName = meishi.getPhotoomotePath().substring(meishi.getPhotoomotePath().lastIndexOf("\\") + 1);
                        String omoteImagePath = "/images/" + omoteFileName; // 静的リソースのパスに変換
                        meishi.setOmoteImagePath(omoteImagePath);
                    }
                } catch (Exception e) {
                    System.err.println("Error during photoomotePath processing: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // モデルにデータを追加
            model.addAttribute("duplicateMeishi", duplicateMeishi);

            // nayose.htmlを表示
            return "/meishi/nayose";
        }

        

 //---------CSV出力機能---------------------
      
         @GetMapping("/output_csv")
	 public void exportCsv(HttpServletResponse response, HttpSession session) {
	     response.setContentType("text/csv; charset=UTF-8");
	     response.setHeader("Content-Disposition", "attachment; filename=\"filtered_meishi.csv\"");
	
	     try (PrintWriter writer = response.getWriter()) {
	         writer.write("\uFEFF"); // BOMを追加して文字化けを防止
	
	         @SuppressWarnings("unchecked")
	         List<MeishiEntity> searchResults = (List<MeishiEntity>) session.getAttribute("searchResults");
	
	         // セッションからデータが正しく取得できているか確認
	         if (searchResults == null || searchResults.isEmpty()) {
	             writer.write("検索結果がありません");
	             return;
	         }
	
	         // データをCSVに出力
	         meishiService.writeCsv(writer, searchResults);
	     } catch (Exception e) {
	         e.printStackTrace();
	         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	     }
	 }

        
        
//------PDF化：印刷用にPDF化する。情報詳細画面のPDF化のバージョン------------------------
         @GetMapping("/downloadPdf")
         public void downloadPdf(HttpServletResponse response,
                                 @RequestParam("id") int id) {

             String pgpassword = "P4ssW0rd"; // サーバー側で鍵を保持
             try {
                 byte[] pdfData = pdfService.generatePdf(id, pgpassword);
                 response.setContentType("application/pdf");
                 response.setHeader("Content-Disposition", "attachment; filename=\"detailperson.pdf\"");
                 response.getOutputStream().write(pdfData);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }



//--------PDF化：検索結果後に表示されている画面のPDF化バージョン------
         
         @GetMapping("/print_pdf")
         public void generateSearchResultPdf(HttpServletResponse response, HttpSession session) {
             String pgpassword = "P4ssW0rd"; // サーバー側で鍵を保持

             try {
                 // セッションから検索結果を取得
                 List<MeishiEntity> searchResults = (List<MeishiEntity>) session.getAttribute("searchResults");

                 if (searchResults == null || searchResults.isEmpty()) {
                     throw new IllegalStateException("検索結果が存在しません。");
                 }

                 // PDFデータを生成
                 byte[] pdfData = pdfService.generateMultiResultPdf(searchResults, pgpassword);

                 // HTTPレスポンスにPDFデータを設定
                 response.setContentType("application/pdf");
                 response.setHeader("Content-Disposition", "attachment; filename=\"search_results.pdf\"");
                 response.getOutputStream().write(pdfData);
             } catch (IOException | IllegalStateException e) {
                 e.printStackTrace();
                 try {
                     response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PDF生成中にエラーが発生しました: " + e.getMessage());
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
             }
         }



 

        
        
    
}


    
    
    	