package com.tba.wechat.web.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tba.wechat.config.mybatisplus.service.impl.CustomServiceImpl;
import com.tba.wechat.web.domain.entity.WechatCustomAccount;
import com.tba.wechat.web.mapper.WechatCustomAccountMapper;
import com.tba.wechat.web.service.IWechatCustomAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author YangSenLin
 * @since 2023-4-4 13:56
 */

@Service
public class WechatCustomAccountImpl extends CustomServiceImpl<WechatCustomAccountMapper, WechatCustomAccount> implements IWechatCustomAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatCustomAccountImpl.class);

    @Override
    public boolean addCustomAccount(String token, String account, String nickName, String pwd) {
        String url = String.format("https://api.weixin.qq.com/customservice/kfaccount/add?access_token=%s", token);

        Map<String, String> bodyMap = new HashMap<>(5);
        bodyMap.put("kf_account", account);
        bodyMap.put("nickname", nickName);
        bodyMap.put("password", SecureUtil.md5(pwd));

        String body = HttpRequest.post(url)
                .body(JSON.toJSONString(bodyMap))
                .execute()
                .body();
        LOGGER.info("调用创建客服接口，响应内容为：{}", body);

        JSONObject jsonObject = JSON.parseObject(body);

        if (jsonObject.getInteger("errcode") != 0) {
            return false;
        }
        WechatCustomAccount wechatCustomAccount = new WechatCustomAccount();
        wechatCustomAccount.setKfAccount(account);
        wechatCustomAccount.setKfNick(nickName);

        return true;
    }

    @Override
    public boolean modifyCustomAccount() {

        return true;
    }

    @Override
    public boolean uploadCustomHeadImg() {

        return true;
    }

    @Override
    public boolean sendMessageByCustom() {

        return true;
    }

}
