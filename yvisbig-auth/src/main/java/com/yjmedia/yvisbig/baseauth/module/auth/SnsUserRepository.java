package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.SnsUserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SNS 사용자 Mapper
 * Service DB(MH_EXT_MEMBER)에 대한 SNS 사용자 CRUD
 */
@Mapper
public interface SnsUserRepository {

    /**
     * SNS 사용자 조회 (memberId + loginType)
     */
    List<SnsUserDTO> findSnsUser(@Param("memberId") String memberId,
                                 @Param("loginType") String loginType);

    /**
     * SNS 사용자 생성
     */
    int insertSnsUser(SnsUserDTO dto);
}
