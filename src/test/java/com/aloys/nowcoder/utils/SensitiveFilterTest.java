package com.aloys.nowcoder.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testFilter() {
        String text = "哪里可以※赌※博♥和嫖♥娼";
        String result = sensitiveFilter.filter(text);
        System.out.println(result);
    }

}