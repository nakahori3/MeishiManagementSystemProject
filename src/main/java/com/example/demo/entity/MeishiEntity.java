package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "photoomote")
    private String photoomote;

    @Column(name = "photoura")
    private String photoura;

    @Column(name = "savedate")
    private String savedate;

    // Getters and Setters
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

    public String getPhotoomote() {
        return photoomote;
    }

    public void setPhotoomote(String photoomote) {
        this.photoomote = photoomote;
    }

    public String getPhotoura() {
        return photoura;
    }

    public void setPhotoura(String photoura) {
        this.photoura = photoura;
    }

    public String getSavedate() {
        return savedate;
    }

    public void setSavedate(String savedate) {
        this.savedate = savedate;
    }
}








/*package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
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
	
	@Column(name="companyname")
	private String companyname;
	
	@Column(name="companykananame")
	private String companykananame;
	
	@Lob
	@Column(name="personalname")
	private byte[] personalname; //bytea型
	
	@Lob
	@Column(name="personalkananame")
	private byte[] personalkananame;  //bytea型
	
	@Column(name="belong")
	private String belong;
	
	@Column(name="position")
	private String position;
	
	@Column(name="address")
	private String address;
	
	@Column(name="companytel")
	private String companytel;
	
	@Lob
	@Column(name="mobiletel")
	private byte[] mobiletel;  //bytea型
	
	@Lob
	@Column(name="email")
	private byte[] email;  //bytea型
	
	@Lob
	@Column(name="photoomote")
	private byte[] photoomote;  //bytea型
	
	@Lob
	@Column(name="photoura")
	private byte[] photoura;  //bytea型
	
	@Column(name="savedate")
	public String savedate;
	
	
	// Getters and Setters
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

    public byte[] getPersonalname() {
        return personalname;
    }

    public void setPersonalname(byte[] personalname) {
        this.personalname = personalname;
    }

    public byte[] getPersonalkananame() {
        return personalkananame;
    }

    public void setPersonalkananame(byte[] personalkananame) {
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

    public byte[] getMobiletel() {
        return mobiletel;
    }

    public void setMobiletel(byte[] mobiletel) {
        this.mobiletel = mobiletel;
    }

    public byte[] getEmail() {
        return email;
    }

    public void setEmail(byte[] email) {
        this.email = email;
    }

    public byte[] getPhotoomote() {
        return photoomote;
    }

    public void setPhotoomote(byte[] photoomote) {
        this.photoomote = photoomote;
    }

    public byte[] getPhotoura() {
        return photoura;
    }

    public void setPhotoura(byte[] photoura) {
        this.photoura = photoura;
    }

    public String getSavedate() {
        return savedate;
    }

    public void setSavedate(String savedate) {
        this.savedate = savedate;
    }
	
}*/
