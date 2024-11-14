package com.gazi.gazi_renew.mock;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FakeMailSender implements JavaMailSender {

    private List<MimeMessage> sentMessages = new ArrayList<>();

    @Override
    public MimeMessage createMimeMessage() {
        // 실제로는 세션이 필요하지 않으므로 null 세션을 가진 빈 MimeMessage 반환
        return new MimeMessage((Session) null);
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) {
        // 메일을 실제로 전송하지 않고 목록에 추가
        sentMessages.add(mimeMessage);
    }

    @Override
    public void send(MimeMessage... mimeMessages) {
        // 다수의 메일 전송 시 모든 메일을 sentMessages에 추가
        for (MimeMessage mimeMessage : mimeMessages) {
            sentMessages.add(mimeMessage);
        }
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {

    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {

    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {

    }
}
