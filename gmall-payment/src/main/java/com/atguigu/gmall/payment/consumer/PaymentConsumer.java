package com.atguigu.gmall.payment.consumer;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class PaymentConsumer {
    @Autowired
    private PaymentService paymentService;

    @JmsListener(destination = "PAYMENT_RESULT_CHECK_QUEUE",containerFactory = "jmsQueueListener")
    public void consumerPaymentResultCheck(MapMessage mapMessage) throws JMSException {
        // 得到数据
        String outTradeNo = mapMessage.getString("outTradeNo");
        int delaySec = mapMessage.getInt("delaySec");
        int checkCount = mapMessage.getInt("checkCount");
        // 检查
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        System.out.println("开始检查");
        boolean flag = paymentService.checkPayment(paymentInfo);
        System.out.println("支付结果"+flag);
        if (!flag && checkCount!=0){
            System.out.println("再次发送 checkCount="+checkCount);
            paymentService.sendDelayPaymentResult(outTradeNo,delaySec,checkCount-1);
        }
    }
}
