package com.jixiebackstage.springboot.config;

import com.jixiebackstage.springboot.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/**/export", "/**/import", "/file/**",
                        "/avatar/**", "/blog/**", "/pageImage/**", "/assets/**", "/article/**", "/comment/**",
                        "/echarts/**", "/official/**");
        // 拦截所有请求，通过判断token
        // 是否合法
        // 决定是否需要登录
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").
                allowedOriginPatterns("*").//允许哪些域访问
                allowedMethods("GET", "POST", "PUT").//允许哪些请求方法访问
                allowCredentials(true).//是否可以携带Cookie
                maxAge(3600).//3600秒内可以不用再访问该配置（是否允许该配置中的设置）
                allowedHeaders("*");//允许哪些请求头访问
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}
