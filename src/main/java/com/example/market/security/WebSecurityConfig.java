package com.example.market.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        // パスワードの暗号化クラスを戻り値とする
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    // WebSecurity の設定
    @Override
    public void configure(WebSecurity web) throws Exception {
        // images, js, css フォルダ以下のファイルはアクセス制限の対象外とする
        web
            .debug(false)
            .ignoring()
            .antMatchers("/images/**", "/js/**", "/css/**");
    }
 
    // HttpSecurity の設定
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .mvcMatchers("/users/login", "/users/sign_up").permitAll() // login画面と sign_up 画面については認証無しでアクセスできる
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/users/login")
                .loginProcessingUrl("/login") // th:actionに指定したURLを設定
                .usernameParameter("email") // ログインフォームのユーザー欄のname属性を設定
                .passwordParameter("password") // ログインフォームのパスワード欄のname属性を設定
                .defaultSuccessUrl("/items/", true) // ログイン成功時に遷移するURL
                //.failureUrl("/login?error")
                .permitAll()
            .and()
                .logout()
                .invalidateHttpSession(true) // セッションの削除
                .deleteCookies("JSESSIONID") // クッキーの削除
                .logoutSuccessUrl("/users/login"); // ログアウト時はログイン画面へ
    }
    
}
