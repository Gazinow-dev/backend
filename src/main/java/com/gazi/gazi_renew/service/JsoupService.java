package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.repository.IssueRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsoupService {

    private final IssueRepository issueRepository;
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final JsonSimple jsonSimple;

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

            dto.setTitle(title);
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

    @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 실행
    public void noticeCrawler() throws Exception {
        // 자동업데이트
        // WebDriverManager.chromedriver().setup();
        // 수동 업데이트
        // https://storage.googleapis.com/chrome-for-testing-public/122.0.6261.57/mac-x64/chromedriver-mac-x64.zip
        System.setProperty("webdriver.chrome.driver", "./chromedriver");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-popup-blocking");   // 팝업 안띄움
//        options.addArguments("headless");   // 브라우저 안띄움
        options.addArguments("--disable-gpu");  // gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false");   // 이미지 다운 안받음
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        // WebDriver 가 로드될때까지 10초 기다림
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://topis.seoul.go.kr/notice/openNoticeList.do");

        // noticeTbl 테이블의 tbody 내의 tr 요소들을 찾아 리스트로 받아옴
        // notiList 안의 모든 tr 요소들을 리스트로 받아옴
        // 가져올 데이터가 로딩될 때까지 기다림
        webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='notiList']//tr"))
        );
        List<WebElement> rows = driver.findElements(By.xpath("//*[@id='notiList']//tr"));

        loopOut:
        // 각 tr 요소 출력
        for (int i = 0; i < rows.size(); i++) {

            Thread.sleep(5000);
            String title = "";
            String no = "topis-";
            String content = "";
            int latestNo = 0;
            WebElement row = rows.get(i);
            Thread.sleep(5000);
            // 현재 행에서 모든 td 요소 가져오기
            List<WebElement> columns = row.findElements(By.tagName("td"));

            // 1, 2, 4번째 td 출력
            if (columns.size() >= 4) {
                webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"notiList\"]/tr[1]/td[4]"))
                );

                WebElement firstTd = columns.get(0);
                WebElement secondTd = columns.get(1);
                no += firstTd.getText();

                // 최근 조회 넘버가 변경되지 않으면 크롤링을 돌필요가 없다.
                if(i == 0){
                    latestNo = Integer.parseInt(firstTd.getText());
                    int latestStoredNo = jsonSimple.getLastestNumber();
                    log.info("latestNo: " + latestNo);
                    log.info("latestStoredNo: " + latestStoredNo);
                    if(latestNo == latestStoredNo){
                        break loopOut;
                    }else{
                        jsonSimple.writeJson(latestNo);
                    }
                }

                // 번호를 통한 검증로직
                if(issueRepository.existsByCrawlingNo(no)){
                    continue;
                }
                title = secondTd.getText();
                WebElement aTag = secondTd.findElement(By.tagName("a"));

                if (!aTag.equals("")) {
                    aTag.click();

                    WebElement contentElement = driver.findElement(By.xpath("//*[@id=\"brdContents\"]"));
                    content = contentElement.getText();
                }
            }
            driver.navigate().back();

            IssueRequest dto = new IssueRequest();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCrawlingNo(no);
            dto.setLatestNo(latestNo);
            if(dto.getTitle() != null && dto.getTitle() != null){
                sendEmail(dto);
            }
        }
        // WebDriver 종료
        driver.quit();

    }

    private MimeMessage createMessage(IssueRequest dto) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo("gazinowcs@gmail.com");
        helper.setSubject("topis 공지사항 업데이트");

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
        context.setVariable("crawlingNo", dto.getCrawlingNo());
        context.setVariable("latestNo", dto.getLatestNo());
        return templateEngine.process("emailTemplate", context); // mail.html
    }

    public void sendEmail(IssueRequest dto)  {
        try{
            MimeMessage message = createMessage(dto);
            // 이메일 전송
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

}
