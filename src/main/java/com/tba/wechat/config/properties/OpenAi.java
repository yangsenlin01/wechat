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
 * @since 2023-3-29 19:16
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tba.openai")
public class OpenAi {

    /**
     * 调用api的key
     */
    private String apiKey = "";

}
