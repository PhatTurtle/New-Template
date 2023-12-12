package com.ps20652.DATN.service;

import java.util.List;

import com.ps20652.DATN.entity.Order;
import com.ps20652.DATN.entity.Revenue;

public interface RevenueService {

	 public List<Revenue> calculateRevenueForYear(int year);
	 
	 public List<Revenue> getAllRevenues();
	
	 public Revenue create(Revenue revenue);
	 
	 void deleteByOrderId(Integer orderId);
	 
	 public List<Integer> getAllYears();

	 public List<Integer> getSortedYears();

	 public List<Revenue> getRevenuesByYear(int year);
}
