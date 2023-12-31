package com.ps20652.DATN.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ps20652.DATN.entity.Category;
import com.ps20652.DATN.entity.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {
	 List<Product> findByName(String name);
     Page<Product> findByName(String name, Pageable pageable);
     @Query("SELECT o FROM Product o WHERE o.name LIKE %:keyword% ")
     Page<Product> findByKeywordPaginated(String keyword, Pageable pageable);
	 @Query("SELECT o FROM Product o WHERE o.price BETWEEN ?1 AND ?2")
   	List<Product> findByPrice(double minPrice, double maxPrice);
	 
    List<Product> findByCategoryCategoryId(int categoryId);
    
    List<Product> findByCategory(Category category);
    
    @Modifying
    @Query("UPDATE Product p SET p.quantityInStock = p.quantityInStock + :quantityAdded WHERE p.productId = :productId")
    void updateQuantityInStock(@Param("productId") Integer productId, @Param("quantityAdded") int quantityAdded);
    
     Page<Product> findByCategoryCategoryId(int categoryId, Pageable pageable);

     List<Product> findAllByOrderByPriceAsc();

      Page<Product> findAllByOrderByPriceAsc(Pageable pageable);

      Page<Product> findAllByOrderByPriceDesc(Pageable pageable);

      Page<Product> findByCategoryCategoryIdOrderByPriceAsc(Integer categoryId, Pageable pageable);

       Page<Product> findByCategoryCategoryIdOrderByPriceDesc(Integer categoryId, Pageable pageable);

       @Query("SELECT COUNT(p) FROM Product p")
        int countTotalProducts();
      
        @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId")
        int countProductsByCategoryId(@Param("categoryId") int categoryId);
}