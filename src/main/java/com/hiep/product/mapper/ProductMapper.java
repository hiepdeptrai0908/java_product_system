package com.hiep.product.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.hiep.product.entity.ProductEntity;
import com.hiep.product.entity.ShoppingCartEntity;
import com.hiep.product.model.ProductModel;
import com.hiep.product.model.SearchModel;

@Mapper
public interface ProductMapper {

	int insertProduct(ProductModel productModel);

	List<ProductEntity> selectProduct();

	List<ProductEntity> detailProduct(int id);

	int addToCart(ProductModel productModel);

	List<ShoppingCartEntity> getCartProduct();

	int deleteItemCart(ProductModel productModel);

	int getPriceOnCart();
	
	int getAllHiepProductRecodeCount();
	
	List<ProductEntity> findListPaging(@Param("startIndex") int startIndex, 
			@Param("pageSize") int pageSize, @Param("data") String  data);

	int getManHiepProductRecodeCount(ProductModel productModel);

	List<ProductEntity> selectManProduct(int startIndex, int pageSize);

	int getWomanHiepProductRecodeCount(ProductModel productModel);

	List<ProductEntity> selectWomanProduct(int startIndex, int pageSize);

	List<ProductEntity> selectCheapPriceProduct(int startIndex, int pageSize);

	List<ProductEntity> selectHighPriceProduct(int startIndex, int pageSize);

	int getSearchHiepProductRecodeCount(SearchModel searchModel);

	List<ProductEntity> searchProduct(@Param("startIndex") int startIndex,
										@Param("pageSize") int pageSize,
										@Param("searchModel") SearchModel searchModel);

	int getAllHiepProductRecodeCountBySession(String data);

}
