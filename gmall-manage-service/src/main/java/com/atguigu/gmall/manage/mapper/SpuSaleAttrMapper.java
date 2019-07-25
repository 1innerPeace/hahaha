package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr>{

    public List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);

    List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(String skuId, String spuId);
}
