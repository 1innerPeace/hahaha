package com.atguigu.gmall.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    ManageService manageService;

    @Reference
    ListService listService;

    @RequestMapping("spuImageList")
    @ResponseBody
    public List<SpuImage> spuImageList(String spuId){
        return   manageService.getSpuImageList(spuId);
    }

    @RequestMapping(value="attrInfoList",method = RequestMethod.GET )
    @ResponseBody
    public List<BaseAttrInfo> attrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);
    }

    @RequestMapping("spuSaleAttrList")
    @ResponseBody
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId){
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrList(spuId);
        return  spuSaleAttrList;
    }

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody SkuInfo skuInfo){
        manageService.saveSkuInfo(skuInfo);
        return "OK";
    }


    @RequestMapping(value="onSale",method = RequestMethod.GET)
    @ResponseBody
    public String onSale(String skuId){
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //拷贝属性
        BeanUtils.copyProperties(skuInfo,skuLsInfo);
        listService.saveSkuInfo(skuLsInfo);
        return "OK";
    }

}
