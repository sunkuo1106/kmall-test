package com.kgc.kmall.service;

import com.kgc.kmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    public OmsCartItem ifCartExistByUser(String memberId, long skuId);

    public void addCart(OmsCartItem omsCartItem);

    public void updateCart(OmsCartItem omsCartItemFromDb);

    public void flushCartCache(String memberId);

    public List<OmsCartItem> cartList(String memberId);
}
