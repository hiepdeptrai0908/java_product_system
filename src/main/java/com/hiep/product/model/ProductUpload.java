package com.hiep.product.model;

import javax.persistence.GeneratedValue;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
@Table(name="hiep_product")
public class ProductUpload {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private int id;
	private String name;
	private String age;
	private String sex;
	private int price;
	
	private MultipartFile image;
	
	private String description;
	private String create_date;
	private String update_date;
}
