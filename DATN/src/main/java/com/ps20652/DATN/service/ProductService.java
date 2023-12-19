package com.ps20652.DATN.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ps20652.DATN.entity.Order;
import com.ps20652.DATN.entity.Product;


public interface ProductService {

	
	public List<Product> getTop4BestSellingProducts();

	public List<Product> getEightProducts();

	public List<Product> findAll();

	public Product create(Product pro);

	public Product findbyId(Integer id);
	
	public Product findbyIdPro(Product product);
	
	Product update(Product product);

	void delete(Product id);
	
	List<Product> findByName(String name);
	public Page<Product> findByKeywordPaginated(String keyword, Pageable pageable);
	
	List<Product> findByPrice(double minPrice, double maxPrice);
	
	List<Product> findByCategoryCategoryId(int categoryId);
	
	// public List<Product> getTop4BestSellingProductsPerCategory(int categoryId);
	
	 Page<Product> getAllOrdersPaginated(PageRequest pageRequest);
	 
	 void updateQuantityInStock(Integer productId, int quantityAdded);
	 
	 public Page<Product> getAllProductCategory(int categoryId, int page, int size);

	 Page<Product> findByNamePaginated(String productName, Pageable pageable);

	 public List<Product> getProductsSortedByLowToHigh();

	 public Page<Product> getProductsSortedByLowToHighPaginated(Pageable pageable);

	 public Page<Product> getProductsSortedByHighToLowPaginated(Pageable pageable);

	 Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable);

	 public Page<Product> getProductsSortedByLowToHighForCategory(Integer categoryId, Pageable pageable);

	 public Page<Product> getProductsSortedByHighToLowForCategory(Integer categoryId, Pageable pageable);

	 public int getTotalProducts();

	 public int getTotalProductsByCategory(int categoryId);
}