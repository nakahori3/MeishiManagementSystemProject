package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private DataSource dataSource;

	// SecurityFilterChainを設定するメソッド
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
	    http.authorizeHttpRequests(authorize -> authorize
	    	// 全てのユーザーに許可
	    	.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
	    	.requestMatchers(new MvcRequestMatcher(null,"/new/**")).permitAll()
	    	.anyRequest().authenticated()
	        ).formLogin(login -> login
	        // ログインを行うURL
	        .loginProcessingUrl("/login")
	        .loginPage("/")	        
	        .defaultSuccessUrl("/success",true)
	        // ログイン失敗のURL
	        .failureUrl("/login?error")
	        .permitAll()
	        )
	
	    	.logout((logout) -> logout
	    			
	    			 // ログアウトのURL
	    	        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
	    	        // ログアウト成功時のURL（ログイン画面に遷移）
	    	        .logoutSuccessUrl("/afterlogout")
					// Cookieの値を削除する
					.deleteCookies("JSESSIONID")
	    	        // セッションを無効化する
	    	        .invalidateHttpSession(true).permitAll());
	    	
	        return http.build();
	}

	// UserDetailsManagerの設定
	@Bean
	UserDetailsManager users() {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(this.dataSource);
		return users;
	}

	// PasswordEncoderの設定
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}