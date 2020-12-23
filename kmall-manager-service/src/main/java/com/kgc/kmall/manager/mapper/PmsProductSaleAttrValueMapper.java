package com.kgc.kmall.manager.mapper;

import com.kgc.kmall.bean.PmsProductSaleAttrValue;
import com.kgc.kmall.bean.PmsProductSaleAttrValueExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsProductSaleAttrValueMapper {
    int countByExample(PmsProductSaleAttrValueExample example);

    int deleteByExample(PmsProductSaleAttrValueExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductSaleAttrValue record);

    int insertSelective(PmsProductSaleAttrValue record);

    List<PmsProductSaleAttrValue> selectByExample(PmsProductSaleAttrValueExample example);

    PmsProductSaleAttrValue selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsProductSaleAttrValue record, @Param("example") PmsProductSaleAttrValueExample example);

    int updateByExample(@Param("record") PmsProductSaleAttrValue record, @Param("example") PmsProductSaleAttrValueExample example);

    int updateByPrimaryKeySelective(PmsProductSaleAttrValue record);

    int updateByPrimaryKey(PmsProductSaleAttrValue record);

    //批量添加
    int insertAttrValues(@Param("productId") Long productId, @Param("spuSaleAttrValueList") List<PmsProductSaleAttrValue> spuSaleAttrValueList);
}