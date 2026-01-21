-- 테스트용 언론사 키 데이터
-- 비밀키 'qwert1234'를 시스템키로 AES-256 암호화한 값
INSERT INTO svr_media_key (media_id, media_key, media_secret_key, create_id, create_dt)
VALUES ('HANKOOK', '12312s3213123xqweqwe123', 'U2FsdGVkX1+test1234567890encryptedSecretKey==', 'SYS', CURRENT_TIMESTAMP);

-- 테스트용 사용자 데이터
INSERT INTO svr_user (media_id, user_id, user_nm, create_id, create_dt)
VALUES ('HANKOOK', 'testuser001', '테스트유저', 'SYS', CURRENT_TIMESTAMP);
