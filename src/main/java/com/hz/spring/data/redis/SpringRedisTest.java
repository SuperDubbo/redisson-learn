package com.hz.spring.data.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)//表示整合JUnit4进行测试
@ContextConfiguration(locations={"classpath:spring-redis.xml"})//加载spring配置文件
public class SpringRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultRedisScript defaultRedisScript;


    @Test
    public void springRedisTest(){
//        redisTemplate.opsForValue().set("aaa","123");
        String key="ess";
//        redisTemplate.opsForValue().set(key,"456",1000, TimeUnit.SECONDS);
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<Boolean>();
        redisScript.setScriptText("local currValue = redis.call('get', KEYS[1]); "
                + "if currValue == ARGV[1] "
                + "or (tonumber(ARGV[1]) == 0 and currValue == false) then "
                + "redis.call('set', KEYS[1], ARGV[2]); "
                    + "if(tonumber(ARGV[3])~=-1) then "
                    + "redis.call('EXPIRE',KEYS[1],ARGV[3]); "
                    + "end "
                + "return 1 "
                + "else "
                + "redis.call('set', 'e',currValue); "
                + "return 0 "
                + "end");
        redisScript.setResultType(Boolean.class);
        System.out.println(redisTemplate.execute(redisScript, Collections.singletonList(key),"11","110","120"));
        System.out.println(redisTemplate.opsForValue().get(key));
    }

}
