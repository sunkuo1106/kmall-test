package com.kgc.kmall.kmallpassportweb.controller;

import com.kgc.kmall.bean.Member;
import com.kgc.kmall.service.MemberService;
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
}
