package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.config.MediaProperties;
import com.yjmedia.yvisbig.baseauth.voProtocol.*;
import com.yjmedia.yvisbig.bizcom.constants.RedisKeyConfig;
import com.yjmedia.yvisbig.bizcom.dto.SvrMediaKeyDTO;
import com.yjmedia.yvisbig.bizcom.dto.SvrUserDTO;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerBizException;
import com.yjmedia.yvisbig.bizcom.security.TokenProvider;
import com.yjmedia.yvisbig.bizcom.util.CryptoUtil;
import com.yjmedia.yvisbig.bizcom.util.LogJsonMapper;
import com.yjmedia.yvisbig.bizcom.util.RedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component("authCoreService")
@RequiredArgsConstructor
public class AuthCoreService {

  private final AuthRepository authRepository;
  private final TokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final RedisManager redisManager;

  private final RedisKeyConfig redisKeyConfig;

  private final LogJsonMapper logJsonMapper;

  private final MediaProperties mediaProperties;

  @Value("${jwt.system-secret}")
  private String systemSecret;




  @Transactional
  public SvrGetTokenResVO getToken(SvrGetTokenReqVO svrGetTokenReqVO){

    SvrGetTokenResVO svrGetTokenResVO = new SvrGetTokenResVO();

    //1. 언론사 비밀키 조회 (application.yml 우선, 없으면 DB 조회)
    String mediaSecretKey = getMediaSecretKey(svrGetTokenReqVO.getMediaKey());

    try{

      //2. 해쉬 검증
      String verifyHash = CryptoUtil.getHMAC(
              svrGetTokenReqVO.getMediaId()+svrGetTokenReqVO.getUserId()+svrGetTokenReqVO.getUserNm()+ svrGetTokenReqVO.getCallDate(),
              mediaSecretKey,
              "HmacSHA256");

      if(!svrGetTokenReqVO.getHmacHash().equals(verifyHash)){
        log.error("{}|{}|{}", svrGetTokenReqVO.getClientId(),
                "", "잘못된 요청입니다. Hash 값이 다릅니다.");
        throw new ServerBizException(ErrorType.JWT_NOT_AUTH);
      }

      //3. 토큰 생성
      svrGetTokenResVO.setJwt(tokenProvider.createTokenWithString(svrGetTokenReqVO.getMediaId(),
              svrGetTokenReqVO.getUserId()));

      //4. User 인증 정보 업데이트
      SvrUserDTO svrUserDTOExist = authRepository.selectSvrUserWithId(svrGetTokenReqVO.getMediaId(),svrGetTokenReqVO.getUserId());

      if( svrUserDTOExist == null){
        SvrUserDTO svrUserDTO = new SvrUserDTO();
        svrUserDTO.setUserNm(svrGetTokenReqVO.getUserNm());
        svrUserDTO.setMediaId(svrGetTokenReqVO.getMediaId());
        svrUserDTO.setUserId(svrGetTokenReqVO.getUserId());

        authRepository.insertSvrUser(svrUserDTO);
      }else {
        svrUserDTOExist.setUserNm(svrGetTokenReqVO.getUserNm());
        authRepository.updateSvrUser(svrUserDTOExist);
      }

    }catch (ServerBizException se) {
      log.error("{}|{}|{}", svrGetTokenReqVO.getClientId(),
              "", se.getMessage());
      throw se;
    }catch (BadSqlGrammarException se) {
      log.error("{}|{}|{}", "insert,update user fail", "Svr_User", se.getMessage());
      throw new ServerBizException(ErrorType.SQL_GRAMMER_EXCEPTION, se.getMessage());
    }catch (NoSuchAlgorithmException ek) {
      log.error("{}|{}|{}", "getToken", "NoSuchAlgorithmException", ek.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, ek.getMessage());
    }catch (InvalidKeyException ik) {
      log.error("{}|{}|{}", "getToken", "InvalidKeyException ", ik.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, ik.getMessage());
    }catch (Exception e) {
      log.error("{}|{}|{}", "getToken", "Exception GetToken", e.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, e.getMessage());
    }

    return svrGetTokenResVO;
  }


  //------------------------------------------
  // refresh 별도 처리시 로직 삽입
  @Transactional
  public SvrRefreshTokenResVO refreshToken(SvrRefreshTokenReqVO svrRefreshTokenReqVO) {

    SvrRefreshTokenResVO svrRefreshTokenResVO = new SvrRefreshTokenResVO();

    //1. 언론사 비밀키 조회 (application.yml 우선, 없으면 DB 조회)
    String mediaSecretKey = getMediaSecretKey(svrRefreshTokenReqVO.getMediaKey());

    try{
      //2. 해쉬 검증
      String verifyHash = CryptoUtil.getHMAC(
              svrRefreshTokenReqVO.getMediaId()+svrRefreshTokenReqVO.getUserId()+svrRefreshTokenReqVO.getUserNm()+ svrRefreshTokenReqVO.getCallDate(),
              mediaSecretKey,
              "HmacSHA256");

      if(!svrRefreshTokenReqVO.getHmacHash().equals(verifyHash)){
        log.error("{}|{}|{}", svrRefreshTokenReqVO.getClientId(),
                "", "잘못된 요청입니다. Hash 값이 다릅니다.");
        throw new ServerBizException(ErrorType.JWT_NOT_AUTH);
      }

      //4. 토큰 생성
      svrRefreshTokenResVO.setJwt(tokenProvider.createTokenWithString(svrRefreshTokenReqVO.getMediaId(),
              svrRefreshTokenReqVO.getUserId()));

      //5. User 인증 정보 업데이트
      SvrUserDTO svrUserDTOExist = authRepository.selectSvrUserWithId(svrRefreshTokenReqVO.getMediaId(),svrRefreshTokenReqVO.getUserId());

      if( svrUserDTOExist == null){
        SvrUserDTO svrUserDTO = new SvrUserDTO();
        svrUserDTO.setUserNm(svrRefreshTokenReqVO.getUserNm());
        svrUserDTO.setMediaId(svrRefreshTokenReqVO.getMediaId());
        svrUserDTO.setUserId(svrRefreshTokenReqVO.getUserId());

        authRepository.insertSvrUser(svrUserDTO);
      }else {
        svrUserDTOExist.setUserNm(svrRefreshTokenReqVO.getUserNm());
        authRepository.updateSvrUser(svrUserDTOExist);
      }

    }catch (ServerBizException se) {
      log.error("{}|{}|{}", svrRefreshTokenReqVO.getClientId(),
              "", se.getMessage());
      throw se;
    }catch (BadSqlGrammarException se) {
      log.error("{}|{}|{}", "insert,update user fail", "Svr_User", se.getMessage());
      throw new ServerBizException(ErrorType.SQL_GRAMMER_EXCEPTION, se.getMessage());
    }catch (Exception e) {
      log.error("{}|{}|{}", "getToken", "Delete Members", e.getMessage());
      throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, e.getMessage());
    }
    return svrRefreshTokenResVO;
  }

  /**
   * 언론사 비밀키 조회
   * application.yml 설정 우선, 없으면 DB에서 조회
   */
  private String getMediaSecretKey(String mediaKey) {
    // 1. application.yml에서 조회
    return mediaProperties.findByMediaKey(mediaKey)
            .map(MediaProperties.MediaConfig::getMediaSecret)
            .orElseGet(() -> {
              // 2. DB에서 조회
              try {
                SvrMediaKeyDTO svrMediaKeyDTO = authRepository.getMediaKeyInfo(mediaKey);
                if (svrMediaKeyDTO != null) {
                  return CryptoUtil.decryptAES256(svrMediaKeyDTO.getMediaSecretKey(), systemSecret);
                }
              } catch (Exception e) {
                log.error("언론사 비밀키 DB 조회 실패: {}", e.getMessage());
              }
              throw new ServerBizException(ErrorType.JWT_NOT_AUTH, "언론사 키를 찾을 수 없습니다: " + mediaKey);
            });
  }

}
