package com.example.market.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.market.entities.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	
	Optional<Item> findById(long id);

	
	List<Item> findAllByNameContaining(String name);
	
	List<Item> findAllByDescriptionContaining(String description);

	List<Item> findAllById(long id);
	
	List<Item> findByUserIdOrderByCreatedAtDesc(long id);
	
	@Query(value="select item.* from item inner join books on item.id=books.item_id where books.user_id = ?1 order by books.id desc",nativeQuery=true)
    List<Item> getLikedItems (long user_id);
	
	List<Item> findByUserIdNotOrderByCreatedAtDesc(long user_id);

}
