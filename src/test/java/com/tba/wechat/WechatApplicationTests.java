package com.tba.wechat;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.tba.wechat.util.WechatUtils;
import com.tba.wechat.web.domain.entity.WechatCustomAccount;
import com.tba.wechat.web.mapper.WechatCustomAccountMapper;
import com.tba.wechat.web.service.IWechatCustomAccountService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.parsers.ParserConfigurationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class WechatApplicationTests {

    private final String API_KEY = "";

    @Autowired
    private IWechatCustomAccountService customAccountService;

    @Autowired
    private WechatCustomAccountMapper wechatCustomAccountMapper;

    @Test
    void testMysql() {
        List<WechatCustomAccount> wechatCustomAccountList = wechatCustomAccountMapper.listAll();
        wechatCustomAccountList.forEach(System.out::println);
    }

    @Test
    void testHutools() {
        String apiKey = "Bearer " + API_KEY;

        Map<String, Object> map = new HashMap<>();
        map.put("model", "text-davinci-003");
        map.put("prompt", "Say this is a test");
        map.put("max_tokens", 77);
        map.put("temperature", 0);
        String body = HttpRequest.post("https://api.openai.com/v1/completions")
                .setHttpProxy("127.0.0.1", 10809)
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .body(JSON.toJSONString(map))
                .execute()
                .body();
        System.out.println(body);
    }

    @Test
    void testCompletionRequest() {
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "10809");

        OpenAiService service = new OpenAiService(API_KEY);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt("请解释下这个代码：System.out.println(\"I'm tom!\");")
                .maxTokens(1024)
                .temperature(0.8)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
    }

    @Test
    void testChatCompletionRequest() {
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "7890");

        OpenAiService service = new OpenAiService(API_KEY);

        List<ChatMessage> chatMessageList = Arrays.asList(
                new ChatMessage(ChatMessageRole.USER.value(), "请用姓氏“杨”起一个中文名称。"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), "杨柳。"),
                new ChatMessage(ChatMessageRole.USER.value(), "请让名字看起来更像男生一点。"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), "杨泽。"),
                new ChatMessage(ChatMessageRole.USER.value(), "换一个三个字的"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), "杨岳辉。"),
                new ChatMessage(ChatMessageRole.USER.value(), "4个字的"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), "杨宇辰。"),
                new ChatMessage(ChatMessageRole.USER.value(), "5个字的")
        );

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessageList)
                .maxTokens(1024)
                .temperature(0.8)
                .build();
        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(System.out::println);
    }

    @Test
    void testGeneratorXml() {
        String toUserName = "toUser";
        String fromUserName = "fromUser";
        Long createTime = 12345678L;
        String content = "你好";

        Map<String, Object> map = new HashMap<>(7);
        map.put("ToUserName", toUserName);
        map.put("FromUserName", fromUserName);
        map.put("CreateTime", createTime);
        map.put("MsgType", "text");
        map.put("Content", content);

        String xml;
        try {
            xml = WechatUtils.generatorXml(map);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            try {
                map.put("Content", "出了点问题");
                xml = WechatUtils.generatorXml(map);
            } catch (ParserConfigurationException ex) {
                xml = "";
            }
        }
        System.out.println(xml);
    }

}
