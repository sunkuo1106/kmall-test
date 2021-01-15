package com.kgc.kmall.kmallorderservice.service.impl;

import com.kgc.kmall.service.OrderService;
import com.kgc.kmall.utils.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.UUID;

@Service
@Component
public class OrderServiceImpl implements OrderService {

    @Resource
    RedisUtil redisUtil;

    @Override
    public String genTradeCode(Long memberId) {
        Jedis jedis = redisUtil.getJedis();

        String tradeKey = "user:"+memberId+":tradeCode";

        String tradeCode = UUID.randomUUID().toString();

        jedis.setex(tradeKey,60*15,tradeCode);

        jedis.close();

        return tradeCode;
    }

    @Override
    public String checkTradeCode(Long valueOf, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:"+valueOf+":tradeCode";
        String code = jedis.get(tradeKey);
        jedis.close();
        if(code!=null && code.equals(tradeCode)){
            jedis.del(tradeKey);
            return "success";
        }else{
            return "fail";
        }
    }
}
