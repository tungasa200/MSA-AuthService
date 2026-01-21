-- 언론사 키 정보 테이블
CREATE TABLE IF NOT EXISTS svr_media_key (
    media_id VARCHAR(100) NOT NULL,
    media_key VARCHAR(100) PRIMARY KEY,
    media_secret_key VARCHAR(1000) NOT NULL,
    create_id VARCHAR(50),
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_id VARCHAR(50),
    update_dt TIMESTAMP
);

-- 서버 사용자 정보 테이블
CREATE TABLE IF NOT EXISTS svr_user (
    user_sq INT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(100) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    user_nm VARCHAR(100),
    create_id VARCHAR(50),
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_id VARCHAR(50),
    update_dt TIMESTAMP,
    CONSTRAINT uk_media_user UNIQUE (media_id, user_id)
);

-- 회원 정보 테이블 (배치용)
CREATE TABLE IF NOT EXISTS kdmp_mbr_info (
    mbr_sq BIGINT AUTO_INCREMENT PRIMARY KEY,
    mbr_id VARCHAR(100),
    mbr_nm VARCHAR(100),
    mbr_mobile_phone VARCHAR(20),
    mbr_ci VARCHAR(200),
    mbr_st CHAR(1) DEFAULT 'I',
    exit_dt TIMESTAMP
);

-- 회원 상세 정보 테이블 (배치용)
CREATE TABLE IF NOT EXISTS kdmp_mbr_detail (
    mbr_sq BIGINT PRIMARY KEY,
    mbr_email VARCHAR(100),
    mbr_drv_licnum VARCHAR(50),
    mbr_drv_insurnum VARCHAR(50),
    mbr_basic_address VARCHAR(200),
    mbr_detail_address VARCHAR(200),
    mbr_drv_insurnm VARCHAR(100),
    mbr_drv_insurkind VARCHAR(50),
    mbr_wdt_account VARCHAR(50),
    mbr_wdt_account_ownnm VARCHAR(50),
    mbr_vir_account VARCHAR(50),
    mbr_vir_account_ownnm VARCHAR(50),
    vir_bank_kind VARCHAR(50),
    wdt_bank_kind VARCHAR(50),
    mbr_toss_billingkey VARCHAR(100),
    mbr_insur_juminnum VARCHAR(50)
);
