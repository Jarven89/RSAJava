package test;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hyshi on 01/05/2017.
 */
public  class TestAbstatic {
    public static void main(String[] args) {
        ConcurrentHashMap map = new ConcurrentHashMap();
        map.put("x",1);
        int i =0;
        Integer j = new Integer(0);
        System.out.println(i == j );
        System.out.println(j.equals(i));
    }

}
