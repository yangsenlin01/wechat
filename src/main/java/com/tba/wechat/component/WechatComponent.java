package com.tba.wechat.component;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tba.wechat.config.properties.CustomProperties;
import com.tba.wechat.exception.CustomException;
import com.tba.wechat.util.Sha1Utils;
import com.tba.wechat.web.domain.entity.WechatToken;
import com.tba.wechat.web.service.IWechatTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 10:52
 */

@Component
public class WechatComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatComponent.class);

    private final CustomProperties customProperties;
    private final IWechatTokenService wechatTokenService;

    public WechatComponent(CustomProperties customProperties, IWechatTokenService wechatTokenService) {
        this.customProperties = customProperties;
        this.wechatTokenService = wechatTokenService;
    }

    /**
     * 校验域名有效性，在接入时微信需要对域名进行有效性验证
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public boolean checkDomain(String signature, String timestamp, String nonce, String echostr) {
        LOGGER.info("收到check参数，signature：{}， timestamp：{}，nonce：{}，echostr：{}", signature, timestamp, nonce, echostr);

        TreeSet<String> set = new TreeSet<>();
        set.add(customProperties.getWechat().getServerToken());
        set.add(timestamp);
        set.add(nonce);

        String finalStr = "";
        for (String s : set) {
            finalStr += s;
        }
        LOGGER.info("按字典排序后的字符串：{}", finalStr);

        finalStr = Sha1Utils.encryption(finalStr);
        LOGGER.info("加密后的字符串：{}", finalStr);

        return signature.equals(finalStr);
    }

    /**
     * 获取微信token
     *
     * @return token
     */
    @Transactional(rollbackFor = Exception.class)
    public String getToken() {
        List<WechatToken> wechatTokenList = wechatTokenService.list();
        if (CollectionUtil.isNotEmpty(wechatTokenList)) {
            WechatToken wechatToken = wechatTokenList.get(0);
            if (wechatToken.getExpiresTime().after(new Date())) {
                return wechatToken.getToken();
            }
        }

        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                customProperties.getWechat().getAppId(),
                customProperties.getWechat().getAppSecret());

        String body = HttpRequest.get(url)
                .execute()
                .body();
        LOGGER.info("请求微信token，响应结果为：{}", body);

        if (StrUtil.isBlank(body)) {
            throw new CustomException("请求微信token失败");
        }
        // {"access_token":"ACCESS_TOKEN","expires_in":7200}
        JSONObject jsonObject = JSON.parseObject(body);
        if (jsonObject.get("errcode") != null && !"0".equals(jsonObject.getString("errcode"))) {
            throw new CustomException("请求微信token失败");
        }

        String token = jsonObject.getString("access_token");
        Integer expiresIn = jsonObject.getInteger("expires_in");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, expiresIn);
        Date expiresDate = calendar.getTime();

        WechatToken wechatToken = new WechatToken();
        wechatToken.setToken(token);
        wechatToken.setExpiresTime(expiresDate);

        if (CollectionUtil.isNotEmpty(wechatTokenList)) {
            wechatTokenService.removeById(wechatTokenList.get(0).getId());
        }

        wechatTokenService.save(wechatToken);

        return token;
    }

}
