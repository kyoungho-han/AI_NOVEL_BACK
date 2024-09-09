package com.daelim.daelim_hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class DaelimHackathonApplication {
    // EC2 로 서버를 운영할 때 필요
    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    // security password 인코더 설정을 위한 빈설정
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(DaelimHackathonApplication.class, args);
    }

}
