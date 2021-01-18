package com.kgc.kmall.service;

import com.kgc.kmall.bean.Order;

public interface OrderService {
    String genTradeCode(Long memberId);

    String checkTradeCode(Long valueOf, String tradeCode);

    void saveOrder(Order order);

    Order getOrderByOutTradeNo(String outTradeNo);
}
