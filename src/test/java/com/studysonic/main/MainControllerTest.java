package com.studysonic.main;

import com.studysonic.account.AccountRepository;
import com.studysonic.account.AccountService;
import com.studysonic.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc //Junit5 어노테이션(RunWith..)은 안달아도 된다. SpringBootTest에 있음 추가적인게 필요하다면 달아라
public class MainControllerTest {

    @Autowired // 여기에선 생성자 주입에도 이거 달아야함 - Junit에서 먼저 해당 생성자에 인스턴스를 넣으려고 시도하기때문
//    private final
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("sonic");
        signUpForm.setEmail("si4420@naver.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }
    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }


    @DisplayName("이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception{

        mockMvc.perform(post("/login")
                .param("username","si4420@naver.com")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("sonic"));
    }

    @DisplayName("닉네임으로 로그인 성공")
    @Test
    void login_with_nickname() throws Exception{

        mockMvc.perform(post("/login")
                .param("username","sonic")
                .param("password","12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("sonic"));
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception{
        mockMvc.perform(post("/login")
                .param("username","error")
                .param("password","111111111")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {

        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}
