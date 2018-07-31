package com.example.demo.redis;

import com.example.demo.util.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by WCL on 2018/7/31.
 */
@Configuration
public class WebConfig{
    @Bean
    public SpringContextUtil SpringContextUtilBean() {
        return new SpringContextUtil();
    }
}
