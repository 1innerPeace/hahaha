package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.enums.OrderStatus;
import com.atguigu.gmall.bean.enums.ProcessStatus;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Reference
    private UserService userService;
    @Reference
    private CartService cartService;
    @Reference
    private OrderService orderService;


    /*@RequestMapping("trade")
    @ResponseBody
    public List<UserAddress> trade(HttpServletRequest request){
        String userId = request.getParameter("userId");
        List<UserAddress> userAddressList = userService.findAddressByUserId(userId);
        return  userAddressList;
    }*/


    @RequestMapping(value = "trade",method = RequestMethod.GET)
    @LoginRequire(autoRedirect = true)
    public  String tradeInit(HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        // 得到选中的购物车列表
        List<CartInfo> cartCheckedList = cartService.getCartCheckedList(userId);
        // 收货人地址
        List<UserAddress> userAddressList = userService.findAddressByUserId(userId);
        request.setAttribute("userAddressList",userAddressList);
        // 订单信息集合
        List<OrderDetail> orderDetailList=new ArrayList<>(cartCheckedList.size());
        for (CartInfo cartInfo : cartCheckedList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetailList.add(orderDetail);
        }
        request.setAttribute("orderDetailList",orderDetailList);
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());
        // 获取TradeCode号
        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeCode",tradeNo);
        return  "trade";
    }

    @RequestMapping(value = "submitOrder",method = RequestMethod.POST)
    @LoginRequire(autoRedirect = true)
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        // 检查tradeCode
        String tradeNo = request.getParameter("tradeNo");
        boolean flag = orderService.checkTradeCode(userId, tradeNo);
        if (!flag){
            request.setAttribute("errMsg","不能重复提交表单！");
            return "tradeFail";
        }
        // 初始化参数
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        orderInfo.sumTotalAmount();
        orderInfo.setUserId(userId);
        // 校验，验价
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        for (OrderDetail orderDetail : orderDetailList) {
            // 从订单中去购物skuId，数量
            boolean result = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!result){
                request.setAttribute("errMsg","商品库存不足，请重新下单！");
                return "tradeFail";
            }
        }
        String orderId = orderService.saveOrder(orderInfo);
        // 删除tradeNo
        orderService.delTradeCode(userId);
        return "redirect://payment.gmall.com/index?orderId="+orderId;
    }


    @RequestMapping("orderSplit")
    @ResponseBody
    public String orderSplit(HttpServletRequest request){
        String orderId = request.getParameter("orderId");
        String wareSkuMap = request.getParameter("wareSkuMap");
        // 定义订单集合
        List<OrderInfo> subOrderInfoList = orderService.splitOrder(orderId,wareSkuMap);
        List<Map> wareMapList=new ArrayList<>();
        for (OrderInfo orderInfo : subOrderInfoList) {
            Map map = orderService.initWareOrder(orderInfo);
            wareMapList.add(map);
        }
        return JSON.toJSONString(wareMapList);
    }

}
