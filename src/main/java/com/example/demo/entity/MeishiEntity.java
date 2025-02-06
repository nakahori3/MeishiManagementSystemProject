package com.example.demo.entity;

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
	
	
}
