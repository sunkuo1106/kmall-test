package com.kgc.kmall.manager.mapper;

import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsSkuInfoMapper {
    int countByExample(PmsSkuInfoExample example);

    int deleteByExample(PmsSkuInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuInfo record);

    int insertSelective(PmsSkuInfo record);

    List<PmsSkuInfo> selectByExample(PmsSkuInfoExample example);

    PmsSkuInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsSkuInfo record, @Param("example") PmsSkuInfoExample example);

    int updateByExample(@Param("record") PmsSkuInfo record, @Param("example") PmsSkuInfoExample example);

    int updateByPrimaryKeySelective(PmsSkuInfo record);

    int updateByPrimaryKey(PmsSkuInfo record);
}