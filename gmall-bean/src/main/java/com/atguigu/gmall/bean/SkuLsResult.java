package com.atguigu.gmall.bean;

import com.atguigu.gmall.bean.SkuLsInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SkuLsResult implements Serializable{

    // 属性
    private List<SkuLsInfo> skuLsInfoList;
    private long total;
    private long totalPages;
    private List<String> attrValueIdList;

}
