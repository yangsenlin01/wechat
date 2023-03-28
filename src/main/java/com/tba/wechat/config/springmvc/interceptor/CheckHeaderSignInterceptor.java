package com.tba.wechat.config.springmvc.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.tba.wechat.util.ServletUtils;
import com.tba.wechat.web.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * description
 * </p>
 *
 * @author theAplyBoy
 * @since 2023-3-28 11:08
 */

@Component
public class CheckHeaderSignInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckHeaderSignInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String username = request.getHeader("v-username");
        String password = request.getHeader("v-password");
        LOGGER.info("---->>>> v-username: {}, v-password: {}", username, password);

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            ServletUtils.renderString(response, JSON.toJSONString(Result.error("认证失败")));
            return false;
        }
        return true;
    }
}
