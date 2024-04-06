package com.example.market.services;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.Category;
import com.example.market.entities.Item;
import com.example.market.entities.User;

public interface ItemService {
	// 出品商品一覧の取得
    List<Item> findAll();
    
    List<Item> getExhivitions(long id);
    
    List<Item> getLikedItems(long user_id);
    
    // ID を指定して投稿を取得
    Optional<Item> findById(long id);
    
    // カテゴリーを取得
    List<Category> getCategories();
    
    // 削除
    void delete(long id);
    
    // 出品商品の登録
    Item register(User user, String name, String description, Category category, int price, MultipartFile image, int stock);
    
    List<Item> findAllByNameContaining(String name);
    List<Item> findAllByDescriptionContaining(String description);
    
    void getOrderItems(User user, long Item_id);
    
    // お気に入り処理
    void toggleLike(User user, long item_id);
    
    // 商品画像の更新
    void updateImage(long id, MultipartFile image);
    
    List<Item> getItemsExceptSelf(long user_id);
    
    void update(long id, String name, String description, Category category, int price); 
    }
