package com.studysonic.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @DisplayName("회원가입화면 보이는지 테스트")
    @Test
    void SignUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                //.andDo(print()) 를 하면 실제 뷰까지도 테스트가 다 가능하다
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"));
    }
}
