package com.gazi.gazi_renew.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;

@Service
public class JsoupService {
    public static Connection getJsoupConnection(String url) throws Exception {
        return Jsoup.connect(url);
    }

    public static Elements getJsoupElements(Connection connection, String url, String query) throws Exception {
        Connection conn = !ObjectUtils.isEmpty(connection) ? connection : getJsoupConnection(url);
        Elements result = null;
        result = conn.get().select(query);
        return result;
    }
    public static void main(String[] args) throws IOException {
        String url = "http://www.seoulmetro.co.kr/kr/board.do?menuIdx=546";

        Document doc = Jsoup.connect("http://www.seoulmetro.co.kr/kr/board.do?menuIdx=546").get();

        Elements datas = doc.select("table tbody ");

        int cnt = 0;
        Elements no = new Elements();
        Elements title = new Elements();
        Elements date = new Elements();

        for(Element data : datas){
            no = data.select("tr td.bd1");
            title = doc.select("tr td.bd2");
            date = doc.select("tr td.bd5");

            System.out.println(no);
            System.out.println(title);
            System.out.println(date);
        }


    }
}
