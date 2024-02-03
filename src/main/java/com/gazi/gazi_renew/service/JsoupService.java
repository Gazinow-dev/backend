package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JsoupService {

    private final IssueRepository issueRepository;

    public static Connection getJsoupConnection(String url) throws Exception {
        return Jsoup.connect(url);
    }

    public static Elements getJsoupElements(Connection connection, String url, String query) throws Exception {
        Connection conn = !ObjectUtils.isEmpty(connection) ? connection : getJsoupConnection(url);
        Elements result = null;
        result = conn.get().select(query);
        return result;
    }

    public static void getData() throws Exception {
        String url = "http://www.seoulmetro.co.kr/kr/board.do?menuIdx=546";
        Document doc = getJsoupConnection(url).get();

        Element table = doc.select("table tbody ").first();
        Elements rows = table.select("tr");

        for(Element row : rows){
//            no = data.select("tr td.bd1").;
//            title = data.select("tr td.bd2");
//            date = data.select("tr td.bd5");
//            String detailUrl = data.getElementsByAttribute("href").attr("href");

//            getJsoupConnection(data.select("tr td a.href").text());
//            if(!no.text().equals("NOTICE")){
//                // 해당번호가 없다면
//                if(!issueRepository.existsById(Long.valueOf(no.text()))){
//                    // 상세정보 크롤링
//                    getJsoupConnection(data.select("tr td a.href").text());
//
//                    // 관리자 계정 이메일로 텍스트를 보낸다.
//                }
//            }

//            System.out.println(getJsoupConnection(data.select("tr td.bd2 a").text()));
    }




    }
    public static void main(String[] args) throws Exception {

        getData();

//        만약 번호가 내 저장소에있는 가장높은번호와 같지않다면? 새로운 데이터각 있는것으로 간주

//        안에 내용 크롤링 시작
//        데이터를 가공
//        이메일 보내기
//        이슈 심기 or 이슈 무시하기
//        호선 설정 및 라인 설정


    }
}
