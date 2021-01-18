package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.utils.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

@Service
@Component
public class SkuServiceImpl implements SkuService {
    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    RedissonClient redissonClient;

    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        pmsSkuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }

//    @Override
//    public PmsSkuInfo selectBySkuId(Long skuId) {
//        PmsSkuInfo pmsSkuInfo=null;
//        Jedis jedis = redisUtil.getJedis();
//        String key="sku:"+skuId+":info";
//        String skuJson = jedis.get(key);
//        if(skuJson!=null){
//            //缓存中有数据
//            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
//            System.out.println("进缓存");
//        }else{
//            //获取分布式锁
//            String skuLockKey="sku:"+skuId+":lock";
//            String skuLockVal = UUID.randomUUID().toString();
//            String ok = jedis.set(skuLockKey, skuLockVal, "NX", "PX", 60 * 1000);
//            //拿到分布式锁
//            if(ok.equals("OK")){
//                //缓存中没有数据,从数据库中读,并写入redis
//                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
//                if(pmsSkuInfo!=null){
//                    System.out.println("进数据库");
//                    String json = JSON.toJSONString(pmsSkuInfo);
//                    //有效期随机，防止缓存雪崩
//                    Random random=new Random();
//                    int i = random.nextInt(10);
//                    jedis.setex(key,i*60*1000,json);
//                }else{
//                    //防止缓存击穿，添加一个空的键并设置有效期
//                    jedis.setex(key,5*60*1000,"empty");
//                }
//                //写完缓存后要删除分布式锁,获取锁的值,并对比原来的值 确认是否是一把锁
////                String skuLockVal2 = jedis.get(skuLockKey);
////                if(skuLockVal2!=null && skuLockVal2.equals(skuLockVal)){
////                    jedis.del(skuLockKey);
////                }
//                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(skuLockVal));
//            }else{
//                //未拿到所锁，线程睡眠3秒，递归调用
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                selectBySkuId(skuId);
//            }
//        }
//        return pmsSkuInfo;
//    }

    @Override
    public PmsSkuInfo selectBySkuId(Long skuId) {
        PmsSkuInfo pmsSkuInfo=null;
        Jedis jedis = redisUtil.getJedis();
        String key="sku:"+skuId+":info";
        String skuJson = jedis.get(key);
        if(skuJson!=null){
            //缓存中有数据
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            System.out.println("进缓存");
        }else{
            Lock lock = redissonClient.getLock("lock");// 声明锁
            lock.lock();//上锁
            try {
                //缓存中没有数据,从数据库中读,并写入redis
                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
                if(pmsSkuInfo!=null){
                    System.out.println("进数据库");
                    String json = JSON.toJSONString(pmsSkuInfo);
                    //有效期随机，防止缓存雪崩
                    Random random=new Random();
                    int i = random.nextInt(10);
                    if(i<1){
                        i++;
                    }
                    jedis.setex(key,i*60*1000,json);
                }else{
                    //防止缓存击穿，添加一个空的键并设置有效期
                    jedis.setex(key,5*60*1000,"empty");
                }
            }finally {
                jedis.close();
                lock.unlock();// 解锁
            }
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {
        List<PmsSkuInfo> pmsSkuInfos=null;
//        Jedis jedis = redisUtil.getJedis();
//        String key="sku:"+spuId+":values";
//        String json = jedis.get(key);
//        if(json!=null){
//            System.out.println("进缓存");
//            pmsSkuInfos = JSON.parseArray(json, PmsSkuInfo.class);
//        }else{
//            System.out.println("进数据库");
//            pmsSkuInfos = pmsSkuInfoMapper.selectBySpuId(spuId);
//            jedis.set(key, JSON.toJSONString(pmsSkuInfos));
//        }
        pmsSkuInfos = pmsSkuInfoMapper.selectBySpuId(spuId);
        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectByExample(null);
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValueExample example=new PmsSkuAttrValueExample();
            PmsSkuAttrValueExample.Criteria criteria = example.createCriteria();
            criteria.andSkuIdEqualTo(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(example);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(Long productSkuId, BigDecimal price) {
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(productSkuId);
        BigDecimal bigDecimal = new BigDecimal(pmsSkuInfo.getPrice());
        if(price.compareTo(bigDecimal)==0){
            return true;
        }
        return false;
    }
}
