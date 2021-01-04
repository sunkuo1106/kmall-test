package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {

    @Resource
    RedisUtil redisUtil;

    @Resource
    SkuService skuService;

    @Test
    void contextLoads() {
        try {
            Jedis jedis = redisUtil.getJedis();
            jedis.set("k1","v1");
            String name = jedis.get("k1");
            System.out.println(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1(){
        List<PmsSkuInfo> allSku = skuService.getAllSku();
        System.out.println(allSku.size());
    }

}
