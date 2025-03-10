package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.AuthorityEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MeishiService;
import com.example.demo.service.UserService;

@Controller
public class AdminChangeController {
	
	@Autowired
    UserService userService;

    @Autowired
    MeishiService meishiService;

    @Autowired
    UsersRepository usersRepository;

    
 // ユーザー情報確認画面
    
	@GetMapping("/adminChange")
	public String adminChange(@AuthenticationPrincipal UserDetails userDetails, Model model) {
	    System.out.println("adminChangeページに到達");
	
	    // 管理者権限があるか確認
	    if (userDetails.getAuthorities().stream()
	            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
	        System.out.println("管理者権限あり - authorityListを準備中");
	
	        // ユーザー権限リストを取得
	        List<AuthorityEntity> authorityList = userService.getUserAuthList();
	        model.addAttribute("authorityList", authorityList);
	        return "adminChange"; // 修正：スラッシュを外す
	    }
	
	    // 一般ユーザーの場合の処理
	    System.out.println("管理者権限なし - usersListを準備中");
	    List<UserEntity> usersList = meishiService.makeUsersList();
	    model.addAttribute("usersList", usersList);
	    return "adminChange"; // 修正：スラッシュを外す
	}

    
	@PostMapping("/delete_user")
	public String deleteUser(@RequestParam(name = "username") String username, Model model) {
	    System.out.println("削除対象のユーザー名: " + username);

	    // ユーザー削除処理
	    userService.deleteUserByUsername(username);

	    // 更新後のリストを取得して画面に反映
	    List<AuthorityEntity> authorityList = userService.getUserAuthList();
	    model.addAttribute("authorityList", authorityList);

	    return "adminChange"; // adminChange画面にリダイレクト
	}

    
    
    
    @PostMapping("/change_admin")
    public String changeAdmin(@RequestParam(name = "username") String username,
                              @RequestParam(name = "authority") String authority,
                              Model model) {
        System.out.println("Received username: " + username);
        System.out.println("Received authority: " + authority);

        // 以下のロジックはそのまま
        if (authority.equals("ROLE_GENERAL")) {
            userService.updateAuthorityToAdmin(username);
            System.out.println("Changed to ADMIN");
            return "redirect:/adminChange";
        } else {
            userService.updateAuthorityToGeneral(username);
            System.out.println("Changed to GENERAL");
            return "redirect:/adminChange";
        }
    }

 
	/*  @PostMapping("/change_admin")
	  public String changeAdmin(@RequestParam(name = "username") String username,
			  					@RequestParam(name = "authority") String authority,
			  					Model model) {
		  System.out.println("change admin");
		  if(authority.equals("ROLE_GENERAL")){
	  // 権限をADMINに変更
	  userService.updateAuthorityToAdmin(username);
	  List<UserEntity> usersList = userService.makeUsersList();
	  model.addAttribute("usersList", usersList);
	  System.out.println("管理者");
	  	return "redirect:/adminChange";
	  	
		  }else {
			// 権限をGENERALに変更
		      userService.updateAuthorityToGeneral(username);
		      List<UserEntity> usersList = userService.makeUsersList();
		      model.addAttribute("usersList", usersList);
		      // ユーザー情報確認画面に遷移
		      System.out.println("ユーザー");
		      return "redirect:/adminChange";
		  }
	  }*/

}
