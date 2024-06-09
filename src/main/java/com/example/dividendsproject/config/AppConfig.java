package com.example.dividendsproject.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    //스프링 서버가 초기화 되면서 trie 인스턴스도 빈으로 초기화가 될 수 있도록 AppConfig 작성

    @Bean
    public Trie<String, String> trie(){
        return new PatriciaTrie<>();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }//memberservice에서 선언해준 PasswordEncoder에서 이 PasswordEncoder를 가져와서 사용할 수 있음
}
