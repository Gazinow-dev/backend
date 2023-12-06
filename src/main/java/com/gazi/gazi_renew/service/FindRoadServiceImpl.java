package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.FindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.SubwayDataResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@RequiredArgsConstructor
@Service
public class FindRoadServiceImpl implements FindRoadService {

    private final Response response;
    private final SubwayDataService subwayDataService;
    @Value("${odsay.key}")
    public static String apiKey;

    public JSONObject getJsonArray(String urlInfo) throws IOException {
        URL url = new URL(urlInfo.toString());
        System.out.println(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        conn.disconnect();

        String jsonData = sb.toString();
        JSONObject jsonObject = new JSONObject(jsonData);

        return jsonObject;
    }

    public JSONObject getJsonArray(Double sx, Double sy, Double ex, Double ey) throws IOException {

        StringBuilder urlInfo = new StringBuilder("https://api.odsay.com/v1/api"); /*URL*/
        urlInfo.append("/" + URLEncoder.encode("searchPubTransPathT", "UTF-8"));
        urlInfo.append("?LANG=" + URLEncoder.encode(String.valueOf(1), "UTF-8"));
        urlInfo.append("&SX=" + URLEncoder.encode(String.valueOf(sx), "UTF-8"));
        urlInfo.append("&SY=" + URLEncoder.encode(String.valueOf(sy), "UTF-8"));
        urlInfo.append("&EX=" + URLEncoder.encode(String.valueOf(ex), "UTF-8"));
        urlInfo.append("&EY=" + URLEncoder.encode(String.valueOf(ey), "UTF-8"));
        urlInfo.append("&apiKey=" + URLEncoder.encode("mdn3gFOpu1TYxWEF80iAU4Fmlo2/OSQruUG1Vqw18Xw", "UTF-8"));

        return getJsonArray(urlInfo.toString());
    }

    @Override
    public ResponseEntity<Response.Body> subwayRouteSearch(Long CID, Long SID, Long EID, int sopt) throws IOException {
        StringBuilder urlInfo = new StringBuilder("https://api.odsay.com/v1/api"); /*URL*/
        urlInfo.append("/" + URLEncoder.encode("subwayPath", "UTF-8"));
        urlInfo.append("?CID=" + URLEncoder.encode(String.valueOf(CID), "UTF-8"));
        urlInfo.append("&SID=" + URLEncoder.encode(String.valueOf(SID), "UTF-8"));
        urlInfo.append("&EID=" + URLEncoder.encode(String.valueOf(EID), "UTF-8"));
        urlInfo.append("&sopt=" + URLEncoder.encode(String.valueOf(sopt), "UTF-8"));
        urlInfo.append("&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));
        JSONObject json = getJsonArray(urlInfo.toString());
        return response.success(json, "길 찾기 데이터 전송 완료", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> findRoad(FindRoadRequest request) throws IOException {

        // 출발역 이름과 호선으로 데이터 찾기
        SubwayDataResponse strSubwayInfo = subwayDataService.getCoordinateByNameAndLine(request.getStrSubwayName(), request.getStrSubwayLine());
        // 종착역 이름과 호선으로 데이터 찾기
        SubwayDataResponse endSubwayInfo = subwayDataService.getCoordinateByNameAndLine(request.getEndSubwayName(), request.getEndSubwayLine());

        JSONObject json = getJsonArray(strSubwayInfo.getLng(), strSubwayInfo.getLat(), endSubwayInfo.getLng(), endSubwayInfo.getLat());
        return response.success(json, "길 찾기 데이터 전송 완료", HttpStatus.OK);
    }
}
