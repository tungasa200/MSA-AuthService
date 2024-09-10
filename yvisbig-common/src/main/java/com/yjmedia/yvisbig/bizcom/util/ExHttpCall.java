package com.yjmedia.yvisbig.bizcom.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.bizcom.exception.ServerInterfaceException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.Map;


import static com.yjmedia.yvisbig.bizcom.exception.ErrorType.EXIF_HTTPCALL_ERROR;

@Slf4j
public class ExHttpCall {
    private OkHttpClient httpClient  = null;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExHttpCall(OkHttpClient okHttpClient){
        this.httpClient = okHttpClient;
    }

    public Map<String, Object> syncCallHTTP(Request request) throws Exception {
        if(httpClient != null)
            return _syncCallOkhttp(request);
        return null;
    }

    private Map<String, Object> _syncCallOkhttp(Request request) throws Exception {
            // 기존 Signal 방식을 채택하지 않고, HTTP 고유의 동기 통신용 함수 활용
        Response response = httpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();

        //성공하면
        if (responseBody != null && response.code() >= 200 && response.code() <= 299) {
            String returnBody = responseBody.string();
            // 전송된 내용을 JSON Map 개체로 변환
            return (returnBody == null || returnBody.isEmpty() )
                    ? null: objectMapper.readValue(returnBody, new TypeReference<Map<String, Object>>() {});
        }

        if (responseBody != null) {
            String strError = responseBody.string();
            if(strError.contains("\\u")){
                strError = convertString(strError);
            }
            throw new ServerInterfaceException(EXIF_HTTPCALL_ERROR, strError );
        } else {
            throw new ServerInterfaceException(EXIF_HTTPCALL_ERROR, "Call API Error=>:code: " + response.code()+"message:" + response.message());
        }
    }


    //webclient 을 이용하나 비동기 처리
    public Map<String, Object> asyncCallHTTP(Request request) throws Exception {

        return null;
    }

    //초/분당 호출 제한 콜
    public Map<String, Object> limitCallHTTP(Request request) throws Exception {

        return null;
    }

    public static String convertString(String val) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < val.length(); i++) {
            if ('\\' == val.charAt(i) && 'u' == val.charAt(i + 1)) {
                Character r = (char) Integer.parseInt(val.substring(i + 2, i + 6), 16);
                sb.append(r);
                i += 5;
            } else {
                sb.append(val.charAt(i));
            }
        }
        return sb.toString();
    }

    public Map<String, Object> jsonToObject(String jsonBody) {
        try{
            return objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {});
        }catch (Exception e){
        }
        return null;
    }

    public String objectToJson(Object object) throws Exception{
        return objectMapper.writeValueAsString(object);
    }


}
