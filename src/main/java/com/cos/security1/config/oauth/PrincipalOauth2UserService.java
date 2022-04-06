package com.cos.security1.config.oauth;

import com.cos.security1.config.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
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

import java.util.Map;

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

        // 구글과 페이스북의 각각 요소의 이름이 다르므로 불편하다. 따라서 provider의 인터페이스와 클래스 이용
//        String provider = userRequest.getClientRegistration().getRegistrationId(); // 구글
//        String providerId = oauth2User.getAttribute("sub");
//        String username = provider + "_" + providerId; // 다른 사이트에서 같은 아이디를 가질 수 있으므로
//        String email = oauth2User.getAttribute("email");
//        String password = bCryptPasswordEncoder.encode("장식용");
//        String role = "ROLE_USER";
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());

        } else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oauth2User.getAttributes().get("response"));
        }else {
            System.out.println("구글과 페이스북과 네이버만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // 다른 사이트에서 같은 아이디를 가질 수 있으므로
        String email = oAuth2UserInfo.getEmail();
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
