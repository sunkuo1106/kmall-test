package com.kgc.kmall.kmallcartweb.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    SkuService skuService;

    @Reference
    CartService cartService;

    @RequestMapping("/addToCart")
    public String addToCart(long skuId, Integer num, HttpServletRequest request, HttpServletResponse response){
        List<OmsCartItem> omsCartItems=new ArrayList<>();
        // 调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.selectBySkuId(skuId);
        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(new BigDecimal(skuInfo.getPrice()));
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getSpuId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(num);

        // 判断用户是否登录
        String memberId = "518";

        // 判断用户是否登录
        if(StringUtils.isBlank(memberId)){
            // cookie里原有的购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isBlank(cartListCookie)){
                // cookie为空 、将数据存入购物车集合
                omsCartItems.add(omsCartItem);
            }else{
                // cookie不为空 、 取出该数据
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                // 判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if(exist){
                    // 之前添加过，更新购物车添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            //存在、添加商品数量
                            cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                            break;
                        }
                    }
                }else{
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            // 更新cookie
            CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(omsCartItems),60 * 60 * 72, true);
        }else{
            //思路一：根据用户id查询购物车信息，如果不存在则添加，如果存在判断skuid是否存在，如果不存在则添加，如果存在则修改
            //思路二：根据用户id和skuid查询，如果不存在则添加，如果存在则修改
            // 用户已经登录
            // 从db中查出购物车数据
            OmsCartItem omsCartItemFromDb  = cartService.ifCartExistByUser(memberId,skuId);
            if(omsCartItemFromDb==null){
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(Long.parseLong(memberId));
                omsCartItem.setMemberNickname("test小明");
                cartService.addCart(omsCartItem);
            }else{
                // 该用户添加过当前商品
                Integer quantity = omsCartItemFromDb.getQuantity();
                quantity = quantity+num;
                omsCartItemFromDb.setQuantity(quantity);
                cartService.updateCart(omsCartItemFromDb);
            }
            // 同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem){
        boolean flag=false;
        for (OmsCartItem cartItem : omsCartItems) {
            Long productSkuId = cartItem.getProductSkuId();
            if(productSkuId.equals(omsCartItem.getProductSkuId())){
                //如果为skuid相等那就证明cookie存在该商品
                flag=true;
                break;
            }
        }
        return flag;
    }

    @RequestMapping("/cartList")
    public String cartList(ModelMap modelMap, HttpServletRequest request){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = "518";
        if(StringUtils.isNotBlank(memberId)){
            // 已经登录查询db
            omsCartItems = cartService.cartList(memberId);
        }else{
            // 没登陆查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
            }
        }
        modelMap.put("cartList",omsCartItems);
        return "cartList";
    }
}
