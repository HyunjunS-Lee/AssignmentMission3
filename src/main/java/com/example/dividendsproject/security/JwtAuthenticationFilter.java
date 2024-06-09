package com.example.dividendsproject.security;//회원관리 패키지

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//요청이 들어올 때 마다 컨트롤러에 실행되기전에 doFilterInternal가 실행되면서 요청헤더에 resolveTokenFromRequest를 통해
//토큰이 있는지 확인하고 토큰이 유효하다면 인증정보를 SecurityContextHolder에 담음->유효하지 않다면
//filterChain.doFilter(request, response); 바로 실행
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {//OncePerRequestFilter를 정의하면 모든 요청이 올 때마다 한 요청당 한번 필터가 실행됨

    //요청이 날아올 때 http 헤더에 TOKEN_HEADER의 키 기준으로 해서 value로는 Bearer한 다음 발급해준 토큰 Bearer xxxx.yyyy.zzz 이렇게 붙어있는 형태로 요청이 오게됨
    public static final String TOKEN_HEADER = "Authorization";//어떤 키를 기준으로 토큰을 주고 받을지에 대한 키값
    public static final String TOKEN_PREFIX = "Bearer ";//인증타입을 나타내기 위해 사용->Jwt토큰을 사용하는 경우에는 토큰 앞에 Bearer을 붙임

    private final TokenProvider tokenProvider;

    @Override//컨토롤러에서 실행되기 전에 doFilterInternal에서 request를 가공하거나 response를 처리하는 일도 가능
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //요청이 올때마다 요청에 토큰이 포함되어 있는지 확인하고 유효한지 확인
        //request 헤더로부터 토큰을 꺼내옴
        String token = this.resolveTokenFromRequest(request);

        if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)){//토큰은 가져온 상태고 토큰이 유효한지 확인
            //토큰 유효성 검증됨

            //security context에 인증정보를 넣어줌
            Authentication auth = this.tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);//인증정보를 넣어줌

            log.info(String.format("[%s]-> %s", this.tokenProvider.getUsername(token), request.getRequestURI()));
        }

        //필터가 연속적으로 실행될 수 있도록 해줌
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);//가져올 키값-> key에 해당하는 value가 나옴

        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){//토큰이 있고 TOKEN_PREFIX로 시작하면
            //정상적인 토큰인지 모르겠지만 토콘의 형태를 포함하고 있는 상태
            return token.substring(TOKEN_PREFIX.length());//TOKEN_PREFIX를 제외한 실제 토큰부위 해당하는 부분
        }

        return null;
    }

}
