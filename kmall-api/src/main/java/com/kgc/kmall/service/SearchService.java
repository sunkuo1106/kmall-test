package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> list(PmsSearchSkuParam pmsSearchSkuParam);
}
