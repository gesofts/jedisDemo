package com.example.demo.controller;

/**
 * Created by WCL on 2018/7/30.
 */

import com.alibaba.fastjson.JSON;
import com.example.demo.model.User;
import com.example.demo.redis.JedisClusterConfig;
import com.example.demo.redis.MyRedisTemplate;
import com.example.demo.util.JedisClusterUtils;
import com.example.demo.util.MyConstants;
import com.example.demo.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    @RequestMapping("/getUser")
    public User getUser() {
        return new User(1, "gesoft" , 88);
    }

    @RequestMapping("/testJedisCluster")
    public User testJedisCluster(@RequestParam("username") String username){
        String value =  myRedisTemplate.get(MyConstants.USER_FORWARD_CACHE_PREFIX, username);
        if(StringUtils.isBlank(value)){
            myRedisTemplate.set(MyConstants.USER_FORWARD_CACHE_PREFIX, username, JSON.toJSONString(getUser()));
            return null;
        }
        return JSON.parseObject(value, User.class);
    }

    @RequestMapping("/test")
    public User test(@RequestParam("username") String username){

        String value = JedisClusterUtils.get(String.format("%s:%s", MyConstants.USER_FORWARD_CACHE_PREFIX, username));
        if(StringUtils.isBlank(value)){
            JedisClusterUtils.set(String.format("%s:%s", MyConstants.USER_FORWARD_CACHE_PREFIX, username), JSON.toJSONString(getUser()), 1000000000);
            return null;
        }
        return JSON.parseObject(value, User.class);
    }


}