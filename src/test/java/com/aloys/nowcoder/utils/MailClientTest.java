package com.aloys.nowcoder.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
class MailClientTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    void sendTextMail() {
        mailClient.sendMail("757898974@qq.com", "测试发送邮件", "啦啦啦啦");
    }

    @Test
    void sendHtmlMail() {
        // thymeleaf 以键值对(HashMap)形式存储元素
        // 模板 html 文件可以通过 每个键值对的键引用该键值对的值;
        // 这里就是 ${username} 可以取到值 testHtmlMail
        Context context = new Context();
        context.setVariable("username", "testHtmlMail");

        // 传入模板 html 文件和 context，构建最终的样式并处理成字符串，然后就可以普通方式发送出去
        String content = templateEngine.process("mail/demoMail", context);
        mailClient.sendMail("757898974@qq.com", "Test Html mail", content);
    }
}