package chapter1.bean.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ChaoChao on 11/12/2017.
 * 准确的来讲 这个类为InvocationHandler处理类，不是代理类
 */
public class DynamicProxy implements InvocationHandler{

    private Object target;

    public DynamicProxy(Object target) {
        this.target = target;
    }

    public <T> T getProxy(){
        return (T)Proxy.newProxyInstance(target.getClass().getClassLoader(),
                               target.getClass().getInterfaces(),
                               this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result  = method.invoke(target,args);
        after();
        return result;
    }

    public void before(){
        System.out.println("before");
    }
    public void after(){
        System.out.println("after");
    }

    public static void main(String[] args) {
        // DynamicProxy 准确的来讲 这个类为InvocationHandler处理类，不是代理类，真正的代理类为helloProxy
        Hello helloImpl = new HelloImpl();
        DynamicProxy dynamicProxy = new DynamicProxy(helloImpl);
        Hello helloProxy = dynamicProxy.getProxy();
        helloProxy.say("asd");
    }
}
