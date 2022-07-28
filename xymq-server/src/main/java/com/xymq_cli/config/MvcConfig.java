package com.xymq_cli.config;

import com.xymq_cli.interceptor.AccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 黎勇炫
 * @date 2022年07月25日 22:47
 */
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {
    // 放行静态资源


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AccessInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/","/index","/index.html","/templates/**","/static/**","/**/*.woff","/**/*.ttf","/**/*.svg","/**/*.js");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("templates/**").addResourceLocations("classpath:/templates/");
        registry.addResourceHandler("static/**").addResourceLocations("classpath:/static/");
    }
}
