package com.kgc.kmall.manager.mapper;

import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.bean.PmsSkuAttrValueExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsSkuAttrValueMapper {
    int countByExample(PmsSkuAttrValueExample example);

    int deleteByExample(PmsSkuAttrValueExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuAttrValue record);

    int insertSelective(PmsSkuAttrValue record);

    List<PmsSkuAttrValue> selectByExample(PmsSkuAttrValueExample example);

    PmsSkuAttrValue selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsSkuAttrValue record, @Param("example") PmsSkuAttrValueExample example);

    int updateByExample(@Param("record") PmsSkuAttrValue record, @Param("example") PmsSkuAttrValueExample example);

    int updateByPrimaryKeySelective(PmsSkuAttrValue record);

    int updateByPrimaryKey(PmsSkuAttrValue record);
}