package com.atguigu.gmall.payment;

import com.atguigu.gmall.config.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPaymentApplicationTests {

	@Autowired
	private ActiveMQUtil activeMQUtil;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testMQ() throws JMSException {
		// ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.67.215:61616");
		Connection connection = activeMQUtil.getConnection();
		//		Connection connection = activeMQConnectionFactory.createConnection();
		// 打开工厂连接
		connection.start();
		// 第一个参数表示是否开启事务 true, 开启事务true, Session.SESSION_TRANSACTED，false 关闭事务
		// 第二个参数与第一个参数有关联，
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
		// 调用session 创建一个队列
		Queue queue = session.createQueue("atguigu-test");
		//        Queue queue = session.createQueue("atguigu001");
		// 队列中放消息提供者
		MessageProducer producer = session.createProducer(queue);

		// 创建要发送的消息对象
		ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
		activeMQTextMessage.setText("测试工具类！！");
		// 发送消息
		producer.send(activeMQTextMessage);

		// 消息提交
//        session.commit();

		// 关闭操作
		producer.close();
		session.close();
		connection.close();
	}
}
