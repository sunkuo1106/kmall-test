package com.kgc.kmall.bean;

import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "kmall",type = "PmsSkuInfo")
public class PmsSearchSkuInfo implements Serializable {
    private Long id;
    private String skuName;
    private String skuDesc;
    private Long catalog3Id;
    private Double price;
    private String skuDefaultImg;
    private Double hotScore;
    private Long productId;
    private List<PmsSkuAttrValue> skuAttrValueList;

    public PmsSearchSkuInfo() {
    }

    public PmsSearchSkuInfo(Long id, String skuName, String skuDesc, Long catalog3Id, Double price, String skuDefaultImg, Double hotScore, Long productId, List<PmsSkuAttrValue> skuAttrValueList) {
        this.id = id;
        this.skuName = skuName;
        this.skuDesc = skuDesc;
        this.catalog3Id = catalog3Id;
        this.price = price;
        this.skuDefaultImg = skuDefaultImg;
        this.hotScore = hotScore;
        this.productId = productId;
        this.skuAttrValueList = skuAttrValueList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDesc() {
        return skuDesc;
    }

    public void setSkuDesc(String skuDesc) {
        this.skuDesc = skuDesc;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSkuDefaultImg() {
        return skuDefaultImg;
    }

    public void setSkuDefaultImg(String skuDefaultImg) {
        this.skuDefaultImg = skuDefaultImg;
    }

    public Double getHotScore() {
        return hotScore;
    }

    public void setHotScore(Double hotScore) {
        this.hotScore = hotScore;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<PmsSkuAttrValue> getSkuAttrValueList() {
        return skuAttrValueList;
    }

    public void setSkuAttrValueList(List<PmsSkuAttrValue> skuAttrValueList) {
        this.skuAttrValueList = skuAttrValueList;
    }

    @Override
    public String toString() {
        return "PmsSearchSkuInfo{" +
                "id=" + id +
                ", skuName='" + skuName + '\'' +
                ", skuDesc='" + skuDesc + '\'' +
                ", catalog3Id=" + catalog3Id +
                ", price=" + price +
                ", skuDefaultImg='" + skuDefaultImg + '\'' +
                ", hotScore=" + hotScore +
                ", productId=" + productId +
                ", skuAttrValueList=" + skuAttrValueList +
                '}';
    }
}
