package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {
	
	@NotBlank(message = "※ユーザー名を入力して下さい")
	@Pattern(regexp = "[a-zA-Z0-9]*", message = "ユーザー名は半角英数字で入力して下さい。")
	private String username;
	
	@NotBlank(message = "※パスワードを入力して下さい")
	@Pattern(regexp = "[a-zA-Z0-9]*", message = "パスワードは半角英数字で入力して下さい。")
	private String password;

}
