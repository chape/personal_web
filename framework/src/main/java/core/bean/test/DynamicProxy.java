package core.bean.test;

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
}
