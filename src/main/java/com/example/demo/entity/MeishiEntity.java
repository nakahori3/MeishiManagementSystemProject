package com.example.demo.entity;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="meishis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeishiEntity {

    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
    private int id;

    @Column(name = "companyname")
    private String companyname;

    @Column(name = "companykananame")
    private String companykananame;

    @Column(name = "personalname")
    private String personalname;

    @Column(name = "personalkananame")
    private String personalkananame;

    @Column(name = "belong")
    private String belong;

    @Column(name = "position")
    private String position;

    @Column(name = "address")
    private String address;

    @Column(name = "companytel")
    private String companytel;

    @Column(name = "mobiletel")
    private String mobiletel;

    @Column(name = "email")
    private String email;

    @Column(name = "photoomotePath")
    private String photoomotePath;
  
    @Column(name = "photouraPath")
    private String photouraPath;

    @Column(name = "savedate")
    private String savedate;
    
    
  //2025年3月3日　画像パスから画像ファイル名を取り出すために作成した箇所
    @Transient
    private String omoteImagePath; // 一時的なフィールドとして追加
 
  //2025年3月5日　画像パスから画像ファイル名を取り出すために作成した箇所
    @Transient
    private String uraImagePath; // 一時的なフィールドとして追加

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getPhotoomotePath() {
        return photoomotePath;
    }

    public void setPhotoomotePath(String photoomotePath) {
        this.photoomotePath = photoomotePath;
    }

    public String getPhotouraPath() {
        return photouraPath;
    }

    public void setPhotouraPath(String photouraPath) {
        this.photouraPath = photouraPath;
    }

    public String getSavedate() {
        return savedate;
    }

    public void setSavedate(String savedate) {
        this.savedate = savedate;
    }
    
    //2025年3月3日　画像パスから画像ファイル名を取り出すために作成した箇所
    public String getOmoteImagePath() {
        return omoteImagePath;
    }

    public void setOmoteImagePath(String omoteImagePath) {
        this.omoteImagePath = omoteImagePath;
    }
  
    //2025年3月5日　画像パスから画像ファイル名を取り出すために作成した箇所
    public String getUraImagePath() {
        return uraImagePath;
    }

    public void setUraImagePath(String uraImagePath) {
        this.uraImagePath = uraImagePath;
    }
    
}