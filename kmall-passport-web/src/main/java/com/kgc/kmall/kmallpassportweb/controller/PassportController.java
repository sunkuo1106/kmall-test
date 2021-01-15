package com.kgc.kmall.kmallpassportweb.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.utils.HttpclientUtil;
import com.kgc.kmall.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    MemberService memberService;

    @RequestMapping("index")
    public String index(String ReturnUrl, Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(Member member, HttpServletRequest request){
        String token = "";
        // 调用用户服务验证用户名和密码
        Member umsMember = memberService.login(member);
        if(umsMember!=null){
            // // 用jwt制作token
            Map<String,Object> map = new HashMap<>();
            map.put("memberId",umsMember.getId());
            map.put("nickname",umsMember.getNickname());

            // 如果使用了nginx，则需要如此获取客户端ip
//            String ip = request.getHeader("x-forwarded-for");
            //如果没有使用nginx
            String ip = request.getRemoteAddr();// 从request中获取ip
            if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
                ip = "127.0.0.1";
            }
            token = JwtUtil.encode("2021kmall077", map, ip );
            System.err.println(token);

            // 将token存入redis一份
            memberService.addUserToken(token,umsMember.getId());
        }else{
            //验证不成功
            token = "fail";
        }
        return token;
    }

    @RequestMapping("verify")
    @ResponseBody
    public Map<String,Object> verify(String token,String currentIp,HttpServletRequest request){
        // 通过jwt校验token真假
        Map<String,Object> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "2021kmall077", currentIp);
        if(decode!=null){
            map.put("status","success");
            Object memberId = decode.get("memberId");
            map.put("memberId",memberId);
            map.put("nickname",decode.get("nickname"));
        }else{
            map.put("status","fail");
        }
        return map;
    }

    @RequestMapping("/vlogin")
    public String vlogin(String code,HttpServletRequest request){
        //code接收授权码

        //根据授权码获取access_token
        String s3 = "https://api.weibo.com/oauth2/access_token";
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id","3329732143");
        paramMap.put("client_secret","e4c7691568ce77db4aa2e0ab4742803d");
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri","http://passport.kmall.com:8086/vlogin");
        paramMap.put("code",code);// 授权有效期内可以使用，没新生成一次授权码，说明用户对第三方数据进行重启授权，之前的access_token和授权码全部过期
        String access_token_json = HttpclientUtil.doPost(s3, paramMap);

        Map<String,String> access_map = JSON.parseObject(access_token_json,Map.class);

        String access_token = access_map.get("access_token");
        String uid = access_map.get("uid");

        //根据access_token获取用户信息
        // 4 用access_token查询用户信息
        String s4 = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;
        String user_json = HttpclientUtil.doGet(s4);
        Map<String,String> user_map = JSON.parseObject(user_json,Map.class);
        System.out.println(user_map);

        // 将用户信息保存数据库，用户类型设置为微博用户
        Member umsMember=new Member();
        umsMember.setSourceType(2);
        //umsMember.setAccessToken(code);
        umsMember.setAccessToken(access_token);
        umsMember.setSourceUid(Long.parseLong((String) user_map.get("idstr")));
        umsMember.setCity((String)user_map.get("location"));
        umsMember.setNickname((String)user_map.get("screen_name"));
        Integer g = 0;
        String gender = user_map.get("gender");
        if(gender.equals("m")){
            g = 1;
        }
        umsMember.setGender(g);

        Member umsMemberCheck = memberService.checkOauthUser(umsMember.getSourceUid());

        if(umsMemberCheck==null){
            memberService.addOauthUser(umsMember);
        }else{
            umsMember = umsMemberCheck;
        }

        // 用jwt制作token
        String token = "";
        Long memberId = umsMember.getId();
        String nickname = umsMember.getNickname();
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("memberId",memberId);
        userMap.put("nickname",nickname);

        // 如果使用了nginx，则需要如此获取客户端ip
        //            String ip = request.getHeader("x-forwarded-for");
        //如果没有使用nginx
        String ip = request.getRemoteAddr();// 从request中获取ip
        if(StringUtils.isBlank(ip)||ip.equals("0:0:0:0:0:0:0:1")){
            ip = "127.0.0.1";
        }

        // 按照设计的算法对参数进行加密后，生成token
        token = JwtUtil.encode("2021kmall077", userMap, ip);

        // 将token存入redis一份
        memberService.addUserToken(token,memberId);

        return "redirect:http://127.0.0.1:8084/index.html?token="+token;
    }
}
