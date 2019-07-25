package com.atguigu.gmall.cart;

import sun.applet.Main;

import java.util.HashMap;
import java.util.Map;

//递归算法...
public class Recurseion {

    Double hight1 = 100.0;
    Double length = 150.0;
    HashMap<String,Double> map = new HashMap();

    public void recursion( ){
        if (hight1>0) {
            hight1--;
            System.out.println(hight1);
            recursion();
        }
    }
    public Map test(Integer n){
        Double hight = 100.0;
        if (n>1){
            Map map = test(n - 1);
            //Integer hight2 = (Integer) map.get("hight");
            Double length2 = (Double) map.get("length");
            Double L1 = length2+hight/(2<<(n-2));
            Double L2 = hight/(2<<(n-1));
            length = L1 + L2;
            hight = hight/n;
            //map.put("hight",hight);
            map.put("length",length);
            //System.out.println(hight);
            System.out.println(map.get("length"));
            return map;
        }else if (n == 1){
            //map.put("hight",hight);
            map.put("length",length);
            //System.out.println(map.get("hight"));
            System.out.println(map.get("length"));
            return map;
        }else {
            return map;
        }

    }

    public static void main(String[] args ){
        Recurseion recurseion = new Recurseion();
        //recurseion.recursion();


        recurseion.test(12);
    }
}
