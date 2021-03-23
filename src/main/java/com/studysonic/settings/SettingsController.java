package com.studysonic.settings;

import com.studysonic.account.CurrentUser;
import com.studysonic.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));

        // 얘는 생략할 수도 있다. url translator가 매핑url과 이름이 같을 것으로 예상하고 알아서 리턴해줌
        return "settings/profile";
    }
}
