package com.hiep.product.entity;

import lombok.Data;

@Data
public class ProductEntity {
	private int id;
	private String name;
	private String age;
	private String sex;
	private String price;
	private String image;
	private String description;
	private String create_date;
	private String update_date;
}
