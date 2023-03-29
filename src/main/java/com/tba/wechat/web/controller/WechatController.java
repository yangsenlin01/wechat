package com.tba.wechat.web.controller;

import com.tba.wechat.component.WechatComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 9:03
 */

@RestController
public class WechatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatController.class);

    private final WechatComponent wechatComponent;

    public WechatController(WechatComponent wechatComponent) {
        this.wechatComponent = wechatComponent;
    }

    @RequestMapping(value = "/wechat", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> wechat(HttpServletRequest http,
                                         @RequestParam(value = "signature", required = false) String signature,
                                         @RequestParam(value = "timestamp", required = false) String timestamp,
                                         @RequestParam(value = "nonce", required = false) String nonce,
                                         @RequestParam(value = "echostr", required = false) String echostr,
                                         @RequestBody(required = false) String message) {
        if ("get".equalsIgnoreCase(http.getMethod())) {
            return ResponseEntity.ok(wechatComponent.checkDomain(signature, timestamp, nonce, echostr));
        } else {
            return ResponseEntity.ok(wechatComponent.processMessage(message));
        }
    }

}
