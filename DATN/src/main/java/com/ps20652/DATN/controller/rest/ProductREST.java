package com.ps20652.DATN.controller.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Category;
import com.ps20652.DATN.entity.Product;
import com.ps20652.DATN.service.ProductService;

@RestController
@RequestMapping("/api")
public class ProductREST {

	@Autowired 
	ProductService p_service;
	
	@GetMapping("/product")
	public List<Product> getProduct() {
		return p_service.findAll();
	}
	@PostMapping("/product/create")
	public Product createe(@RequestBody Product pro) {
		return p_service.create(pro);
	}
	@GetMapping("/product/{id}")
	public Product getOne(@PathVariable Integer id) {
		return p_service.findbyId(id);
	}
	@PutMapping("/product/{id}")
	public Product update(@RequestBody Product product,@PathVariable("id") Integer id) {
		return p_service.update(product);
	}
	@DeleteMapping("/product/delete/{id}")
	public void delete(@PathVariable("id")Product id) {
		p_service.delete(id);
	}
	  @GetMapping("/products/searchByName")
	    public List<Product> searchProductsByName(@RequestParam String name) {
	        return p_service.findByName(name);
    }
	    @GetMapping("/product/searchByPrice")
	    public List<Product> searchProductsByPrice(
	        @RequestParam("minPrice") double minPrice,
	        @RequestParam("maxPrice") double maxPrice) {
	        return p_service.findByPrice(minPrice, maxPrice);
    }
	    @GetMapping("/products/category/{categoryId}")
		 public ResponseEntity<List<Product>> listProductsCategory(@PathVariable Integer categoryId,@RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "pageSize", defaultValue = "8") int pageSize) {
        try {
            Page<Product> productPage = p_service.findByCategoryCategoryId(categoryId,PageRequest.of(page, pageSize));
            List<Product> products = productPage.getContent();

            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Thêm logic xử lý dữ liệu khác nếu cần

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	   @GetMapping("/products/list")
    public ResponseEntity<List<Product>> listProducts(@RequestParam(name = "page", defaultValue = "0") int page,
                                                      @RequestParam(name = "pageSize", defaultValue = "8") int pageSize) {
        try {
            Page<Product> productPage = p_service.getAllOrdersPaginated(PageRequest.of(page, pageSize));
            List<Product> products = productPage.getContent();

            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            // Thêm logic xử lý dữ liệu khác nếu cần

            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	//   @GetMapping("/products/sort/low-to-high")
    // public ResponseEntity<List<Product>> getProductsSortedByLowToHigh() {
    //     List<Product> sortedProducts = p_service.getProductsSortedByLowToHigh();
    //     return ResponseEntity.ok(sortedProducts);
    // }
	@GetMapping("/products/sort/low-to-high")
public ResponseEntity<Page<Product>> getProductsSortedByLowToHigh(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                  @RequestParam(name = "pageSize", defaultValue = "12") int pageSize) {
    try {
        Page<Product> sortedProductPage = p_service.getProductsSortedByLowToHighPaginated(PageRequest.of(page, pageSize));
        
        if (sortedProductPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Thêm logic xử lý dữ liệu khác nếu cần

        return new ResponseEntity<>(sortedProductPage, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@GetMapping("/products/sort/high-to-low")
public ResponseEntity<Page<Product>> getProductsSortedByHighToLow(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                  @RequestParam(name = "pageSize", defaultValue = "12") int pageSize) {
    try {
        Page<Product> sortedProductPage = p_service.getProductsSortedByHighToLowPaginated(PageRequest.of(page, pageSize));
        
        if (sortedProductPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Thêm logic xử lý dữ liệu khác nếu cần

        return new ResponseEntity<>(sortedProductPage, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


@GetMapping("/products/category/{categoryId}/sort/low-to-high")
public Page<Product> sortProductsByLowToHighForCategory(@PathVariable Integer categoryId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "8") int pageSize) {
    return p_service.getProductsSortedByLowToHighForCategory(categoryId, PageRequest.of(page, pageSize));
}

@GetMapping("/products/category/{categoryId}/sort/high-to-low")
public Page<Product> sortProductsHighToLowForCategory(@PathVariable Integer categoryId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "8") int pageSize) {
    return p_service.getProductsSortedByHighToLowForCategory(categoryId, PageRequest.of(page, pageSize));
}

}

