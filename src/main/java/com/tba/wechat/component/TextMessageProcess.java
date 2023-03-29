package com.tba.wechat.component;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tba.wechat.component.type.TextMessage;
import com.tba.wechat.config.properties.CustomProperties;
import com.tba.wechat.util.WechatUtils;
import com.tba.wechat.web.domain.entity.WechatMessage;
import com.tba.wechat.web.service.IWechatMessageService;
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
import java.util.ArrayList;
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
    private final CustomProperties customProperties;

    public TextMessageProcess(IWechatMessageService wechatMessageService, CustomProperties customProperties) {
        this.wechatMessageService = wechatMessageService;
        this.customProperties = customProperties;
    }


    /**
     * 收到文本的后续操作
     *
     * @param message 文本对象
     * @return 返回客户端的消息
     */
    public String process(TextMessage message) {
        LambdaQueryWrapper<WechatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WechatMessage::getMsgId, message.getMsgId());
        if (wechatMessageService.count(queryWrapper) > 0) {
            return "";
        }
        if (!message.getContent().startsWith("yy")) {
            // 清除聊天记录
            if ("清除记录".equals(message.getContent())) {
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.and(item -> item
                        .eq(WechatMessage::getFromUserName, message.getFromUserName())
                        .or()
                        .eq(WechatMessage::getToUserName, message.getFromUserName()));
                wechatMessageService.remove(queryWrapper);
                return this.replyMessage(message.getFromUserName(), message.getToUserName(), message.getCreateTime(), "OK，已清除记录");
            }
            return this.replyMessage(message.getFromUserName(), message.getToUserName(), message.getCreateTime(), "我是个莫的感情的yy：" + message.getContent());
        }

        // 获取历史消息
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(item -> item
                .eq(WechatMessage::getFromUserName, message.getFromUserName())
                .or()
                .eq(WechatMessage::getToUserName, message.getFromUserName()));
        queryWrapper.last("order by create_time limit 10");
        List<WechatMessage> wechatMessageList = wechatMessageService.list(queryWrapper);

        WechatMessage wechatMessage = new WechatMessage();
        wechatMessage.setToUserName(message.getToUserName());
        wechatMessage.setFromUserName(message.getFromUserName());
        wechatMessage.setCreateTime(message.getCreateTime());
        wechatMessage.setContent(message.getContent());
        wechatMessage.setMsgId(message.getMsgId());
        wechatMessageService.save(wechatMessage);

        if (CollectionUtil.isEmpty(wechatMessageList)) {
            wechatMessageList = new ArrayList<>();
        }
        wechatMessageList.add(wechatMessage);

        // 使用历史消息构造openai的chat请求参数
        List<ChatMessage> chatMessageList = wechatMessageList.stream().map(item -> {
            if (item.getFromUserName().equals(message.getFromUserName())) {
                return new ChatMessage(ChatMessageRole.USER.value(), item.getContent());
            } else {
                return new ChatMessage(ChatMessageRole.ASSISTANT.value(), item.getContent());
            }
        }).collect(Collectors.toList());

        OpenAiService service = new OpenAiService(customProperties.getOpenAi().getApiKey(), Duration.ofSeconds(15));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(chatMessageList)
                .maxTokens(4000)
                .temperature(0.8)
                .build();
        List<ChatCompletionChoice> choiceList;
        try {
            choiceList = service.createChatCompletion(chatCompletionRequest).getChoices();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("timeout")) {
                return this.replyMessage(message.getFromUserName(), message.getToUserName(), message.getCreateTime(), "yy脑子不够用了");
            }
            return this.replyMessage(message.getFromUserName(), message.getToUserName(), message.getCreateTime(), "yy好像出了点问题");
        }
        if (CollectionUtil.isEmpty(choiceList)) {
            LOGGER.error("openai响应结果为空");
            return "";
        }
        for (ChatCompletionChoice chatCompletionChoice : choiceList) {
            LOGGER.info("openai 响应结果：{}", chatCompletionChoice.toString());
        }

        wechatMessage = new WechatMessage();
        wechatMessage.setToUserName(message.getFromUserName());
        wechatMessage.setFromUserName(message.getToUserName());
        wechatMessage.setCreateTime(System.currentTimeMillis() / 1000);
        wechatMessage.setContent(choiceList.get(0).getMessage().getContent());
        wechatMessageService.save(wechatMessage);

        return this.replyMessage(wechatMessage.getToUserName(), wechatMessage.getFromUserName(), wechatMessage.getCreateTime(), wechatMessage.getContent());
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
                map.put("Content", "yy好像出了点问题");
                xml = WechatUtils.generatorXml(map);
            } catch (ParserConfigurationException ex) {
                xml = "";
            }
        }
        return xml;
    }

}
