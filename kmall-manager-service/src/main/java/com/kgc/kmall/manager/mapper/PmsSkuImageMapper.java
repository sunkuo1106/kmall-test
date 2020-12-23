package com.kgc.kmall.manager.mapper;

import com.kgc.kmall.bean.PmsSkuImage;
import com.kgc.kmall.bean.PmsSkuImageExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsSkuImageMapper {
    int countByExample(PmsSkuImageExample example);

    int deleteByExample(PmsSkuImageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuImage record);

    int insertSelective(PmsSkuImage record);

    List<PmsSkuImage> selectByExample(PmsSkuImageExample example);

    PmsSkuImage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsSkuImage record, @Param("example") PmsSkuImageExample example);

    int updateByExample(@Param("record") PmsSkuImage record, @Param("example") PmsSkuImageExample example);

    int updateByPrimaryKeySelective(PmsSkuImage record);

    int updateByPrimaryKey(PmsSkuImage record);
}