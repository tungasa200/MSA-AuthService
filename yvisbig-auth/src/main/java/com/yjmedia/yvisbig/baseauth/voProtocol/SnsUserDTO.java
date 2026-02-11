package com.yjmedia.yvisbig.baseauth.voProtocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SNS 사용자 정보 DTO
 * MH_EXT_MEMBER 테이블의 SNS 관련 컬럼 매핑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsUserDTO {

    private int memberSeq;
    private String memberId;
    private String snsLoginType;
    private String snsAccessToken;
    private String email;
    private String name;
    private String nickname;
    private String accountStatus;
    private String memberGrade;
    private int mediaSeq;
    private String socialProfileImagePath;
    private String socialThumbnailImagePath;
    private String socialGender;
    private String socialApproxAge;
    private String socialApproxBirthday;
    private String socialApproxBirthyear;
}
