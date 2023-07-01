package com.aloys.nowcoder;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LogbackTest {
    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    @Test
    public void testLogger() {
        // logger 的名字为创建对象时传入的 类的名字
        System.out.println(logger.getName());

        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
