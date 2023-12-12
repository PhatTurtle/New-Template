package com.ps20652.DATN.dao;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ps20652.DATN.entity.Revenue;

@Repository
public interface RevenueDAO extends JpaRepository<Revenue, Integer> {

	void deleteByOrder_OrderId(Integer orderId);
	
	 @Query("SELECT DISTINCT YEAR(r.orderDate) FROM Revenue r")
    List<Integer> findAllYearsWithRevenue();

	 @Query("SELECT r FROM Revenue r WHERE YEAR(r.orderDate) = :year")
    List<Revenue> findByOrderYear(@Param("year") int year);
}
