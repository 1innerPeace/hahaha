package com.atguigu.gmall.order.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.service.OrderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class OrderTask {

    @Reference
    OrderService orderService;

    // 5 每分钟的第五秒
// 0/5 没隔五秒执行一次
    @Scheduled(cron = "5 * * * * ?")
    public void work() {
        System.out.println("Thread ====== " + Thread.currentThread());
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void work1() {
        System.out.println("Thread1 ====== " + Thread.currentThread());
    }

    @Scheduled(cron = "0/20 * * * * ?")
    public void checkOrder() {
        System.out.println("开始处理过期订单");
        long starttime = System.currentTimeMillis();
        List<OrderInfo> expiredOrderList = orderService.getExpiredOrderList();
        for (OrderInfo orderInfo : expiredOrderList) {
            // 处理未完成订单
            orderService.execExpiredOrder(orderInfo);
        }
        long costtime = System.currentTimeMillis() - starttime;
        System.out.println("一共处理" + expiredOrderList.size() + "个订单 共消耗" + costtime + "毫秒");

    }

}
