package com.hiep.product.controller;

import java.io.File;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hiep.product.common.Pagination;
import com.hiep.product.entity.ProductEntity;
import com.hiep.product.entity.ShoppingCartEntity;
import com.hiep.product.mapper.ProductMapper;
import com.hiep.product.model.ProductModel;
import com.hiep.product.model.ProductUpload;
import com.hiep.product.model.SearchModel;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/")
// @SessionAttributes(types = UserForm.class)
public class MainController {

	@Autowired
	HttpSession session;

	@Autowired
	ProductMapper productMapper;

	@Value("${upload.path}")
	private String fileUpload;

	/**
	 * 商品追加
	 */
	@GetMapping("admin")
	public String admin(@ModelAttribute ProductUpload productUpload, Model model) {
		model.addAttribute("searchModel", new SearchModel());
		return "contents/create";
	}

	/**
	 * 初期表示,ページリンク
	 */
	@GetMapping("product")
	public String init(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
		String data = (String)session.getAttribute("data");
		log.info("session data------：  {}", data);
		
		int totalListCnt;
		if(!StringUtils.isBlank(data)) {
			SearchModel searchModel = new SearchModel();
			searchModel.setParam(data.toString());
			totalListCnt = productMapper.getAllHiepProductRecodeCountBySession(data);
//			session.invalidate();
		}else {
			totalListCnt = productMapper.getAllHiepProductRecodeCount();
		}
		
		// 総掲示物数と現在ページ
		Pagination pagination = new Pagination(totalListCnt, page);
		// DB接近スタートインデックス
		int startIndex = pagination.getStartIndex();

		// ページことに表示する掲示物最大数
		int pageSize = pagination.getPageSize();

		if(StringUtils.isBlank(data)) {
			data="";
		}
		// 掲示物取得
		List<ProductEntity> boardList = productMapper.findListPaging(startIndex, pageSize,data);

		model.addAttribute("listProducts", boardList);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("searchModel", new SearchModel());

		model.addAttribute("pagination", pagination);
		session.invalidate();

		return "contents/index";
	}

	/**
	 * Search product
	 */
	@PostMapping("product")
	public String doSearch(@RequestParam(defaultValue = "1") int page, SearchModel searchModel, Model model) {
		//session で保存データ
		session.setAttribute("data", searchModel.getParam());
		
		log.info("searchModel------：  {}", searchModel);
		int totalSearchListCnt = productMapper.getSearchHiepProductRecodeCount(searchModel);
		log.info("totalSearchListCnt------：  {}", totalSearchListCnt);

		Pagination pagination = new Pagination(totalSearchListCnt, page);

		int startIndex = pagination.getStartIndex();

		int pageSize = pagination.getPageSize();

		List<ProductEntity> searchProduct = productMapper.searchProduct(startIndex, pageSize, searchModel);
		log.info("searchProduct------：  {}", searchProduct);
		model.addAttribute("listProducts", searchProduct);
		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("pagination", pagination);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/index";
	}

	// Create Product
	/**
	 * 商品登録
	 */
	@PostMapping("doCreateProduct")
	public String doCreateProduct(@ModelAttribute("productUpload") ProductUpload productUpload, Model model) {
		log.info("multipartFile------：  {}", productUpload);
		/////////////////////////////////////////////// save image
		MultipartFile multipartFile = productUpload.getImage(); // multipartFile
		String fileName = multipartFile.getOriginalFilename(); // 元のファイル名
		try {
			FileCopyUtils.copy(productUpload.getImage().getBytes(), new File(this.fileUpload + fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		///////////////////////////////////////////////// date
		LocalDate getDate = LocalDate.now();
		int year = getDate.getYear();
		int month = getDate.getMonthValue();
		int day = getDate.getDayOfMonth();
		String date = year + " - " + month + " - " + day;

		////////////////////////////////////////////////// change model
		ProductModel productModel = new ProductModel();
		productModel.setName(productUpload.getName());
		productModel.setAge(productUpload.getAge());
		productModel.setSex(productUpload.getSex());
		productModel.setPrice(productUpload.getPrice());
		productModel.setDescription(productUpload.getDescription());
		productModel.setImage(fileName);

		productModel.setCreate_date(date);

		productMapper.insertProduct(productModel);

		model.addAttribute("searchModel", new SearchModel());

		return "redirect:/product";
	}

	/**
	 * Detail（詳細商品）
	 */
	@GetMapping("{id}")
	// @PostMapping("doDetail")
	public String doDetail(@PathVariable int id, Model model) {
		log.info("resulrereere------  {}", id);
		List<ProductEntity> detailProduct = productMapper.detailProduct(id);
		model.addAttribute("detailProduct", detailProduct);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/detail";
	}

	/**
	 * カート
	 */
	@PostMapping("doCart")
	public String doCart(ProductModel productModel, Model model) {
		log.info("detailllllllll----  {}", productModel);
		productMapper.addToCart(productModel);

		return "redirect:/backIndex";
	}

	@RequestMapping("backIndex")
	public String backIndex(Model model) {
		List<ProductEntity> getAllProduct = productMapper.selectProduct();
		model.addAttribute("listProducts", getAllProduct);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/index";
	}

	@RequestMapping("doSearchAll")
	public String doSearchAll() {
		return "redirect:/product";
	}

	@RequestMapping("doSearchMan")
	public String doSearchMan(@RequestParam(defaultValue = "1") int page, Model model) {
		ProductModel productModel = new ProductModel();
		productModel.setSex("男性");

		int totalListManCnt = productMapper.getManHiepProductRecodeCount(productModel);

		Pagination pagination = new Pagination(totalListManCnt, page);
		log.info("asdhfjsdhfjh---: {}", totalListManCnt);

		int startIndex = pagination.getStartIndex();

		// ページことに表示する掲示物最大数
		int pageSize = pagination.getPageSize();

		List<ProductEntity> searchMan = productMapper.selectManProduct(startIndex, pageSize);
		model.addAttribute("listProducts", searchMan);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("searchModel", new SearchModel());

		model.addAttribute("pagination", pagination);
		return "contents/index";
	}

	@RequestMapping("doSearchWoman")
	public String doSearchWoman(@RequestParam(defaultValue = "1") int page, Model model) {
		ProductModel productModel = new ProductModel();
		productModel.setSex("女性");

		int totalListWomanCnt = productMapper.getWomanHiepProductRecodeCount(productModel);

		Pagination pagination = new Pagination(totalListWomanCnt, page);

		int startIndex = pagination.getStartIndex();

		int pageSize = pagination.getPageSize();

		List<ProductEntity> searchWoman = productMapper.selectWomanProduct(startIndex, pageSize);
		model.addAttribute("listProducts", searchWoman);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("pagination", pagination);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/index";
	}

	@RequestMapping("cheapPrice")
	public String cheapPrice(@RequestParam(defaultValue = "1") int page, Model model) {
		int totalListCnt = productMapper.getAllHiepProductRecodeCount();

		Pagination pagination = new Pagination(totalListCnt, page);

		int startIndex = pagination.getStartIndex();

		int pageSize = pagination.getPageSize();

		List<ProductEntity> cheapPrice = productMapper.selectCheapPriceProduct(startIndex, pageSize);
		model.addAttribute("listProducts", cheapPrice);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("pagination", pagination);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/index";
	}

	@RequestMapping("highPrice")
	public String highPrice(@RequestParam(defaultValue = "1") int page, Model model) {

		int totalListCnt = productMapper.getAllHiepProductRecodeCount();

		Pagination pagination = new Pagination(totalListCnt, page);

		int startIndex = pagination.getStartIndex();

		int pageSize = pagination.getPageSize();

		List<ProductEntity> highPrice = productMapper.selectHighPriceProduct(startIndex, pageSize);
		model.addAttribute("listProducts", highPrice);

		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("pagination", pagination);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/index";
	}

	@RequestMapping("doViewCart")
	public String doViewCart(Model model) {
		List<ShoppingCartEntity> cartProduct = productMapper.getCartProduct();
		int getPrice = productMapper.getPriceOnCart();
		log.info("getPricellllllll----  {}", getPrice);

		model.addAttribute("totalPrice", getPrice);
		model.addAttribute("cartProduct", cartProduct);

		model.addAttribute("searchModel", new SearchModel());
		return "contents/view-cart";
	}

	@PostMapping("do-Delete-Item-Cart")
	public String doDeleteItemCart(ProductModel productModel) {
		productMapper.deleteItemCart(productModel);
		return "redirect:/doViewCart";
	}

}
