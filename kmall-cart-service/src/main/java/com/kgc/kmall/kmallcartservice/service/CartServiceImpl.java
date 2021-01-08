package com.kgc.kmall.kmallcartservice.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.OmsCartItemExample;
import com.kgc.kmall.kmallcartservice.mapper.OmsCartItemMapper;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.utils.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service
public class CartServiceImpl implements CartService {

    @Resource
    OmsCartItemMapper omsCartItemMapper;

    @Resource
    RedisUtil redisUtil;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, long skuId) {
        OmsCartItemExample example=new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(Long.parseLong(memberId)).andProductSkuIdEqualTo(skuId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        if(omsCartItems!=null && omsCartItems.size()>0){
            return omsCartItems.get(0);
        }
        return null;
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        omsCartItemMapper.updateByPrimaryKeySelective(omsCartItemFromDb);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItemExample example=new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(Long.parseLong(memberId));
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(example);
        //创建redis连接
        Jedis jedis = redisUtil.getJedis();
        Map<String,String> map = new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            map.put(omsCartItem.getProductSkuId().toString(), JSON.toJSONString(omsCartItem));
        }
        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String memberId) {
        Jedis jedis = null;
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        try {
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
            if(hvals!=null){
                //redis中存在数据
                for (String hval : hvals) {
                    OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                    omsCartItems.add(omsCartItem);
                }
            }else{
                //redis中不存在查询数据库
                OmsCartItemExample example=new OmsCartItemExample();
                example.createCriteria().andMemberIdEqualTo(Long.parseLong(memberId));
                omsCartItems = omsCartItemMapper.selectByExample(example);
            }
        } catch (Exception e) {
            // 处理异常，记录系统日志
            e.printStackTrace();
            //String message = e.getMessage();
            //logService.addErrLog(message);
            return null;
        }finally {
            jedis.close();
        }
        return omsCartItems;
    }
}
