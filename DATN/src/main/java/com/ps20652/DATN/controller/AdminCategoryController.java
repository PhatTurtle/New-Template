package com.ps20652.DATN.controller;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ps20652.DATN.DTO.ProductDTO;
import com.ps20652.DATN.entity.Category;
import com.ps20652.DATN.entity.Order;
import com.ps20652.DATN.entity.Product;
import com.ps20652.DATN.entity.StockHistory;
import com.ps20652.DATN.service.CategoryService;
import com.ps20652.DATN.service.ProductService;
import com.ps20652.DATN.service.UploadService;

@Controller
@RequestMapping("/admin/category")
public class AdminCategoryController {

	@Autowired
	private UploadService uploadService;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@GetMapping
	public String listProducts( @RequestParam(name = "confirmationMessage", required = false) String confirmationMessage  ,Model model,
			Principal principal,  @RequestParam(name = "errorMessage", required = false) String errorMessage)  {


	
		// List<Product> products = productService.findAll();
		List<Category> categories = categoryService.findAll();

		if (confirmationMessage != null) {
            model.addAttribute("confirmationMessage", confirmationMessage);
        }

		if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
		
		model.addAttribute("categories", categories);
		return "AdminCpanel/ui-category";

	}

	@PostMapping("/addCategory")
	public String addCategory(@RequestParam("name") String name , RedirectAttributes redirectAttributes) {
		

			Category category = new Category();

			category.setName(name);

			// Lưu sản phẩm vào cơ sở dữ liệu
			categoryService.create(category);
redirectAttributes.addFlashAttribute("confirmationMessage", "Thêm mới danh mục thành công");


			// Chuyển hướng đến trang danh sách sản phẩm sau khi thêm
			return "redirect:/admin/category";
		
	}

	@PostMapping("/deleteCategory/{categoryId}")
	public String deleteCategory(@PathVariable("categoryId") Integer id, RedirectAttributes redirectAttributes ) {
		



		Category category = categoryService.findbyId(id);
			
		try{
			categoryService.delete(category);
			redirectAttributes.addFlashAttribute("confirmationMessage", "Xóa sản danh mục thành công");

		}catch(Exception e){
			redirectAttributes.addFlashAttribute("errorMessage", "Không được xóa vì đã có sản phẩm chứa loại này");
		}

			// Lưu sản phẩm vào cơ sở dữ liệu
			

			// Chuyển hướng đến trang danh sách sản phẩm sau khi thêm
			return "redirect:/admin/category";
		
	}

	@PostMapping("/edit/{categoryId}")
    public String handleEditProductForm(@PathVariable("categoryId") Integer categoryId,
                                        @ModelAttribute("category") Category updatedCategory,  RedirectAttributes redirectAttributes) {
        Category category = categoryService.findbyId(categoryId);
        if (category != null) {
            category.setName(updatedCategory.getName()); // Cập nhật thông tin mới của danh mục sản phẩm
            categoryService.update(category); // Gọi service để cập nhật vào cơ sở dữ liệu
			redirectAttributes.addFlashAttribute("confirmationMessage", "Cập nhật tên danh mục thành công");
        }
        return "redirect:/admin/category";
    }

}
