package com.tba.wechat.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * description
 * </p>
 *
 * @author 1050696985@qq.com
 * @since 2023-3-29 10:41
 */

@RestController
public class IndexController {

    @RequestMapping("/")
    public ResponseEntity<String> index() {
        return ResponseEntity.ok("");
    }

}
