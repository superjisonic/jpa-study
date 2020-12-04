package com.studysonic.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        return "account/sign-up"; //TymeLeaf에 따라 뷰가 찾아지면 초록줄 사라짐
    }
}
