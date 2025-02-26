package com.example.demo.form;

	import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

	@Data
	public class MeishiForm {
		
		@NotBlank(message = "※企業名を入力してください。")
	    public String companyname;
	    
	    @NotBlank(message = "※企業名(カナ）を入力してください。")
		@Pattern(regexp = "^[ァ-ヴー]+$", message = "企業名（カナ）は全角カタカナで入力して下さい。")
	    public String companykananame;
	    
	    @NotBlank(message = "※担当者名を入力してください。")
	    public String personalname;  // String型に変更
	    
	    @NotBlank(message = "※担当者名(カナ）を入力してください。")
		@Pattern(regexp = "^[ァ-ヴー]+$", message = "担当者名（カナ）は全角カタカナで入力して下さい。")
	    public String personalkananame; // String型に変更
	    
	    public String belong;
	    
	    public String position;
	    
	    @NotBlank(message = "※所在地を入力してください。")
	    public String address;
	    
	    @NotBlank(message = "※会社電話番号を入力してください。")
		@Pattern(regexp = "^[0-9]+$", message = "会社電話番号は半角数字で入力して下さい。")
	    public String companytel;
	    
	    @Pattern(regexp = "^[0-9]*$", message = "携帯電話番号は半角数字で入力して下さい。")
	    public String mobiletel; // String型に変更
	    
	    @NotBlank(message = "※Eメールアドレスを入力してください。")
		@Pattern(regexp = "^[a-zA-Z0-9_.+-]+@([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]*\\.)+[a-zA-Z]{2,}$", message = "Eメールアドレスは半角英数字で入力して下さい。")
	    public String email;  // String型に変更
	    
	    @NotNull(message = "※名刺写真（表面）を選択してください。")
		/*public String photoomote;  // String型に変更
		*/    private MultipartFile photoomote;
	    
		/*public String photoura;  // String型に変更
		*/    private MultipartFile photoura;
	    
	    public String savedate;
		
	 // Getters and Setters
	    public String getCompanyname() {
	        return companyname;
	    }

	    public void setCompanyname(String companyname) {
	        this.companyname = companyname;
	    }

	    public String getCompanykananame() {
	        return companykananame;
	    }

	    public void setCompanykananame(String companykananame) {
	        this.companykananame = companykananame;
	    }

	    public String getPersonalname() {
	        return personalname;
	    }

	    public void setPersonalname(String personalname) {
	        this.personalname = personalname;
	    }

	    public String getPersonalkananame() {
	        return personalkananame;
	    }

	    public void setPersonalkananame(String personalkananame) {
	        this.personalkananame = personalkananame;
	    }

	    public String getBelong() {
	        return belong;
	    }

	    public void setBelong(String belong) {
	        this.belong = belong;
	    }

	    public String getPosition() {
	        return position;
	    }

	    public void setPosition(String position) {
	        this.position = position;
	    }

	    public String getAddress() {
	        return address;
	    }

	    public void setAddress(String address) {
	        this.address = address;
	    }

	    public String getCompanytel() {
	        return companytel;
	    }

	    public void setCompanytel(String companytel) {
	        this.companytel = companytel;
	    }

	    public String getMobiletel() {
	        return mobiletel;
	    }

	    public void setMobiletel(String mobiletel) {
	        this.mobiletel = mobiletel;
	    }

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }
	    
	    public MultipartFile getPhotoomote() {
	        return photoomote;
	    }

	    public void setPhotoomote(MultipartFile photoomote) {
	        this.photoomote = photoomote;
	    }

	    public MultipartFile getPhotoura() {
	        return photoura;
	    }

	    public void setPhotoura(MultipartFile photoura) {
	        this.photoura = photoura;
	    }

	    public String getSavedate() {
	        return savedate;
	    }

	    public void setSavedate(String savedate) {
	        this.savedate = savedate;
	    }
		
		
		/*@NotBlank(message = "※企業名を入力してください。")
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
		
		public String savedate;*/
		
		
	}