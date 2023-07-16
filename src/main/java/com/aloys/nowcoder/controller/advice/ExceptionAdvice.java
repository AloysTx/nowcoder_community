package com.aloys.nowcoder.controller.advice;

import com.aloys.nowcoder.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    // 方法必须公有且没有返回值
    @ExceptionHandler(Exception.class)
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        for (StackTraceElement element: e.getStackTrace()) {
            logger.error(element.toString());
        }

        // 判断普通请求和异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {   // 异步请求
            response.setContentType("application/plain; charset=utf-8"); // 返回普通的字符串，需要手动转换为json
            PrintWriter writer = response.getWriter();
            writer.write(CommonUtils.getJsonString(1, "服务器异常！"));
        } else {
            // 重定向 （HomeController 的 getErrorPage）
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
