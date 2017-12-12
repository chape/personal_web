package core.bean.test;

import java.io.IOException;

/**
 * Created by ChaoChao on 12/12/2017.
 */
public class PerformanceTest {

    private static int creation = 10000;
    private static int execution = 10000;

    public static void main(String[] args) throws IOException {
//        testJDKDynamicCreation();
        testJDKDynamicExecution();
//        testCglibCreation();
//        testCglibExecution();
    }

    private static void testJDKDynamicCreation() {
        long start = System.currentTimeMillis();
        Hello helloImpl = new HelloImpl();
        for (int i = 0; i < creation; i++) {
            DynamicProxy dynamicProxy = new DynamicProxy(helloImpl);
            Hello helloProxy = dynamicProxy.getProxy();
        }
        long stop = System.currentTimeMillis();
        System.out.println("JDK creation time : "+(stop - start)+"ms");
    }

    private static void testJDKDynamicExecution() {
        Hello helloImpl = new HelloImpl();
        DynamicProxy dynamicProxy = new DynamicProxy(helloImpl);
        Hello helloProxy = dynamicProxy.getProxy();
        long start = System.currentTimeMillis();
        for (int i = 0; i < execution; i++) {
            helloProxy.say("proxy");
        }
        long stop = System.currentTimeMillis();
        System.out.println("JDK execution time : "+(stop - start)+"ms");
    }

    private static void testCglibCreation() {
        long start = System.currentTimeMillis();
        CGLibProxy cgLibProxy = CGLibProxy.getInstance();
        for (int i = 0; i < creation; i++) {
            Hello helloProxy = cgLibProxy.getProxy(HelloImpl.class);
        }
        long stop = System.currentTimeMillis();
        System.out.println("cglib creation time : "+(stop - start)+"ms");
    }

    private static void testCglibExecution() {
        CGLibProxy cgLibProxy = CGLibProxy.getInstance();
        Hello helloProxy = cgLibProxy.getProxy(HelloImpl.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < execution; i++) {
            helloProxy.say("proxy");
        }
        long stop = System.currentTimeMillis();
        System.out.println("cglib execution time : "+(stop - start)+"ms");
    }
}
