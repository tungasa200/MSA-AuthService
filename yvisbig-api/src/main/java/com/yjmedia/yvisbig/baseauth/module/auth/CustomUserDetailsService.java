package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.bizcom.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;




    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDTO userDTO = new UserDTO() ;// authRepository.getUserInfo(username);

        UserDetails userDetails = (UserDetails)new User(
                userDTO.getMbrId(),
                userDTO.getMbrPwd(),
                AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_"+userDTO.getMbrPrivilegeTp())
        );

        //System.out.println(userDetails);
        return userDetails;
    }


}