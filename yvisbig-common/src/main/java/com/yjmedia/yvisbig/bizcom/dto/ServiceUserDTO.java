package com.yjmedia.yvisbig.bizcom.dto;

import lombok.Data;

/**
 * Service 프로젝트 사용자 정보 DTO (MH_USERS 테이블)
 */
@Data
public class ServiceUserDTO {
    private String userId;
    private String userLogin;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userOu;
    private String userFlags;
    private String userLanguage;
    private String userLastlogin;
    private String userCreated;
    private String dateExpired;
}