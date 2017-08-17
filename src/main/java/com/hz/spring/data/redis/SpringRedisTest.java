package com.hz.spring.data.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)//表示整合JUnit4进行测试
@ContextConfiguration(locations={"classpath:spring-redis.xml"})//加载spring配置文件
public class SpringRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultRedisScript defaultRedisScript;

    private CountDownLatch countDownLatch=new CountDownLatch(1);




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

    @Test
    public void delayQueueRedisTest() throws InterruptedException {
        startListener();
        countDownLatch.await();
    }


    private void startListener() {
        new Thread(() -> {
            while (true){
                Long size=redisTemplate.opsForZSet().zCard("delayQueue");
                List<Object> txResults= (List<Object>) redisTemplate.execute(new SessionCallback() {
                    @Override
                    public List<Object> execute(RedisOperations operations) throws DataAccessException {
                        operations.multi();
                        long time=System.currentTimeMillis();
                        operations.opsForZSet().rangeByScore("delayQueue",0,time);
                        operations.opsForZSet().removeRangeByScore("delayQueue",0,time);
                        return operations.exec();
                    }
                });
                if(size==null||size==0){
                    Random random=new Random();
                    int i = 0;
                    while (i == 0) {
                        i = random.nextInt(10);
                        if (i != 0) {
                            break;
                        }
                    }
                    System.out.println("i=" + i);
                    redisTemplate.opsForZSet().add("delayQueue", "a" + i, (System.currentTimeMillis() + ((i) * 1000)));
                }
                Set<String> queue= (Set<String>) txResults.get(0);
                if(!queue.isEmpty()) {
                    System.out.print("从延迟队列获得的数据:");
                    for (String str : queue) {
                        System.out.print(str);
                    }
                    System.out.println();
                }
                try {
                    Thread.sleep(300);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        }).start();
    }

}
