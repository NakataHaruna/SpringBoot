package com.example.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.market.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
}