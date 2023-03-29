package com.tba.wechat.util;

import com.tba.wechat.component.type.Message;
import com.tba.wechat.component.type.TextMessage;
import com.tba.wechat.exception.CustomException;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 16:13
 */
public class WechatUtils {

    private WechatUtils() {
    }

    /**
     * 解析微信客户端消息
     *
     * @param message
     * @return
     * @throws Exception
     */
    public static Object parseMessage(String message) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));

        Element root = doc.getDocumentElement();

        NodeList toUserNameNodeList = root.getElementsByTagName("ToUserName");
        Element toUserNameElement = (Element) toUserNameNodeList.item(0);
        String toUserName = toUserNameElement.getTextContent();

        NodeList fromUserNameNodeList = root.getElementsByTagName("FromUserName");
        Element fromUserNameElement = (Element) fromUserNameNodeList.item(0);
        String fromUserName = fromUserNameElement.getTextContent();

        NodeList createTimeNodeList = root.getElementsByTagName("CreateTime");
        Element createTimeElement = (Element) createTimeNodeList.item(0);
        String createTime = createTimeElement.getTextContent();

        NodeList msgTypeNodeList = root.getElementsByTagName("MsgType");
        Element msgTypeElement = (Element) msgTypeNodeList.item(0);
        String msgType = msgTypeElement.getTextContent();

        NodeList msgIdNodeList = root.getElementsByTagName("MsgId");
        Element msgIdElement = (Element) msgIdNodeList.item(0);
        String msgId = msgIdElement.getTextContent();

        switch (msgType) {
            case "text":
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(toUserName);
                textMessage.setFromUserName(fromUserName);
                textMessage.setCreateTime(Long.parseLong(createTime));
                textMessage.setMsgType("text");
                textMessage.setMsgId(Long.parseLong(msgId));

                NodeList contentNodeList = root.getElementsByTagName("Content");
                Element contentElement = (Element) contentNodeList.item(0);
                String content = contentElement.getTextContent();

                textMessage.setContent(content);
                return textMessage;
            default:
                return new Message();
        }
    }

    /**
     * 生成返回客户端的xml文本
     *
     * @param map xml对应的key-value
     * @return xml文本
     * @throws ParserConfigurationException
     */
    public static String generatorXml(Map<String, Object> map) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("xml");
        document.appendChild(root);

        for (Map.Entry<String, Object> item : map.entrySet()) {
            Element element = document.createElement(item.getKey());
            Object value = item.getValue();
            if (value instanceof String) {
                CDATASection cdataSection = document.createCDATASection(value.toString());
                element.appendChild(cdataSection);
            } else {
                element.setTextContent(value.toString());
            }
            root.appendChild(element);
        }

        return WechatUtils.convertDocumentToString(document);
    }

    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new CustomException(e.getMessage(), e);
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new CustomException(e.getMessage(), e);
        }
        return writer.getBuffer().toString();
    }

}
