package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CartService {

    public  void  addToCart(String skuId,String userId,Integer skuNum);

    // 缓存中没有数据，则从 数据库中加载
    public List<CartInfo> loadCartCache(String userId);

    // 查询购物车集合列表
    public List<CartInfo> getCartList(String userId);

    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId);

    public  void  checkCart(String skuId,String isChecked,String userId);

    // 得到选中购物车列表
    public  List<CartInfo> getCartCheckedList(String userId);

}
