package com.yjmedia.yvisbig.bizcom.exception;

public enum ErrorType {
  ALL_SUCCESS_OK(0, "OK.", "all thing sucess.", "ALL_SUCCESS_OK"),

  // 22000 ~  auth, role
  JWT_NOT_AUTH(22001, "Not Auth", "인증실패:HMAC 인증 실패.",
      "NOT_AUTH"),
  JWT_NOT_ADMIN(22002, "Not Admin", "사이트 관리자가 아닙니다 ", "NOT_ADMIN"),
  JWT_NOT_PERMISSION(22003, "Has not Permission",
      "This account not has role. Please contact the manager.", "NOT_PERMISSION"),
  JWT_NOT_EXIST_USER(22004, "User not exist", "사용자가 존재하지 않습니다.",
      "NOT_EXIST_USER"),
  JWT_INVALID_PASSWORD(22006, "Invalid Password", "비밀번호가 일치하지 않습니다.",
      "INVALID_PASSWORD"),
  JWT_INVALID_MEDIA(22007, "Invalid Media", "유효하지 않은 언론사입니다.",
      "INVALID_MEDIA"),
  JWT_INVALID_REFRESH_TOKEN(22008, "Invalid Refresh Token", "유효하지 않은 Refresh Token입니다.",
      "INVALID_REFRESH_TOKEN"),
  JWT_REGIST_USEREINTO_ERROR(22004, "User info error", "User regist error.",
      "REGIST_USEREINTO_ERROR"),
  JWT_REGIST_USEREREDIS_ERROR(22005, "User Redis Info error ", "사용자 레디스 등록오류",
      "REGIST_USEREREDIS_ERROR"),
  JWT_TOKEN_TIME_OUT(22010, "Token Timeout", "토큰 타입 아웃", "JWT_TOKEN_TIME_OUT"),
  JWT_TOKEN_REFRESH_DIFF(22011, "Token Auto Refresh Difference", "토큰 리프레쉬키가 다릅니다.",
      "JWT_TOKEN_REFRESH_DIFF"),
  JWT_TOKEN_REFRESH_TIMEOUT(22012, "Token Auto Refresh time out", "리프레쉬 토큰 만료되었거나, 잘못되었습니다.",
      "JWT_TOKEN_REFRESH_TIMEOUT"),
  JWT_USER_ALREADY_EXISTS(22015, "User Already Exists", "이미 존재하는 사용자입니다.",
      "USER_ALREADY_EXISTS"),
  JWT_INVALID_MEDIA_CREDENTIALS(22016, "Invalid Media Credentials", "유효하지 않은 언론사 인증 정보입니다.",
      "INVALID_MEDIA_CREDENTIALS"),
  LOGIN_DEVICEID_DIFF(22013, "device Id diff", "디바이스 아이디가 다릅니다.",
      "LOGIN_DEVICEID_DIFF"),
  LOGIN_CMDM_DIFF(22014, "cleint,driver check", "고객,기사 구분이 필요합니다.", "LOGIN_CMDM_DIFF"),

  // 22020~ SNS OAuth2 관련 에러
  JWT_SNS_PROVIDER_NOT_SUPPORTED(22020, "SNS Provider Not Supported", "지원하지 않는 SNS 프로바이더입니다.", "SNS_PROVIDER_NOT_SUPPORTED"),
  JWT_SNS_AUTH_FAILED(22021, "SNS Auth Failed", "SNS 인증에 실패했습니다.", "SNS_AUTH_FAILED"),
  JWT_SNS_USER_BLOCKED(22022, "SNS User Blocked", "관리자에 의해 차단된 계정입니다.", "SNS_USER_BLOCKED"),
  JWT_SNS_INVALID_STATE(22023, "Invalid OAuth State", "유효하지 않은 OAuth 상태값입니다.", "SNS_INVALID_STATE"),
  JWT_SNS_CONSENT_REQUIRED(22024, "SNS Consent Required", "필수 동의 항목이 누락되었습니다.", "SNS_CONSENT_REQUIRED"),

  // 23000~ 통합 오류  코드
  IO_EXCEPTION(21009, "IO exception", " IOException. 오류", "IO_EXCEPTION"),
  REQUEST_PARAM_NULL(21010, "Request Param Null", "파라미터 정보가 없습니다.", "REQUEST_PARAM_NULL"),
  REQUEST_UPFIEL_ZERO(21011, "Upload file zero", "업로드 파일 정보가 없습니다.", "REQUEST_UPFIEL_ZERO"),
  FILE_MAKE_DIRECTORY(21012, "make Directory error", "디렉토리 생성중에 오류 발생했습니다.", "FILE_MAKE_DIRECTORY"),

  FILE_UPLOAD_UNKNOWN(21013, "unknown upload file kind", "알수없는 업로드 파일입니다.", "FILE_UPLOAD_UNKNOWN"),

  REQUEST_PARAM_ERROR(21014, "Request Param Error", "파라미터 정보가 잘못됬습니다.", "REQUEST_PARAM_ERROR"),
  FILE_NOT_FOUND(21015, "file not found", "파일을 찾을 수 없습니다.", "FILE_NOT_FOUND"),

  // 25001 DB Error,  Database, SQL
  SQL_GENERAL_NODATA(25001, "SQL OK But No data, No Update, not success", "데이터가 없거나, 잘못된 업데이트입니다.",
      "SQL_GENERAL_WARNING"),
  SQL_GRAMMER_EXCEPTION(25002, "SQL error", "SQL 구문오류. 파라미터, 변수 확인필요합니다.", "SQL_GRAMMER_EXCEPTION"),


  SERVER_INTERNAL_EXCEPTION(26001, "Server error", "Server Internal Error.",
      "SERVER_INTERNAL_EXCEPTION"),

  // 40001~ 외부인터페이스 오류
  EXIF_HTTPCALL_ERROR(40001, "error  external call",
      "외부호출 오류",
      "EXIF_HTTPCALL_ERROR"),

  EXIF_FCM_TOKENEXFIRE(40002, "error  external call",
      "FCM 토큰 만료",
      "EXIF_FCM_TOKENEXFIRE"),

  EXIF_TGMOBIL_ERROR(40003, "error  external call",
          "TG모빌리언스 연동 오류",
          "EXIF_TGMOBIL_ERROR"),
  // 90001~ default, unknown
  UNKNOWN(90001, "Server error", "Sever Internal Error.", "UNKNOWN"),
  BINDING_ERROR(90002, "Binding Error", "파라미터 형식,개수 틀림", "BINDING_ERROR");

  private final int bizErrorCode;
  private final String message;
  private final String detailMessage;
  private final String messageKey;

  ErrorType(int bizErrorCode, String message, String detailMessage, String messageKey) {
    this.bizErrorCode = bizErrorCode;
    this.message = message;
    this.detailMessage = detailMessage;
    this.messageKey = messageKey;
  }

  public int getBizErrorCode() {
    return bizErrorCode;
  }

  public String getMessage() {
    return message;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public String getDetailMessage() {
    return detailMessage;
  }
}
