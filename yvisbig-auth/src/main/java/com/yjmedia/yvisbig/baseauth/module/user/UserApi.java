package com.yjmedia.yvisbig.baseauth.module.user;

import com.yjmedia.yvisbig.baseauth.module.auth.UserLoginService;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterResVO;
import com.yjmedia.yvisbig.bizcom.annotation.AcessScope;
import com.yjmedia.yvisbig.bizcom.config.HttpHeaderDefaultType;
import com.yjmedia.yvisbig.bizcom.enums.AccessScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 API 컨트롤러
 * 회원가입, 중복체크 등 사용자 관리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth-svr")
@Tag(name = "User", description = "사용자 관리 API (회원가입/중복체크)")
public class UserApi {

    private final UserLoginService userLoginService;
    private final HttpHeaderDefaultType httpHeaderDefaultType;

    /**
     * 회원가입
     * 새 사용자 등록
     *
     * @param reqVO 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @Operation(summary = "회원가입", description = "새 사용자 등록")
    @PostMapping("/user/register")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserRegisterResVO> register(@Valid @RequestBody UserRegisterReqVO reqVO) {
        log.info("Register request: mediaId={}, userLogin={}", reqVO.getMediaId(), reqVO.getUserLogin());

        UserRegisterResVO resVO = userLoginService.register(reqVO);

        return new ResponseEntity<>(resVO, httpHeaderDefaultType.getHeader(), HttpStatus.CREATED);
    }

    /**
     * 사용자 ID 중복 체크
     *
     * @param userLogin 사용자 로그인 ID
     * @return 사용 가능 여부
     */
    @Operation(summary = "ID 중복 체크", description = "사용자 ID 사용 가능 여부 확인")
    @GetMapping("/user/check-id")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<Map<String, Object>> checkUserId(@RequestParam("userLogin") String userLogin) {
        log.info("Check user ID: userLogin={}", userLogin);

        boolean available = userLoginService.checkUserLoginAvailable(userLogin);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("userLogin", userLogin);
        response.put("message", available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");

        return new ResponseEntity<>(response, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }

    /**
     * 이메일 중복 체크
     *
     * @param userEmail 이메일
     * @return 사용 가능 여부
     */
    @Operation(summary = "이메일 중복 체크", description = "이메일 사용 가능 여부 확인")
    @GetMapping("/user/check-email")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<Map<String, Object>> checkUserEmail(@RequestParam("userEmail") String userEmail) {
        log.info("Check user email: userEmail={}", userEmail);

        boolean available = userLoginService.checkUserEmailAvailable(userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("userEmail", userEmail);
        response.put("message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");

        return new ResponseEntity<>(response, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }
}
