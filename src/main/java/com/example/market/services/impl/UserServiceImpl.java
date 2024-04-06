package com.example.market.services.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.market.entities.User;
import com.example.market.repositories.UserRepository;
import com.example.market.services.UserService;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private Environment environment;
 
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
 
    @Transactional(readOnly = true)
    @Override
    public List<User> findAll() {
        // userRepository の findAll メソッドを呼び出す。
        return userRepository.findAll();
    }
 
    @Transactional(readOnly = true)
    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
 
    @Transactional
    @Override
    public void register(String name, String email, String password, String[] roles) {
        // 該当のメールアドレスが登録されているかどうかをチェック
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("該当のメールアドレスは登録済みです。");
        }
        // パスワードを暗号化
        String encodedPassword = passwordEncode(password);
        // ユーザー権限の配列を文字列にコンバート
        String joinedRoles = joinRoles(roles);
        
        String profile = "";
        String image = "";
 
        // User エンティティの生成
        User user = new User(null, null, null, null, name, email, encodedPassword, joinedRoles, Boolean.TRUE, profile, image);
        // ユーザー登録
        userRepository.saveAndFlush(user);
    }
 
    // パスワードを暗号化
    private String passwordEncode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
 
    // ユーザー権限の配列を文字列にコンバート
    private String joinRoles(String[] roles) {
        if (roles == null || roles.length == 0) {
            return "";
        }
        return Stream.of(roles)
            .map(String::trim)
            .map(String::toUpperCase)
            .collect(Collectors.joining(","));
    }
    
    // プロフィール画像の更新
    @Transactional
	@Override
	public void updateImage(Integer id, MultipartFile image) {
		// TODO 自動生成されたメソッド・スタブ
		User user = findById(id).orElseThrow();
		// 拡張子取得
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        // ランダムなファイル名を設定
        String randomFileName = RandomStringUtils.randomAlphanumeric(20) + "." + extension;
        uploadImage(image, randomFileName);
        user.setImage(randomFileName);
        userRepository.saveAndFlush(user);
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
	public void updateProfile(Integer id, String name, String profile) {
		User user = findById(id).orElseThrow();
		user.setName(name);
		user.setProfile(profile);
		userRepository.saveAndFlush(user);
		
	}
	
	
}
