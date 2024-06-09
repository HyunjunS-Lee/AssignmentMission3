package com.example.dividendsproject.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)//.hasRole()같은 어노테이션 처리
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter authenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                //<restApi로 jwt토큰을 해서 인증방식을 구현할 때 붙여줘야하는 부분>
                .httpBasic().disable() //사용하지 않을 부분은 disable
                .csrf().disable()
                //로그인 구현 떄 jwt토큰으로 구현해서 이건 상태정보를 저장하지 않는 STATELESS이 특징
                //반대로 session으로 로그인을 구현하면 session은 상태를 가지고 있는.. STATELESS하지 않는 상태로 구현->session방식 로그인방식
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //<실질적인 권한제어 부분>
                .and()
                .authorizeRequests()
                //무조건 권한을 허용->회원가입과 로그인 api는 토큰없이 접근이 가능해야함(토큰[인증정보] 자체가 로그인 해야 발급되는 것이기 때문)
                .antMatchers("/**/signup", "/**/signin").permitAll()
                //.antMatchers("/company/**").hasAnyRole("READ", "WRITE") // /company/** 경로에 대한 권한 설정
               // .anyRequest().authenticated()
                .and()
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);//필터의 순서를 정해줌
               // .antMatchers("").hasRole() 특정권한을 가지고 있는 사람만 접근할 수 있게 제어 가능
    }

    @Override
    public void configure(final WebSecurity web) throws Exception{
        //h2-console경로로 api를 호출하면 그거에 대한 인증정보는 무시하겠다는 의미
        //인증관련 정보가 없어도 접근 가능
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    //spring 2.x 이상 부터 해줘야함
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }
}
