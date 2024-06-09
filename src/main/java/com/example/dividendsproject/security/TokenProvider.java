package com.example.dividendsproject.security;

import com.example.dividendsproject.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final long TOKEN_EXPIRE_TIME = 1000*60*60;//한시간
    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    @Value("${spring.jwt.secret}")
    private String secretKey;


    //토큰을 생성하는 메소드
    public String generateToken(String username, List<String> roles) {//유저네임과 사용자가 가질 수 있는 권한 정보를 input
        //사용자 권한 정보 저장
        Claims claims = Jwts.claims().setSubject(username); //사용자 이름 저장
        claims.put(KEY_ROLES, roles);//claims데이터 저장할 때는 키 값으로 저장해야함

        //토큰이 생성도니 시간
        var now = new Date();//현재 시간

        //now.getTime() + TOKEN_EXPIRE_TIME(얼마나 토큰을 유효하게 할건지 시간추가)
        // =현재시간으로 부터 TOKEN_EXPIRE_TIME시간까지 datetime을 만료시간으로함
        var expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);//토큰 만료시간

        //생성된 Claims정보와 만료시간을 토큰에 넣어서 생성
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)//토큰 생성 시간
                .setExpiration(expireDate) //토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) //토큰으 시그니처, 사용할 암호화 알고리즘과 비밀키
                .compact();//이렇게 해서 빌드를 끝냄
    }

    //jwt 토큰으로 부터 인증정보를 가져오는 메소드
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    //토큰이 유효한지 확인

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();//넣어준 subject값 username을 얻을 수 있음
    }

    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) return false; //토큰의 값이 값이 빈값이라면 토큰이 유효하지 않아서 false

        var claims = this.parseClaims(token);
        //claims의 토큰만료시간과 현재시간을 비교한 값(현재시간보다 이전인지 아닌지)을 받아서 토큰이 유효한지 확인
        //-> 만료여부 체크
        return !claims.getExpiration().before(new Date());
    }

    //토큰으로부터 Claims정보를 가져오는 메소드
    private Claims parseClaims(String token) {
        try {
            //토큰으로부터 Claims정보를 파싱
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();//Claims정보를 가지고 옴
        }catch (ExpiredJwtException e){//토큰 만료 시간이 지나서 파싱하면 ExpiredJwtException발생
            return e.getClaims();
        }
    }
}
