package com.example.market.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.market.repositories.UserRepository;

@Service
public class SimpleUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
 
    public SimpleUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
 
    // メールアドレスを指定して、ユーザー情報を取得
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        assert(email != null);
        return userRepository
            .findByEmail(email)
            .map(SimpleLoginUser::new)
            .orElseThrow();
    }
}
