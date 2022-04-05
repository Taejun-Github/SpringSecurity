package com.cos.security1.config.oauth;

import com.cos.security1.config.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대해 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // userRequest 정보 -> loadUser 함수 호출 -> 회원 프로필

        OAuth2User oauth2User = super.loadUser(userRequest);
        System.out.println("getAttribute: " + oauth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getClientId(); // 구글
        String providerId = oauth2User.getAttribute("sub");
        String username = provider + "_" + providerId; // 다른 사이트에서 같은 아이디를 가질 수 있으므로
        String email = oauth2User.getAttribute("email");
        String password = bCryptPasswordEncoder.encode("장식용");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null) {
            userEntity = User.builder().username(username).password(password).email(email).role(role).provider(provider).providerId(providerId).build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
        // 이렇게 하면 이것들이 세션으로 들어오게 된다.
    }
}
