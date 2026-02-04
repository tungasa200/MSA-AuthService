package com.yjmedia.yvisbig.bizcom.encoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * KISA SHA-256 기반 비밀번호 인코더
 * service 프로젝트의 KISA_SHA256_PasswordEncoder와 동일한 로직
 */
@Component("kisaSha256PasswordEncoder")
public class KisaSha256PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return KISA_SHA256.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return KISA_SHA256.encrypt(rawPassword.toString()).equals(encodedPassword);
    }
}