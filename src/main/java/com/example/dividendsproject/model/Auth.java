package com.example.dividendsproject.model;

import com.example.dividendsproject.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

//MemberService의 register와 authenticate 구현을 위한
//input으로 받을 수 있는 모델클래스
public class Auth {

    //로그인할 때 사용
    @Data
    public static class SignIn{
        private String username;
        private String password;
    }

    //회원가입할 때 사용
    @Data
    public static class SignUp{
        private String username;
        private String password;
        //어떤 권한을 줄지(내부 로직으로 처리)
        //웹브라우저에서 일반회원가입 경로에서는 일반회원이 가질 수 있는 정보가 들어있고
        //관리자 페이지에서는 관리자가 사용할 수 있는 역할이 들어옴
        private List<String> roles;

        public MemberEntity toEntity(){ //MemberEntity값을 바꿔줌
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
