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
 * @since 2023-3-29 11:03
 */

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "tba")
public class CustomProperties {

    public CustomProperties(Wechat wechat) {
        this.wechat = wechat;
    }

    /**
     * 微信配置
     */
    private Wechat wechat;

    /**
     * OpenAi配置
     */
    private OpenAi openAi;

}
