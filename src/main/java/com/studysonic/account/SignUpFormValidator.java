package com.studysonic.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class SignUpFormValidator implements Validator { //SignUpForm을 검증하는 클래스 <- 밸리데이터 인터페이스를 갖고온다.


    private final AccountRepository accountRepository; //여기 ㅠㅠ final 선언 안하면 NPE 납니다 **

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        //TODO email, nickname 중복여부 검사해야함 -> Account Repository가 필요

        SignUpForm signUpForm = (SignUpForm)object;
        if (accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email","invalid.email", new Object[]{signUpForm.getEmail()}, "이메일이 이미 사용중입니다.");
        }

        if (accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname","invalid.nickname", new Object[]{signUpForm.getNickname()}, "닉네임이 이미 사용중입니다.");
        }

    }
}
