package com.ps20652.DATN.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ps20652.DATN.dao.ProductDAO;
import com.ps20652.DATN.dao.StockHistoryDAO;
import com.ps20652.DATN.entity.Category;
import com.ps20652.DATN.entity.OrderDetail;
import com.ps20652.DATN.entity.Product;
import com.ps20652.DATN.entity.StockHistory;
import com.ps20652.DATN.service.CategoryService;
import com.ps20652.DATN.service.OrderDetailService;
import com.ps20652.DATN.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductDAO productDAO;
	@Autowired
	private CategoryService catService;
	@Autowired
	private OrderDetailService orderDetailService;
	@Autowired
	private StockHistoryDAO stockHistoryDAO;
	
	@Override
	public List<Product> findAll() {
		return productDAO.findAll();
	}

	@Override
	public Product create(Product product) {
		return productDAO.save(product);
	}

	@Override
	public Product findbyId(Integer id) {
		return productDAO.findById(id).get();
	}

	@Override
	public void delete(Product id) {
		productDAO.delete(id);
		
	}

	@Override
	public List<Product> findByName(String name) {
		return productDAO.findByName(name);
	}

	@Override
	public List<Product> findByPrice(double minPrice, double maxPrice) {
		
		return productDAO.findByPrice(minPrice, maxPrice);
	}

	@Override
	public Product update(Product product) {
		return productDAO.save(product);
	}

	@Override
	public List<Product> findByCategoryCategoryId(int categoryId) {
	
		return productDAO.findByCategoryCategoryId(categoryId);
	}

	@Override
	public Product findbyIdPro(Product product) {
		return productDAO.findById(product.getProductId()).get();
	}

		
	

//	// Tính tổng số lượng đã bán của một sản phẩm
	private int getTotalUnitsSold(Product product) {
	    List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByProduct(product);
	    int totalUnitsSold = orderDetails.stream().mapToInt(OrderDetail::getQuantity).sum();
//	    System.out.println("Total units sold for Product ID " + product.getProductId() + ": " + totalUnitsSold);
	    return totalUnitsSold;
	}

// 	@Override
// 	public List<Product> getTop4BestSellingProductsPerCategory(int categoryId) {
// 	    List<Product> top4ProductsPerCategory = new ArrayList<>();
// 	    Category category = catService.findbyId(categoryId); // Sử dụng service để tìm danh mục bằng ID

// 	    if (category != null) {
// 	        List<Product> productsInCategory = productDAO.findByCategory(category);

// 	        // Sắp xếp sản phẩm trong danh mục theo số lượng đã bán
// 	        productsInCategory.sort((p1, p2) -> {
// 	            int unitsSold1 = getTotalUnitsSold(p1);
// 	            int unitsSold2 = getTotalUnitsSold(p2);
// 	            return Integer.compare(unitsSold2, unitsSold1);
// 	        });
	        
// //	        productsInCategory.forEach(product -> {
// //	            System.out.println("Product ID: " + product.getProductId() + product.getCategory().getName() + ", Units Sold: " + getTotalUnitsSold(product));
// //	        });

// 	        // Lấy top 4 sản phẩm của danh mục và thêm vào danh sách chứa top 4 sản phẩm
// 	        top4ProductsPerCategory = productsInCategory.stream().limit(4).collect(Collectors.toList());
// 	    }

// 	    return top4ProductsPerCategory;
// 	}

@Override
public List<Product> getTop4BestSellingProducts() {
    List<Product> top4Products = new ArrayList<>();

    // Lấy tất cả sản phẩm từ DAO hoặc Service
    List<Product> allProducts = productDAO.findAll(); // Thay bằng phương thức lấy tất cả sản phẩm từ DAO hoặc Service của bạn

    // Sắp xếp tất cả sản phẩm theo số lượng đã bán
    allProducts.sort((p1, p2) -> {
        int unitsSold1 = getTotalUnitsSold(p1);
        int unitsSold2 = getTotalUnitsSold(p2);
        return Integer.compare(unitsSold2, unitsSold1);
    });

    // Lấy top 8 sản phẩm bán chạy nhất và thêm vào danh sách top8Products
    top4Products = allProducts.stream().limit(4).collect(Collectors.toList());

    return top4Products;
}


	@Override
	public Page<Product> getAllOrdersPaginated(PageRequest pageRequest) {
	
		return productDAO.findAll(pageRequest);
	}

	 @Override
	    @Transactional
	    public void updateQuantityInStock(Integer productId, int quantityAdded) {
	        // Cập nhật số lượng trong kho
	        Product product = productDAO.findById(productId).orElse(null);
	        if (product != null) {
	
	            productDAO.updateQuantityInStock(productId, quantityAdded);
	            productDAO.save(product);
	            
	            // Thêm lịch sử nhập hàng
	            StockHistory stockHistory = new StockHistory();
	            stockHistory.setProduct(product);
	            stockHistory.setQuantityAdded(quantityAdded);
	            stockHistory.setEntryDate(new Date());
	            stockHistoryDAO.save(stockHistory);
	        }
	    }

		public List<Product> getEightProducts() {
			Pageable pageable = PageRequest.of(0, 8); // Lấy 8 sản phẩm đầu tiên
			return productDAO.findAll(pageable).getContent();
		}

		@Override
		public Page<Product> getAllProductCategory(int categoryId, int page, int size) {
			PageRequest pageRequest = PageRequest.of(page, size);
			return productDAO.findByCategoryCategoryId(categoryId, pageRequest);
		}

		@Override
		public Page<Product> findByNamePaginated(String productName, Pageable pageable) {
			return productDAO.findByName(productName, pageable);
		}

		@Override
		public List<Product> getProductsSortedByLowToHigh() {
			// Thực hiện lấy dữ liệu từ cơ sở dữ liệu đã sắp xếp từ thấp đến cao
			// Ví dụ:
			return productDAO.findAllByOrderByPriceAsc();
			// hoặc thực hiện logic sắp xếp ở đây
		}

		@Override
		public Page<Product> getProductsSortedByLowToHighPaginated(Pageable pageable) {
			return productDAO.findAllByOrderByPriceAsc(pageable);
		}

		@Override
		public Page<Product> getProductsSortedByHighToLowPaginated(Pageable pageable) {
			return productDAO.findAllByOrderByPriceDesc(pageable);
		}

		@Override
		public Page<Product> findByCategoryCategoryId(Integer categoryId, Pageable pageable) {
			return productDAO.findByCategoryCategoryId(categoryId, pageable);
		}

		@Override
		public Page<Product> getProductsSortedByLowToHighForCategory(Integer categoryId, Pageable pageable) {
			// TODO Auto-generated method stub
			return productDAO.findByCategoryCategoryIdOrderByPriceAsc(categoryId, pageable);
		}

		@Override
		public Page<Product> getProductsSortedByHighToLowForCategory(Integer categoryId, Pageable pageable) {
			// TODO Auto-generated method stub
			return productDAO.findByCategoryCategoryIdOrderByPriceDesc(categoryId, pageable);
		}

		@Override
		public int getTotalProducts() {
			return productDAO.countTotalProducts();
		}

		@Override
		public int getTotalProductsByCategory(int categoryId) {
			return productDAO.countProductsByCategoryId(categoryId);
		}
}
