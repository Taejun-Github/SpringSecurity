package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BCryptPasswordEncoder {

    @Bean
    public org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoderPwd() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}
