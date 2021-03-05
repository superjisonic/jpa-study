package com.studysonic.account;

import java.util.List;

import com.studysonic.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService implements UserDetailsService {

    private final JavaMailSender javaMailSender;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    //스프링시큐리티 설정을 다르게 해줘야 이걸 주입받을 수 있음. (현재 설정상 노출 안됨)
    //private final AuthenticationManager authenticationManager;


    public Account processNewAccount(SignUpForm signUpForm) {
        //새 계정 저장하기
        Account newAccount = saveNewAccount(signUpForm);

        //email보내기
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);

        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) //TODO 인코딩 해야
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올랭, 회원가입인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        //정석적인 방법은 아님. 원래 아래 생성자는 User Authentication Manager 내부에서 쓰라고 만들어놓은것.
        //첫번째 파람 - principle, 두번째 파람 - pwd, 마지막 파람 - 권한.
        UsernamePasswordAuthenticationToken token= new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))); //권한목록을 받아준다

//        // 정석적인 방법 - authentification Manager(스프링 시큐리티에 정의된 빈 의존성 주)를 통해서 인증을 해야함
//        UsernamePasswordAuthenticationToken token= new UsernamePasswordAuthenticationToken(
//          username, password);
//        // AuthenticationManager를 통해서 인증을 개친 authentication 객체를 컨텍스트에 넣어줘야한다.
//        Authentication authentication = authenticationManager.authenticate(token);


        // 정석적인 방법은 아니지만,실제 AuthenticationMananger가 하는 일을 그대로 한다.
        // 이렇게 하는 이유 : 현재는 인코딩한 패스워드만 접근할 수 있기때문 -> 정석으로는 plain txt로 받은 비밀번호를 써야하기때문.
        // 그런데 우리는 그 plain txt는 db에 저장도 안하고 더이상 사용하지 않기 때문에 이 방법을 쓴다.
        SecurityContextHolder.getContext().setAuthentication(token);

        // 정석적인 방법
        //context.setAuthentication(authentication);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null){
            account = accountRepository.findByNickname(emailOrNickname);
        }
        if(account == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);//principal에 해당하는 유저 정보를 넘기면된다.
    }

    //Lazy Loading : 모든걸 한꺼번에 읽어와서 모델에 담아주는게 아니라, 뷰에서 도메인 기반으로 객체 네비게이션을 하면서 추가로 로딩을 할 수 있음 -> 도메인 기반 코딩이 수월해짐
    // 데이터 변경은 서비스 계층으로 위임해서 트랜잭션 안에서 처리. (그래야지 DB에 반영이 됨)
    // 데이터 조회는 리파지토리 또는 서비스를 사용한다 (조회는 굳이 트랜잭션이 없어도 되긴함. 그래서 뷰를 렌더링할때 레이지 로딩을 할 수 있음)
   public void completeSignUp(Account account){
        account.completeSignUp();
        login(account);
    }
}
