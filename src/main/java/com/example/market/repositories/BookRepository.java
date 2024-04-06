package com.example.market.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.market.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByUserIdAndItemId(long user_id, long item_id);
}