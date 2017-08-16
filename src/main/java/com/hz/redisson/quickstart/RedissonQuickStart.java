package com.hz.redisson.quickstart;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonQuickStart {
    public static void main(String[] args) {
        Config config=new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient client = Redisson.create(config);
        RAtomicLong atomicLong=client.getAtomicLong("myLong");
        // 同步执行方式
        atomicLong.compareAndSet(3, 401);
        // 异步执行方式
        atomicLong.compareAndSetAsync(3, 401);
        System.out.println(atomicLong.compareAndSet(3,401));
        client.getLock("");
        RBucket<String> rBucket=client.getBucket("e");
        rBucket.compareAndSet(null,"1");
        String bucketString=rBucket.get();
        System.out.println(bucketString);
    }
}
