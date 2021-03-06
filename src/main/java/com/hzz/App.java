package com.hzz;

import com.hzz.security.SecurityInterceptor;
import com.hzz.service.VerifyService;
import com.hzz.utils.SpringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/28
 */
@Configuration
@ComponentScan("com.hzz")
@EnableAutoConfiguration
@SpringBootApplication
public class App extends WebMvcConfigurerAdapter {

    public static void main(String[] args){
        SpringUtils.init(App.class,args);
        //不断去处理
        VerifyService verifyService= (VerifyService) SpringUtils.getBean(VerifyService.class);
        verifyService.doVerifyOperation();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getSecurityInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Bean
    SecurityInterceptor getSecurityInterceptor() {
        return new SecurityInterceptor();
    }



}
