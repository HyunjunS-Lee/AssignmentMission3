package com.example.dividendsproject.web;

import com.example.dividendsproject.model.Auth;
import com.example.dividendsproject.security.TokenProvider;
import com.example.dividendsproject.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Auth.SignUp request){
        var result = this.memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Auth.SignIn request){
        //패스워드 검증->인증이 되었다면 토큰생성해서 반환

        var member = this.memberService.authenticate(request);//정상적으로 되었다면 memberEntity가 나옴
        var token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());//memberEntity를 기준으로 토큰을 만듬
        log.info("user login : " + request.getUsername());
        return ResponseEntity.ok(token);
    }

}
