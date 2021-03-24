package com.studysonic.settings;

import com.studysonic.account.AccountService;
import com.studysonic.account.CurrentUser;
import com.studysonic.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    private static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    private static final String SETTINGS_PROFILE_URL = "/settings/profile";

    private final AccountService accountService;

    @GetMapping("/settings/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model){
        model.addAttribute(account);
        model.addAttribute(new Profile(account));

        // 얘는 생략할 수도 있다. url translator가 매핑url과 이름이 같을 것으로 예상하고 알아서 리턴해줌
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    //근데 여기서 생성자를 통해 Profile을 만들려고 하면, Profile.java파일에 있는 생성자 Account가 없기 때문에 문제가 생긴다. 그래서 그곳에 디폴트 생성자 NoArgsConstructor를 만들어줘야함 (또는 기본생성자 만들어야함) -> NPE 방지
    //Account.completeSignUp은 영속성컨텍스트 상태지만
    //근데 updateProfile에 있는 Account객체는 하단의 파라미터 값이기 때문에 Persiste상태가 아니다. 이 객체는 세션에 넣어놨던 인증정보 안의 Principle 객체 정보임. 이 객체는 트랜잭션이 이미 끝난지 오래이다.
    //하단의 account 파라미터의 객체 상태는 persistent상태 중 jpa의 detached상태이다. 하지만 영속성컨텍스트에서 관리하고 있는 객체는 아닌것..
    //Detached 객체는 아무리 변경해도 변경이력을 감지하지 않기때문에 트랜잭션을 DB에 반영안함.
    //그럼 어떻게 Sync맞춰서 반영하냐? Service/Repository에 .save를 구현해주면 그 안에서 Id값이 있는지 없는지 판단하고 있으면 Merge를 시켜준다. -> 기존 데이터에 해당 데이터로 업데이트를 해준다는것.
    @PostMapping("/settings/profile")
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes attributes){
        if (errors.hasErrors()){
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }
        // 여기에서 업데이트 실제로 안됨.. 그 이유는?????!!!
        // account.completeSingUp()은 업데이트 되지
        accountService.updateProfile(account, profile);

        //스프링 MVC에서 지원해주는 기능으로, 잠깐 한번 쓰고 없어져도 되는 데이터를 보내줄때 활용
        //여기서는 모델에 한번 보내주고 말 데이터를 표현하기 위해 사용 :: redirect 후 폼을 다시한번 보여줄때 해당 메시지를 띄울 수 있다.
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + SETTINGS_PROFILE_URL;
    }
}
