package core.bean.test;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by ChaoChao on 12/12/2017.
 */
public class CGLibProxy implements MethodInterceptor{

    private CGLibProxy(){}

    private static class CGLibProxyHolder{
        private final static CGLibProxy INSTANCE = new CGLibProxy();
    }

    public static CGLibProxy getInstance(){
        return CGLibProxyHolder.INSTANCE;
    }

    public <T> T getProxy(Class<T> cls){
        return (T) Enhancer.create(cls, this);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        before();
        Object result = methodProxy.invokeSuper(o,args);
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
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "com/sun/cglib");
        CGLibProxy cgLibProxy = CGLibProxy.getInstance();
        Hello helloProxy = cgLibProxy.getProxy(HelloImpl.class);
        helloProxy.say("asd");
    }
}
