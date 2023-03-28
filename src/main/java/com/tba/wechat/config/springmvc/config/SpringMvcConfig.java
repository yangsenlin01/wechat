package com.tba.wechat.config.springmvc.config;

import com.tba.wechat.config.springmvc.interceptor.CheckHeaderSignInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * <p>
 * description
 * </p>
 *
 * @author theAplyBoy
 * @since 2023-3-28 11:04
 */
public class SpringMvcConfig implements WebMvcConfigurer {

    private final CheckHeaderSignInterceptor checkHeaderSignInterceptor;

    public SpringMvcConfig(CheckHeaderSignInterceptor checkHeaderSignInterceptor) {
        this.checkHeaderSignInterceptor = checkHeaderSignInterceptor;
    }

    /**
     * 自定义拦截规则
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//         registry.addInterceptor(checkHeaderSignInterceptor).addPathPatterns("/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置访问源地址
        config.addAllowedOrigin("*");
        // 设置访问源请求头
        config.addAllowedHeader("*");
        // 设置访问源请求方法
        config.addAllowedMethod("*");
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
