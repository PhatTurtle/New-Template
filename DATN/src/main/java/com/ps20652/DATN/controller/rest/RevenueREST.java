package com.ps20652.DATN.controller.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ps20652.DATN.dao.AccountDAO;
import com.ps20652.DATN.entity.Account;
import com.ps20652.DATN.entity.Product;
import com.ps20652.DATN.entity.Revenue;
import com.ps20652.DATN.service.AccountService;
import com.ps20652.DATN.service.RevenueService;

@RestController
@RequestMapping("/api")
public class RevenueREST {

	@Autowired 
	private RevenueService r_service;
	
	
	@GetMapping("/revenue/getAllYears")
	public List<Integer> getAccounts() {
		return r_service.getAllYears();
	}

	 @GetMapping("/revenues")
    public ResponseEntity<List<Revenue>> getRevenuesByYear(@RequestParam int year) {
        List<Revenue> revenues = r_service.getRevenuesByYear(year);

        if (revenues.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(revenues, HttpStatus.OK);
    }
	
}
