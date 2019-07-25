package com.atguigu.gmall.manage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {



	@Test
	public void contextLoads() {
		System.out.println("++++++");
		Test1 test1 = new Test1();
		Thread thread = new Thread(test1);
		Thread thread2 = new Thread(test1);
		thread.start();
		thread2.start();

	}

}

class Test1 implements Runnable{

	int nu=50;

	@Override
	public void run() {
		if (nu>0){
			for (int i=0;i<50;i++ ){
				nu-=1;
				System.out.println(nu);
			}

		}
	}
	/*public int test1(){
		if (nu<10){
			for (int i=nu;i<10;i++ ){
                nu+=1;
				System.out.println(nu);
			}

		}
		return nu;
	}*/


}