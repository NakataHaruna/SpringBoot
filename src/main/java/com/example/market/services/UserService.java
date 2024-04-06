package com.example.market.services;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.User;

public interface UserService {
    // ユーザー一覧の取得
    List<User> findAll();
    // ユーザーの取得
    Optional<User> findById(long id);
    // ユーザーの登録
    void register(String name, String email, String password, String[] roles);
    
    // プロフィール画像の更新
    void updateImage(Integer id, MultipartFile image);
    
    // プロフィールの更新
    void updateProfile(Integer id, String name, String profile);
    
    // メールアドレス検索
    Optional<User> findByEmail(String email);
}
