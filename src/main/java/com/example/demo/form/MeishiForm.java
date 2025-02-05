package com.example.demo.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MeishiForm {
	
	@NotBlank(message = "※企業名を入力してください。")
	public String companyname;
	
	@NotBlank(message = "※企業名(カナ）を入力してください。")
	@Pattern(regexp = "/\\A[ァ-ヴー]+\\z/u\n", message = "企業名（カナ）は全角カタカナで入力して下さい。")
	public String companykananame;
	
	@NotBlank(message = "※担当者名を入力してください。")
	public byte[] personalname;  //bytea型
	
	@NotBlank(message = "※担当者名(カナ）を入力してください。")
	@Pattern(regexp = "/\\A[ァ-ヴー]+\\z/u\n", message = "担当者名（カナ）は全角カタカナで入力して下さい。")
	public byte[] personalkananame; //bytea型
	
	public String belong;
	
	public String position;
	
	@NotBlank(message = "※所在地を入力してください。")
	public String address;
	
	@NotBlank(message = "※会社電話番号を入力してください。")
	@Pattern(regexp = "/^[0-9]+$/\n", message = "会社電話番号は半角数字で入力して下さい。")
	public String companytel;
	
	@Pattern(regexp = "/^[0-9]+$/\n", message = "携帯電話番号は半角数字で入力して下さい。")
	public byte[] mobiletel; //bytea型
	
	@NotBlank(message = "※Eメールアドレスを入力してください。")
	@Pattern(regexp = "[a-zA-Z0-9]*", message = "Eメールアドレスは半角英数字で入力して下さい。")
	public byte[] email;  //bytea型
	
	@NotBlank(message = "※名刺写真（表面）を選択してください。")
	public byte[] photoomote;  //bytea型
	
	public byte[] photoura;  //bytea型
	
	public String savedate;
	
	
}
