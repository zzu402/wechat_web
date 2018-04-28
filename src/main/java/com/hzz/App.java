package com.hzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
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
        ConfigurableApplicationContext applicationContext = SpringApplication.run(App.class, args);

    }

}
