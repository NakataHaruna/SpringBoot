package com.example.market.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.Book;
import com.example.market.entities.Category;
import com.example.market.entities.Item;
import com.example.market.entities.User;
import com.example.market.repositories.BookRepository;
import com.example.market.repositories.CategoryRepository;
import com.example.market.repositories.ItemRepository;
import com.example.market.services.ItemService;

/**
 * 画像の保存など、細々した処理を引き受けるクラス
 */
@Service
public class ItemServiceImpl implements ItemService {
	
	private final ItemRepository itemRepository;
	private final CategoryRepository categoryRepository;
	private final BookRepository bookRepository;
	
	@Autowired
    private Environment environment;
 
    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }
 
    @Transactional(readOnly = true)
    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }
    
    @Transactional(readOnly = true)
	@Override
	public Optional<Item> findById(long id) {
		return itemRepository.findById(id);
	}
    
	@Transactional(readOnly = true)
	@Override
	public List<Item> findAllByNameContaining(String name) {
		return itemRepository.findAllByNameContaining(name);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Item> findAllByDescriptionContaining(String description) {
		return itemRepository.findAllByDescriptionContaining(description);
	}
    
	@Transactional
	@Override
	public void delete(long id) {
		Item item = findById(id).orElseThrow();
		itemRepository.delete(item);
	}
	
	@Override
	public void toggleLike(User user, long item_id) {
		Item item = findById(item_id).orElseThrow();
		if (item.getLikedUsers().contains(user)) {
			dislike(user, item);
			return;
		}
		like(user, item);
	}
	
	// 商品画像の更新
	@Transactional
    @Override
    public void updateImage(long id, MultipartFile image) {
		Item item =  findById(id).orElseThrow();
		// 拡張子取得
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        // ランダムなファイル名を設定
        String randomFileName = RandomStringUtils.randomAlphanumeric(20) + "." + extension;
        uploadImage(image, randomFileName);
        item.setImage(randomFileName);
        itemRepository.saveAndFlush(item);
    }

	private void like(User user, Item item) {
		Book favorite = new Book(null, user, item);
        bookRepository.saveAndFlush(favorite);                                              
    }

    private void dislike(User user, Item item) {
        List<Book> favorite = bookRepository.findByUserIdAndItemId(user.getId(), item.getId());
        bookRepository.deleteAll(favorite);
    }
 
	@Transactional
    @Override
    public Item register(User user, String name, String description, Category category, int price, MultipartFile image, int stock) {        // 修正
        if (image.getOriginalFilename().isEmpty()) {
            throw new RuntimeException("ファイルが設定されていません");
        }
        // 拡張子取得
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        // ランダムなファイル名を設定
        String randomFileName = RandomStringUtils.randomAlphanumeric(20) + "." + extension;
        uploadImage(image, randomFileName);
        // Item エンティティの生成
        Item item = new Item(null, user, category, null, null, name, description, price, randomFileName, stock, null, null);

        // Item を保存
        return itemRepository.saveAndFlush(item);                                                                                           // 修正
    }
 
    private void uploadImage(MultipartFile multipartFile, String fileName) {
        // 保存先のパスを作成
        Path filePath = Paths.get(environment.getProperty("sample.images.imagedir") + fileName);
        try {
            // ファイルをバイト列に変換して書き込み
            byte[] bytes  = multipartFile.getBytes();
            OutputStream stream = Files.newOutputStream(filePath);
            stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void getOrderItems(User user, long Item_id) {
		// TODO 自動生成されたメソッド・スタブ
		Item item = findById(Item_id).orElseThrow();
		addcart(user, item);
		
	}
	
	private void addcart(User user, Item item) {
		item.getOrderedUsers().add(user);
		itemRepository.saveAndFlush(item);
	}

	@Override
	public List<Category> getCategories() {
		// TODO 自動生成されたメソッド・スタブ
		return categoryRepository.findAll();
	}

	@Override
	public List<Item> getExhivitions(long id) {
		// TODO 自動生成されたメソッド・スタブ
		
		return itemRepository.findByUserIdOrderByCreatedAtDesc(id);
	}

	@Override
	public List<Item> getLikedItems(long user_id) {
		// TODO 自動生成されたメソッド・スタブ
		return itemRepository.getLikedItems(user_id);
	}

	@Override
	public List<Item> getItemsExceptSelf(long user_id) {
		// TODO 自動生成されたメソッド・スタブ
		return itemRepository.findByUserIdNotOrderByCreatedAtDesc(user_id);
	}

	@Override
    public void update(long id, String name, String description, Category category, int price) {
        Item item = itemRepository.findById(id).orElseThrow();
        item.setName(name);
        item.setDescription(description);
        item.setCategory(category);
        item.setPrice(price);
        itemRepository.saveAndFlush(item);		
    }
}
