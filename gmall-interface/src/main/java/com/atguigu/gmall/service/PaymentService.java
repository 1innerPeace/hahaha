package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {

    void  savePaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfo);

    public void sendPaymentResult(PaymentInfo paymentInfo,String result);

    boolean checkPayment(PaymentInfo paymentInfoQuery);

    public void sendDelayPaymentResult(String outTradeNo,int delaySec ,int checkCount);

    public  void  closePayment(String orderId);

}
