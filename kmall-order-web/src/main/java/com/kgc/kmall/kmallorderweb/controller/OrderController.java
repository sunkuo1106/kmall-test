package com.kgc.kmall.kmallorderweb.controller;

import com.kgc.kmall.annotations.LoginRequired;
import com.kgc.kmall.bean.MemberReceiveAddress;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.OrderItem;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.service.MemberService;
import com.kgc.kmall.service.OrderService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    MemberService memberService;

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;

    @RequestMapping("/toTrade")
    @LoginRequired(value = true)
    public String toTrade(HttpServletRequest request, Model model){
        //从拦截器中获取用户memberid和nickname
        Integer memberId = (Integer) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");

        // 收件人地址列表
        List<MemberReceiveAddress> umsMemberReceiveAddresses = memberService.getReceiveAddressByMemberId(Long.valueOf(memberId));
        model.addAttribute("userAddressList",umsMemberReceiveAddresses);

        // 将购物车集合转化为页面计算清单集合
        List<OmsCartItem> omsCartItems = cartService.cartList(memberId.toString());
        List<OrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            // 每循环一个购物车对象，就封装一个商品的详情到OmsOrderItem
            if (omsCartItem.getIsChecked()==1) {
                OrderItem omsOrderItem = new OrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItems.add(omsOrderItem);
            }
        }

        model.addAttribute("omsOrderItems", omsOrderItems);
        model.addAttribute("totalAmount", getTotalAmount(omsCartItems));

        //生成交易码
        String tradeCode = orderService.genTradeCode(Long.valueOf(memberId));
        System.out.println(tradeCode);
        model.addAttribute("tradeCode", tradeCode);

        return "trade";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if (omsCartItem.getIsChecked()==1) {
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

    @RequestMapping("submitOrder")
    @LoginRequired(value = true)
    public String submitOrder(String receiveAddressId, BigDecimal totalAmount, String tradeCode, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        //从拦截器中获取用户memberid和nickname
        Integer memberId = (Integer) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        // 检查交易码
        String success = orderService.checkTradeCode(Long.valueOf(memberId), tradeCode);
        if (success.equals("success")) {
            System.out.println("提交订单");
            System.out.println(receiveAddressId);
            System.out.println(totalAmount);
        }else{
            model.addAttribute("errMsg","获取用户订单失败!");
            return "tradeFail";
        }
        return null;
    }

}
