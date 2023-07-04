package com.aloys.nowcoder.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    private static Logger logger = LoggerFactory.getLogger(MailClient.class);

    // JavaMailSender 主要包含两个方法，createMimeMessage，send
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        try {
            // MimeMessage 就是要发送的邮件
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // MimeMessageHelper 帮助构建 MimeMessage
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            // 设置发送方
            messageHelper.setFrom(from);
            // 设置接收方
            messageHelper.setTo(to);
            // 设置主题
            messageHelper.setSubject(subject);
            // 设置内容，支持 html 格式
            messageHelper.setText(content, true);
            // 发送
            mailSender.send(messageHelper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("邮件发送失败：" + e.getMessage());
        }

    }
}
