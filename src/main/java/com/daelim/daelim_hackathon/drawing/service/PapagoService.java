package com.daelim.daelim_hackathon.drawing.service;

import com.daelim.daelim_hackathon.drawing.dto.StringDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Log4j2
@Service
public class PapagoService {
    @Value("${NAVER.clientId}")
    private String clientId;//애플리케이션 클라이언트 아이디값";

    @Value("${NAVER.clientSecret}")
    private String clientSecret;//애플리케이션 클라이언트 시크릿값";

    /**
     * ko 받아온 후 en 으로 번역하여 String 을 DTO 에 담아서 반환
     * @param ko
     * @return en
     */
    public StringDTO koToEn(String ko) {
        try {
            String text = URLEncoder.encode(ko, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/nmt/v1/translation";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            // post request
            String postParams = "source=ko&target=en&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            log.info(response.toString());
            int len = response.length();
            response.delete(len - 4, len);
            response.delete(0,78);
            log.info(response.toString());
            return StringDTO.builder().string(response.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
