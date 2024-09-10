package com.yjmedia.yvisbig.bizcom.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringKdmpUtil {

    /**
     * 파라미터에서 숫자만 처리 추출한다.
     * @param strParam
     * @return 암호화 된 패스워드
     */
    public static String getNumberString(String strParam){

        Pattern pattern = Pattern.compile("\\d+"); // \\d는 숫자를 나타내는 정규표현식
        Matcher matcher = pattern.matcher(strParam);

        // 매칭된 숫자를 저장할 문자열
        StringBuilder numbers = new StringBuilder();

        // 매칭된 모든 숫자를 추출하여 numbers 문자열에 추가
        while (matcher.find()) {
            numbers.append(matcher.group());
        }
        return numbers.toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
