package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.repository.IssueRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.sql.Date;

@Service
@RequiredArgsConstructor
public class JsoupService {

    private final IssueRepository issueRepository;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;



    public static Connection getJsoupConnection(String url) throws Exception {
        return Jsoup.connect(url);
    }

    public static Elements getJsoupElements(Connection connection, String url, String query) throws Exception {
        Connection conn = !ObjectUtils.isEmpty(connection) ? connection : getJsoupConnection(url);
        Elements result = null;
        result = conn.get().select(query);
        return result;
    }

    public void getData() throws Exception {
        String url = "http://www.seoulmetro.co.kr/kr/board.do?menuIdx=546";
        Document doc = getJsoupConnection(url).get();

        Element table = doc.select("table tbody ").first();
        Elements rows = table.select("tr");


        int i = 0;
        for(Element row : rows){
            if(i == 1) break;
            IssueRequest dto = new IssueRequest();

            String no = row.select("td.bd1").first().text();
            String title = row.select("td.bd2").first().text();
            String detailUrl = "http://www.seoulmetro.co.kr/kr/"+row.select("td.bd2 a").first().getElementsByAttribute("href").attr("href");
            String date = row.select("tr td.bd5").first().text();

            System.out.println(no);
            System.out.println(title);
            System.out.println(detailUrl);
            System.out.println(date);
            dto.setTitle(title);
            dto.setDate(date);
            // 가장 최근에 조회했을때
            // 금일에 올라온 데이터가 있다면?

            if(!no.equals("NOTICE")){
                // 상세정보 크롤링
                Document detailDoc = getJsoupConnection(detailUrl).get();
                Elements textBoxElements = detailDoc.select(".txc-textbox p");
                StringBuffer content  = new StringBuffer();
                for(Element textBoxElement :textBoxElements){
                    content.append(textBoxElement.select("span").text());
                }
                dto.setContent(content.toString());

                // 관리자 계정 이메일로 텍스트를 보낸다.
                sendEmail(dto);
                i++;
            }
        }
    }


    private MimeMessage createMessage(IssueRequest dto) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("gazinowcs@gmail.com");
        helper.setSubject("서울 교통공사 공지사항 업데이트");

//        String htmlContent = generateHtmlContent(dto);
        helper.setText(setContext(dto), true);

        helper.setFrom("gazinowcs@gmail.com", "gazi");

        return message;
    }

    private String setContext(IssueRequest dto) { // 타임리프 설정하는 코드
        Context context = new Context();
        context.setVariable("dto", dto);
        context.setVariable("title", dto.getTitle());
        context.setVariable("content", dto.getContent());
        context.setVariable("date", dto.getDate());
        return templateEngine.process("emailTemplate", context); // mail.html
    }

    public void sendEmail(IssueRequest dto) throws Exception {
        MimeMessage message = createMessage(dto);

        // 이메일 전송
        emailSender.send(message);
    }

}
