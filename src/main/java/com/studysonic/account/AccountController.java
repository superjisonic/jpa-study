package com.studysonic.account;

import com.studysonic.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Controller
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm") // signUpForm 이라는 데이터를 받을때, 바인딩 설정 가능
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator); //밑에있는 @Valid 어노테이션 객체의 타입을 따라간다. 카멜케이스를 따라감. 변수 이름이 바뀌어도 따라감.
    }


    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/sign-up"; //TymeLeaf에 따라 뷰가 찾아지면 초록줄 사라짐
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignUpForm signUpForm, Errors errors){
        if(errors.hasErrors()){
            return "account/sign-up";
        }

//        signUpFormValidator.validate(signUpForm, errors);
//        if(errors.hasErrors()){
//            return "account/sign-up";
//        } 번거로움,,,

        accountService.processNewAccount(signUpForm);

        return "redirect:/";
    }
    @GetMapping("/check-email-token")
    public String checkEmailToken(String email, String token, Model model){
         Account account = accountRepository.findByEmail(email);
         String view = "account/checked-email";
         if(account == null) {
             model.addAttribute("error", "wrong.email");
             return view;
         }
         if (account.isValidToken(token)) {
             model.addAttribute("error","wrong.email");
             return view;
         }

         account.completeSignUp();

         model.addAttribute("numberOfUser",accountRepository.count());
         model.addAttribute("nickname",account.getNickname());

         return view;
    }


}
