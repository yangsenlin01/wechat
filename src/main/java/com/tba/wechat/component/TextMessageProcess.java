package com.tba.wechat.component;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tba.wechat.component.type.TextMessage;
import com.tba.wechat.config.properties.CustomProperties;
import com.tba.wechat.util.ExceptionUtil;
import com.tba.wechat.util.WechatUtils;
import com.tba.wechat.web.domain.entity.WechatMessage;
import com.tba.wechat.web.domain.entity.WechatWhiteList;
import com.tba.wechat.web.service.IWechatCustomAccountService;
import com.tba.wechat.web.service.IWechatMessageService;
import com.tba.wechat.web.service.IWechatWhiteListService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.parsers.ParserConfigurationException;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 16:40
 */

@Component
public class TextMessageProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextMessageProcess.class);

    private final IWechatMessageService wechatMessageService;
    private final IWechatWhiteListService wechatWhiteListService;
    private final CustomProperties customProperties;
    private final IWechatCustomAccountService customAccountService;

    public TextMessageProcess(IWechatMessageService wechatMessageService, IWechatWhiteListService wechatWhiteListService, CustomProperties customProperties, IWechatCustomAccountService customAccountService) {
        this.wechatMessageService = wechatMessageService;
        this.wechatWhiteListService = wechatWhiteListService;
        this.customProperties = customProperties;
        this.customAccountService = customAccountService;
    }

    /**
     * 收到文本的后续操作
     *
     * @param token   token
     * @param message 文本对象
     * @return 返回客户端的消息
     */
    public String process(String token, TextMessage message) {

        // 查询是否白名单用户
        LambdaQueryWrapper<WechatWhiteList> whiteListQueryWrapper = new LambdaQueryWrapper<>();
        whiteListQueryWrapper.eq(WechatWhiteList::getOpenId, message.getFromUserName());
        if (wechatWhiteListService.count(whiteListQueryWrapper) == 0) {
            return this.replyMessage(message.getFromUserName(), message.getToUserName(), message.getCreateTime(), "你好");
        }

        if (message.getContent().startsWith("添加客服")) {
            String[] split = message.getContent().trim().split(",");
            LOGGER.info("添加客服：{}", message.getContent());
            return customAccountService.addCustomAccount(token, split[1], split[1], split[2]) ? "添加成功" : "添加失败";
        }

        // 查询消息是否已回复
        LambdaQueryWrapper<WechatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatMessage::getMsgId, message.getMsgId());
        queryWrapper.orderByDesc(WechatMessage::getId);
        List<WechatMessage> x = wechatMessageService.list(queryWrapper);
        if (CollectionUtil.isNotEmpty(x)) {
            if (x.size() > 1) {
                WechatMessage y = x.get(0);
                return this.replyMessage(y.getToUserName(), y.getFromUserName(), y.getCreateTime(), y.getContent());
            } else {
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    LOGGER.info(e.getMessage(), e);
                }
                return "";
            }
        }

        // 保存客户端消息
        WechatMessage wechatMessage = new WechatMessage();
        wechatMessage.setToUserName(message.getToUserName());
        wechatMessage.setFromUserName(message.getFromUserName());
        wechatMessage.setCreateTime(message.getCreateTime());
        wechatMessage.setContent(message.getContent());
        wechatMessage.setMsgId(message.getMsgId());
        wechatMessageService.save(wechatMessage);

        // 请求openai
        String response = this.completionRequest(wechatMessage);
        // 保存回复的消息
        WechatMessage saveMessage = new WechatMessage();
        saveMessage.setToUserName(message.getFromUserName());
        saveMessage.setFromUserName(message.getToUserName());
        saveMessage.setMsgId(message.getMsgId());
        saveMessage.setCreateTime(System.currentTimeMillis() / 1000);
        saveMessage.setContent(response);
        wechatMessageService.save(saveMessage);

        return this.replyMessage(saveMessage.getToUserName(), saveMessage.getFromUserName(), saveMessage.getCreateTime(), saveMessage.getContent());
    }

    /**
     * 回复文本消息
     *
     * @param toUserName   接收方帐号（收到的OpenID）
     * @param fromUserName 开发者微信号
     * @param createTime   消息创建时间 （整型）
     * @param content      回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）
     * @return
     */
    public String replyMessage(String toUserName, String fromUserName, Long createTime, String content) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("ToUserName", toUserName);
        map.put("FromUserName", fromUserName);
        map.put("CreateTime", createTime == null ? System.currentTimeMillis() / 1000 : createTime);
        map.put("MsgType", "text");
        map.put("Content", content);
        String xml;
        try {
            xml = WechatUtils.generatorXml(map);
        } catch (ParserConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
            try {
                map.put("Content", "生成xml出了点问题");
                xml = WechatUtils.generatorXml(map);
            } catch (ParserConfigurationException ex) {
                xml = "";
            }
        }
        return xml;
    }

    /**
     * 请求openai并将消息保存
     *
     * @param wechatMessage
     */
//    @Async("taskExecutor")
    public String chatCompletionRequest(WechatMessage wechatMessage) {

//        queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.and(item -> item
//                .eq(WechatMessage::getFromUserName, message.getFromUserName())
//                .or()
//                .eq(WechatMessage::getToUserName, message.getFromUserName()));
//        queryWrapper.last("order by create_time limit 10");
//        List<WechatMessage> wechatMessageList = wechatMessageService.list(queryWrapper);
//        if (CollectionUtil.isEmpty(wechatMessageList)) {
//            wechatMessageList = new ArrayList<>();
//        }

        List<WechatMessage> wechatMessageList = Collections.singletonList(wechatMessage);
        List<ChatMessage> chatMessageList = wechatMessageList.stream().map(item -> {
            if (item.getFromUserName().equals(wechatMessage.getFromUserName())) {
                return new ChatMessage(ChatMessageRole.USER.value(), item.getContent());
            } else {
                return new ChatMessage(ChatMessageRole.ASSISTANT.value(), item.getContent());
            }
        }).collect(Collectors.toList());

        try {
            OpenAiService service = new OpenAiService(customProperties.getOpenAi().getApiKey(), Duration.ofSeconds(60));

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(chatMessageList)
                    .maxTokens(4000)
                    .temperature(0.8)
                    .build();

            List<ChatCompletionChoice> choiceList = service.createChatCompletion(chatCompletionRequest).getChoices();
            for (ChatCompletionChoice chatCompletionChoice : choiceList) {
                LOGGER.info("openai 响应结果：{}", chatCompletionChoice.toString());
            }
            return choiceList.get(0).getMessage().getContent();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            String exceptionMessage = ExceptionUtil.getExceptionMessage(e);
            if (exceptionMessage != null && exceptionMessage.toLowerCase().contains("timeout")) {
                return "内容太多我脑子装不下啦>:<";
            } else {
                return exceptionMessage;
            }
        }
    }

    public String completionRequest(WechatMessage wechatMessage) {
        try {
            OpenAiService service = new OpenAiService(customProperties.getOpenAi().getApiKey(), Duration.ofSeconds(60));

            CompletionRequest completionRequest = CompletionRequest.builder()
                    .model("text-davinci-003")
                    .prompt(wechatMessage.getContent())
                    .maxTokens(4000)
                    .temperature(0.8)
                    .build();
            return service.createCompletion(completionRequest).getChoices().get(0).getText();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            String exceptionMessage = ExceptionUtil.getExceptionMessage(e);
            if (exceptionMessage != null && exceptionMessage.toLowerCase().contains("timeout")) {
                return "内容太多我脑子装不下啦>:<";
            } else {
                return exceptionMessage;
            }
        }

    }
}
