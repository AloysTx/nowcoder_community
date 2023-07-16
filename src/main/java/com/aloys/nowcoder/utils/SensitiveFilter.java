package com.aloys.nowcoder.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// 为了方便复用，交给容器管理
@Component
public class SensitiveFilter {

    // 定义 前缀树 (的节点)，内部类
    private class TrieNode {

        // 关键词结束标识
        private boolean isEnd = false;

        // 子节点，可能有多个子节点，所以用 Map
        private final Map<Character, TrieNode> children = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }

        public void setEnd(boolean end) {
            isEnd = end;
        }

        // 添加子节点
        public void addChild(Character c, TrieNode node) {
            children.put(c, node);
        }

        // 获取子节点
        public TrieNode getChild(Character c) {
            return children.get(c);
        }
    }

    private static Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    // 替换字符串
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode root = new TrieNode();

    // 初始化敏感词对应的前缀树
    // @PostConstruct 注解的方法会在 Bean 初始化时自动执行
    // Constructor -> @Autowired（注入） -> @PostConstruct
    // 也就是在依赖注入完成后执行一次（只会执行一次）初始化
    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String sensitiveWord;
            while((sensitiveWord = reader.readLine()) != null) {
                // 添加到前缀树
                this.addSensitiveWord(sensitiveWord);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件出错：" + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树
    private void addSensitiveWord(String sensitiveWord) {
        // 临时节点，指向根节点
        TrieNode temp = root;

        for(int i = 0; i < sensitiveWord.length(); i++) {
            char c = sensitiveWord.charAt(i);
            // 若已存在值为 c 的子节点，则沿用该子节点，不重复添加
            TrieNode child = temp.getChild(c);
            if(child == null) {
                child = new TrieNode();
                temp.addChild(c, child);
            }
            // temp 指向 child，进入下一轮循环
            temp = child;
            // 若是尾节点，则设标志 isEnd = true
            if(i == sensitiveWord.length() - 1) {
                temp.setEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤文本
     * @return 过滤后文本
     */
    public String filter(String text) {
        // 处理空串
        if (StringUtils.isBlank(text)) {
            return null;
        }

        // 指向前缀树的指针
        TrieNode triePointer = root;
        // 子字符串头尾指针
        int head = 0;
        int tail = 0;
        // 存放结果的 StringBuilder
        StringBuilder result = new StringBuilder();
        // 搜索 text
        while (tail < text.length()) {
            char c = text.charAt(tail);
            // 跳过特殊符号（防止在敏感词中间穿插特殊符号来逃避过滤，例如“嫖⭐娼”）
            if (isSpecialSymbol(c)) {
                // 如果 前缀树指针 处于根节点，当前字符一定是 head 字符，直接加入结果，head 指针右移
                if (triePointer == root) {
                    result.append(c);
                    head++;
                }
                // tail 指针右移
                tail++;
                continue;
            }
            // 获取 字符 为 c 的子节点
            triePointer = triePointer.getChild(c);
            // 若没有字符为 c 的子节点，则证明 head 开头的字符串不是敏感词，head 右移
            if (triePointer == null) {
                result.append(text.charAt(head));
                // head 右移, tail 复位到 head
                tail = ++head;
                // 前缀树指针复位，进行下一轮搜索匹配
                triePointer = root;
            } else if (triePointer.isEnd()) {
                result.append(REPLACEMENT);
                // tail 右移, head 直接到 tail（跳过整个匹配到的过滤词）
                head = ++tail;
                // 前缀树指针复位，进行下一轮搜索匹配
                triePointer = root;
            } else {
                // 存在 c 子节点，且不是 end，继续搜索
                tail++;
            }
        }
        // 可能 taiL 遍历到头了，但 head 还没走到最后，因此可能漏掉字符
        // 这里单独处理将 head 到最后的字符加入结果
        result.append(text.substring(head));
        return result.toString();
    }

    // 判断是否是特殊字符
    private boolean isSpecialSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
