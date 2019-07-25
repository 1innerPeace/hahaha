package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {


    @Reference
    ManageService manageService;
    @Reference
    ListService listService;


    @LoginRequire(autoRedirect = true)
    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId,HttpServletRequest request ){
        System.out.println("商品ID："+skuId);
        // 存储基本的skuInfo信息
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        System.out.println("skuInfo:"+skuInfo);
        //model.addAttribute("skuInfo",skuInfo);
        // 存储 spu，sku数据
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        System.out.println("spuSaleAttrList:"+spuSaleAttrList);
        //model.addAttribute("saleAttrList",saleAttrList);
// 获取销售属性值集合，以及skuId
        System.out.println(skuInfo.getSpuId());
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        System.out.println("skuSaleAttrValueListBySpu:"+skuSaleAttrValueListBySpu);
        //        144	39	黑色
        //        147	39	4G+64G
        //        145	40	金色
        //        147	40	4G+64G

        /*
            生成map
            map.put("144|147",39)
			map.put("145|147",40)
          */
        //把列表变换成 valueid1|valueid2|valueid3 ：skuId  的 哈希表 用于在页面中定位查询
        String valueIdsKey="";

        Map<String,String> valuesSkuMap=new HashMap<>();

        for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
            if(valueIdsKey.length()!=0){
                valueIdsKey= valueIdsKey+"|";
            }
            valueIdsKey=valueIdsKey+skuSaleAttrValue.getSaleAttrValueId();

            if((i+1)== skuSaleAttrValueListBySpu.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())  ){

                valuesSkuMap.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                valueIdsKey="";
            }

        }

        //把map变成json串
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);
        System.out.println("valuesSkuJson:"+valuesSkuJson);
        /*model.addAttribute("skuInfo",skuInfo);
        model.addAttribute("valuesSkuJson",valuesSkuJson);
        model.addAttribute("spuSaleAttrList",spuSaleAttrList);*/

        request.setAttribute("valuesSkuJson",valuesSkuJson);
        request.setAttribute("skuInfo",skuInfo);
        request.setAttribute("spuSaleAttrList",spuSaleAttrList);

        listService.incrHotScore(skuId);  //最终应该由异步方式调用
        return "item";
    }



}
