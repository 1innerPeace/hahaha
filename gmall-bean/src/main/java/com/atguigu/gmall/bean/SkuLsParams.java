package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class SkuLsParams implements Serializable{
    //  属性  keyword == skuName
    private String  keyword;
    private String catalog3Id;
    private String[] valueId;
    // 设置分页使用的
    private int pageNo=1;
    private int pageSize=20;

}
