package com.kgc.kmall.user.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.Member;
import com.kgc.kmall.bean.MemberExample;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.user.mapper.MemberMapper;
import com.kgc.kmall.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;

@Component
@Service
public class MemberServiceImpl implements MemberService {
    @Resource
    MemberMapper memberMapper;

    @Resource
    RedisUtil redisUtil;

    @Override
    public List<Member> selectAllMember() {
        List<Member> members = memberMapper.selectByExample(null);
        return members;
    }

    @Override
    public Member login(Member member) {
        //从Redis中获取
        Jedis jedis = null;
        try {
            jedis=redisUtil.getJedis();
            if(jedis!=null){
                String umsMemberStr  = jedis.get("user:" + member.getUsername() + ":info");
                if(StringUtils.isNotBlank(umsMemberStr)){
                    // 密码正确
                    Member umsMemberFromCache = JSON.parseObject(umsMemberStr, Member.class);
                    return umsMemberFromCache;
                }
            }
            //Redis中没有数据查询数据库并存入Redis
            Member umsMemberFromDb =loginFromDb(member);
            if(umsMemberFromDb!=null){
                jedis.setex("user:" + umsMemberFromDb.getUsername() + ":info",60*60*24, JSON.toJSONString(umsMemberFromDb));
            }
            return umsMemberFromDb;
        } finally {
            jedis.close();
        }
    }

    @Override
    public void addUserToken(String token, Long memberId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:"+memberId+":token",60*60*2,token);

        jedis.close();
    }

    private Member loginFromDb(Member member) {
        MemberExample example=new MemberExample();
        MemberExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(member.getUsername());
        criteria.andPasswordEqualTo(member.getPassword());
        List<Member> members = memberMapper.selectByExample(example);
        if (members.size()>0)
            return members.get(0);
        return null;
    }
}
