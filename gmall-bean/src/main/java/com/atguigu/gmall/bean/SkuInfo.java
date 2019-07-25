package com.atguigu.gmall.bean;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfo implements Serializable{

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String spuId;

    @Column
    private BigDecimal price;

    @Column
    private  String skuName;

    @Column
    private BigDecimal weight;

    @Column
    private String skuDesc;

    @Column
    private String catalog3Id;

    @Column
    private String skuDefaultImg;


    @Transient
    private List<SkuSaleAttrValue> skuSaleAttrValueList;

    @Transient
    private List<SkuImage> skuImageList;

    @Transient
    private List<SkuAttrValue> skuAttrValueList;


}
