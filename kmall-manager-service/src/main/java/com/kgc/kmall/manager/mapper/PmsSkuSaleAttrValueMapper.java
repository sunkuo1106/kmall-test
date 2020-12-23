package com.kgc.kmall.manager.mapper;

import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.bean.PmsSkuSaleAttrValueExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsSkuSaleAttrValueMapper {
    int countByExample(PmsSkuSaleAttrValueExample example);

    int deleteByExample(PmsSkuSaleAttrValueExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuSaleAttrValue record);

    int insertSelective(PmsSkuSaleAttrValue record);

    List<PmsSkuSaleAttrValue> selectByExample(PmsSkuSaleAttrValueExample example);

    PmsSkuSaleAttrValue selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsSkuSaleAttrValue record, @Param("example") PmsSkuSaleAttrValueExample example);

    int updateByExample(@Param("record") PmsSkuSaleAttrValue record, @Param("example") PmsSkuSaleAttrValueExample example);

    int updateByPrimaryKeySelective(PmsSkuSaleAttrValue record);

    int updateByPrimaryKey(PmsSkuSaleAttrValue record);
}