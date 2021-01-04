package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSkuInfo;

import java.util.List;

public interface SkuService {
    public String saveSkuInfo(PmsSkuInfo skuInfo);

    PmsSkuInfo selectBySkuId(Long id);

    List<PmsSkuInfo> selectBySpuId(Long spuId);

    //查询全部sku的方法
    List<PmsSkuInfo> getAllSku();
}
