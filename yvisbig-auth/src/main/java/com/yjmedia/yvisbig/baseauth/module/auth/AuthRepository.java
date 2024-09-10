package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.bizcom.dto.SvrMediaKeyDTO;
import com.yjmedia.yvisbig.bizcom.dto.SvrUserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRepository {
    SvrMediaKeyDTO getMediaKeyInfo(String mediaKey);

    int insertSvrUser(SvrUserDTO svrUserDTO);
    int updateSvrUser(SvrUserDTO svrUserDTO);
    SvrUserDTO selectSvrUserWithId(String mediaId, String userId);

}
