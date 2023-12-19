package com.ps20652.DATN.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ps20652.DATN.entity.Revenue;
import com.ps20652.DATN.entity.StockHistory;
import com.ps20652.DATN.service.HistoryStockService;
import com.ps20652.DATN.service.RevenueService;
import com.ps20652.DATN.utils.DateUtils;

@Controller
public class RevenueController {

    @Autowired
    private RevenueService revenueService;
    
    @Autowired
    private HistoryStockService historyService;
    
    
    // @GetMapping("/admin/dashboard")
    // public String showDashboard(Model model) {

     
    //     return "aaa/index"; // Trả về template Thymeleaf
    // }
    
    @GetMapping("/admin/revenue-chart")
    public String showRevenueChart(Model model) {
    	
    	

        List<Revenue> revenues = revenueService.getAllRevenues(); // Lấy danh sách doanh thu từ Service
        Map<String, Double> monthlyRevenues = new HashMap<>(); // tạo MAP lưu trữ doanh thu theo tháng.
        Map<String, Double> monthlyPurchasePrices = new HashMap<>();// 
        double totalRevenue = 0;

        for (Revenue revenue : revenues) {
            String month = DateUtils.extractMonthFromDate(revenue.getOrderDate()); // trích xuất tháng từ Date trong Revenue
 
            // Tính doanh thu theo tháng
            if (monthlyRevenues.containsKey(month)) { // Đây là phương thức trong Map để kiểm tra xem Map có chứa key month hay không.
                Double currentRevenue = monthlyRevenues.get(month); // Dòng này lấy giá trị doanh thu hiện tại của tháng từ Map monthlyRevenues dựa trên key month
                double updatedRevenue = currentRevenue + revenue.getTotalAmount(); //Giá trị doanh thu hiện tại của tháng đang được lưu trong biến currentRevenue.
                monthlyRevenues.put(month, updatedRevenue);
            } else {
                monthlyRevenues.put(month, revenue.getTotalAmount());
            }

            totalRevenue += revenue.getTotalAmount();
        }
        
        // Tính tổng giá mua vào theo tháng
        for (StockHistory stockHis : historyService.findAll()) {
            String month = DateUtils.extractMonthFromDate(stockHis.getEntryDate());
            double purchasePrice = stockHis.getProduct().getPurchasePrice() * stockHis.getQuantityAdded();

            if (monthlyPurchasePrices.containsKey(month)) {
                Double currentPurchasePrice = monthlyPurchasePrices.get(month);
                double updatedPurchasePrice = currentPurchasePrice + purchasePrice;
                monthlyPurchasePrices.put(month, updatedPurchasePrice);
            } else {
                monthlyPurchasePrices.put(month, purchasePrice);
            }
        }
        
        // Tính netRevenue theo tháng
        Map<String, Double> monthlyNetRevenues = new HashMap<>();
        for (String month : monthlyRevenues.keySet()) {
            double netRevenue = monthlyRevenues.get(month) - monthlyPurchasePrices.getOrDefault(month, 0.0);
            monthlyNetRevenues.put(month, netRevenue);
        }
        
        Double totalPurchaseCost = 0.0;
        List<StockHistory> stockHistory = historyService.findAll();
        for (StockHistory stockHis : stockHistory) {
            totalPurchaseCost += stockHis.getProduct().getPurchasePrice() * stockHis.getQuantityAdded();
        }
        
        Double netRevenue = totalRevenue - totalPurchaseCost;

        List<Integer> sortedYears = revenueService.getSortedYears();

        model.addAttribute("years", sortedYears);
        
        model.addAttribute("monthlyRevenues", monthlyRevenues);
        model.addAttribute("revenues", revenues);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalPurchasePrices", monthlyPurchasePrices);
        model.addAttribute("monthlyNetRevenues", monthlyNetRevenues);
        model.addAttribute("NetRevenues", netRevenue);
        model.addAttribute("totalPurchaseCost", totalPurchaseCost);
        return "AdminCpanel/ui-card"; // Trả về template Thymeleaf
    }
}
