package com.tba.wechat.web.controller;

import com.tba.wechat.component.WechatComponent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private final WechatComponent wechatComponent;

    public WechatController(WechatComponent wechatComponent) {
        this.wechatComponent = wechatComponent;
    }

    @GetMapping("/wechat")
    public ResponseEntity<String> checkDomain(@RequestParam("signature") String signature,
                                              @RequestParam("timestamp") String timestamp,
                                              @RequestParam("nonce") String nonce,
                                              @RequestParam("echostr") String echostr) {
        if (wechatComponent.checkDomain(signature, timestamp, nonce, echostr)) {
            return ResponseEntity.ok(echostr);
        }
        return ResponseEntity.ok("");
    }

    @GetMapping("/token")
    public ResponseEntity<String> token() {
        return ResponseEntity.ok(wechatComponent.getToken());
    }

}
