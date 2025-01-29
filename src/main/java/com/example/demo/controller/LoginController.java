package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.UserForm;
import com.example.demo.service.UserService;

@Controller
public class LoginController {

	//UserServiceクラスをインジェクションする
	@Autowired
	UserService userService;
	
	//ログイン画面に遷移する
	@GetMapping("/")
	public String loginForm() {
		return "/login/login";
	}
	
	//認証失敗時、ログイン画面に遷移し、エラーメッセージを表示する
	@GetMapping(value="/login", params = "error")
	public String loginError(Model model) {
		model.addAttribute("ErrorMessage", "ユーザー名またはパスワードが間違っています。");
		return "/login/login";
	}
	
	// 認証成功時、トップ画面に遷移し、ログインしているユーザ名を取得
    @GetMapping("/success")
    public String loginSuccess(Model model) {
        // トップ画面に遷移
        return "/top";
    }
    
	@GetMapping("/logout")
	public String logout() {
		return "/logout";
	}
	
	@GetMapping("/afterlogout")
	public String afterlogout() {
		return "/login/logout";
	}
	
	
	
	//新規登録画面に遷移する
	@GetMapping("/new/register")
    public String registrationForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "/login/register";
    }
    
	
//	//確認画面に遷移する
//	//入力内容にエラーがあれば、ユーザー登録画面に遷移し、エラーメッセージを表示する
	@PostMapping("/new/confirm")
    public String confirmForm(@Validated UserForm userForm, BindingResult result, Model model) {
        if (result.hasErrors()) {      	
            // バリデーションエラーがある場合、入力画面に戻ります。
            return "/login/register";
          //ユーザー名が重複してないかチェックする
        } else if(!userService.userExistsCheck(userForm.getUsername())) {
			return "/login/confirm";
		 }else {
		 model.addAttribute("errorMessage","同じユーザー名は登録できません");
	     System.out.println("エラーメッセージ: " + "同じユーザー名は登録できません");
       return "/login/register";
   }
}
	
	// ユーザー情報をデータベースに登録し、登録完了画面に遷移する
    @PostMapping("/new/complete")
    public String complete(@ModelAttribute("userForm") UserForm userForm,
    						Model model) {
    	
    	userService.register(userForm);
        model.addAttribute("username", userForm.getUsername());
    	
		/* // データベースにユーザー情報を登録
		userService.register(userForm);*/
        // 登録が成功したらログイン画面にリダイレクト
        return "/login/complete";
    }
}
