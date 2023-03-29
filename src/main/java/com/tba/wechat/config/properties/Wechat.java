package com.tba.wechat.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 11:04
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tba.wechat")
public class Wechat {

    /**
     * 公众号开发信息：开发者ID(AppID)
     */
    private String appId = "";

    /**
     * 公众号开发信息：开发者密码(AppSecret)
     */
    private String appSecret = "";

    /**
     * 服务器配置：令牌(Token)
     */
    private String serverToken = "";

    /**
     * 服务器配置：消息加解密密钥
     */
    private String encodingAesKey = "";

}
