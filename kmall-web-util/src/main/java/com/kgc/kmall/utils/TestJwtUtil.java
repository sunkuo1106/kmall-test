package com.kgc.kmall.utils;

import java.util.HashMap;
import java.util.Map;

public class TestJwtUtil {
    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("memberId","1");
        map.put("nickname","zhangsan");
        String ip = "127.0.0.1";
//        String time = new SimpleDateFormat("yyyyMMdd HHmm").format(new Date());
        String encode = JwtUtil.encode("2021kmall077", map, ip );
        System.err.println(encode);
    }

    public static void test(){
        Map<String, Object> decode = JwtUtil.decode("eyJhbGciOiJIUzI1NiJ9.eyJuaWNrbmFtZSI6InpoYW5nc2FuIiwibWVtYmVySWQiOiIxIn0.eg_xHfMNMsiXCiAJAAA04sQXPwW8GfB7Q93w68mJkDc", "2020kmall075", "127.0.0.1");
        System.out.println(decode);
    }
}
