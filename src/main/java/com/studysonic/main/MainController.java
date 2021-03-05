package com.studysonic.main;

import com.studysonic.account.CurrentUser;
import com.studysonic.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model){
        if(account != null) {
            model.addAttribute(account);
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login"; // 프리픽스 templates/가 뷰 이름 앞에 붙는것이다. (view resolver에 의함)
    }

}
