package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.bizcom.dto.ServiceUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 사용자 로그인 관련 Repository
 * Service DB의 MH_USERS 테이블 조회
 */
@Mapper
public interface UserLoginRepository {

    /**
     * 사용자 ID로 사용자 정보 조회
     * @param userLogin 사용자 로그인 ID
     * @return 사용자 정보
     */
    ServiceUserDTO findByUserLogin(@Param("userLogin") String userLogin);

    /**
     * 사용자 ID 또는 이메일로 사용자 정보 조회
     * @param username 사용자 ID 또는 이메일
     * @return 사용자 정보
     */
    ServiceUserDTO findByUserLoginOrEmail(@Param("username") String username);
}