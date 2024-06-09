package com.example.dividendsproject.service;

import com.example.dividendsproject.exception.impl.AlreadyExistUserException;
import com.example.dividendsproject.model.Auth;
import com.example.dividendsproject.persist.entity.MemberEntity;
import com.example.dividendsproject.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {//spring-security에서 지원하는 기능을 사용하기 위해서는 loadUserByUsername이런 메소드들을 오버라이드 해줘야함

    private final PasswordEncoder passwordEncoder;//실제 구현체 어떤 빈을 쓸건지 정의해야함(AppConfig에서 함)
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //findByUsername메소드는 optional에 맵핑된 엔티티를 반환-> orElseThrow를 썼으니 optional이 벗겨진 memberEntity를 반환
        //memberEntity는 UserDetails을 상송한 클래스
         return this.memberRepository.findByUsername(username)//사용자 찾기
                .orElseThrow(() -> new UsernameNotFoundException("Couldn't find user -> " + username));//없을 시
    }

    //회원가입에 대한 기능
    public MemberEntity register(Auth.SignUp member){
        //가입 전 회원 존재여부 확인
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if(exists){
            throw new AlreadyExistUserException();
        }

        //password를 그냥 넣으면 보안에 문제가 생겨 암호화해서 db에 넣음
        //인코딩 된 패스워드 값으로 바꿔줌
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        var result = this.memberRepository.save(member.toEntity());

        return result;
    }

    //로그인할 때 검증을 하기 위한 메소드, 즉 패스워드 인증 작업
    public MemberEntity authenticate(Auth.SignIn member){

        var user = this.memberRepository.findByUsername(member.getUsername())//입력받은 member의 Username을 기준으로 해서 memberEntity값을 가져옴
                .orElseThrow(()->new RuntimeException("존재하지 않는 ID 입니다."));//아이디가 존재하지 않는 경우

        //user의 비밀번호와 입력받은 member의 비밀번호가 일치하는지 확인
        //member는 인코딩이 되지 않은 형태->member인코딩 해줘서 매칭해서 비교
        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }
}
