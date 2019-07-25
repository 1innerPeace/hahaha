package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.enums.ProcessStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {

    public  String  saveOrder(OrderInfo orderInfo);

    boolean checkStock(String skuId, Integer skuNum);

    public  String getTradeNo(String userId);

    public  boolean checkTradeCode(String userId,String tradeCodeNo);

    public void  delTradeCode(String userId);

    public OrderInfo getOrderInfo(String orderId);

    void updateOrderStatus(String orderId, ProcessStatus processStatus);

    public void sendOrderStatus(String orderId);

    public List<OrderInfo> getExpiredOrderList();

    public  void execExpiredOrder(OrderInfo orderInfo);

    Map initWareOrder(OrderInfo orderInfo);

    List<OrderInfo> splitOrder(String orderId, String wareSkuMap);
}
