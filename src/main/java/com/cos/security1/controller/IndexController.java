package com.cos.security1.controller;

import com.cos.security1.config.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller // View를 리턴하겠다는 뜻이다.
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        System.out.println("/test/login ==========================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication:" + principalDetails.getUser());

        System.out.println(userDetails.getUser());
        return "세션 정보 확인하기";
    }
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth) {
        System.out.println("/test/OAuth/login ==========================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println(oAuth2User.getAttributes());
        System.out.println(oAuth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }

    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping({"", "/"})
    public String index() {
        // pom.xml에 mustache를 사용하겠다고 하면 자동으로 뷰 리졸버가 설정된다.
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        System.out.println("principalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        return "login/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "login/joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        log.info(user.toString());
        String pattern = "admin";
        String username = user.getUsername();
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        if(username.contains(pattern)) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }
        userRepository.save(user);
        // 원래는 위의 로직도 서비스에서 처리하고 이것도 서비스로 보내야 하는데 여기서는 생략
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN") // @EnableGlobalMethodSecurity(securedEnabled = true)로 활성화된다.
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // @EnableGlobalMethodSecurity(prePostEnabled = true)로 활성화된다.
    // @PostAuthorize 이것은 메서드가 종료되고 나서 수행한다.
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터정보";
    }

}
