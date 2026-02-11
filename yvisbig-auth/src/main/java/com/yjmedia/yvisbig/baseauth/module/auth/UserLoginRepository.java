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

    /**
     * 사용자 ID 중복 체크
     * @param userLogin 사용자 로그인 ID
     * @return 존재하면 1, 없으면 0
     */
    int checkUserLoginExists(@Param("userLogin") String userLogin);

    /**
     * 이메일 중복 체크
     * @param userEmail 이메일
     * @return 존재하면 1, 없으면 0
     */
    int checkUserEmailExists(@Param("userEmail") String userEmail);

    /**
     * 사용자 등록 (회원가입)
     * @param user 사용자 정보
     * @return 등록된 row 수
     */
    int insertUser(ServiceUserDTO user);

    /**
     * 로그인 성공 시 최근 로그인 시간/IP 업데이트
     */
    int updateLoginInfo(@Param("memberId") String memberId,
                        @Param("lastLoginIp") String lastLoginIp);

    /**
     * 첫 로그인 시간 기록 (NULL인 경우만 업데이트)
     */
    int updateFirstLogin(@Param("memberId") String memberId);
}