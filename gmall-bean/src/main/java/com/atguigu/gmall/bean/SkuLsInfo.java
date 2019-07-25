package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable{

   String id;

   BigDecimal price;

   String skuName;

   String skuDesc;

   String catalog3Id;

   String skuDefaultImg;

   // 热度排名计数器
   Long hotScore=0L;

   // 平台属性值ID集合
   List<SkuLsAttrValue> skuAttrValueList;

}
