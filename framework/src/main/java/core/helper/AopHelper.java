package core.helper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import core.annotation.Aspect;
import core.annotation.Service;
import core.proxy.AspectProxy;
import core.proxy.Proxy;
import core.proxy.ProxyManager;
import core.proxy.TransactionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ChaoChao on 13/12/2017.
 */
public final class AopHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            targetMap.entrySet().stream().forEach(en -> {
                Class<?> targetClass = en.getKey();
                List<Proxy> proxyList = en.getValue();
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                BeanHelper.setBean(targetClass, proxy);
            });
        } catch(Exception e){
            LOGGER.error("aop failure", e);
        }
    }

    /**
     * 获取注解了Aspect注解值Class的类
     * @param aspect
     * @return
     * @throws Exception
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) {
        Class<? extends Annotation> annotation = aspect.value();
        if(null != annotation && !annotation.equals(Aspect.class)){
            return ClassHelper.getClassSetByAnnotation(annotation);
        }
        return Collections.emptySet();
    }

    /**
     * 构建<代理类-目标类集合>映射关系
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     * 构建<普通代理类(AspectProxy子类)-目标类集合>映射关系
     * @param proxyMap
     * @throws Exception
     */
    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);

        proxyClassSet.stream()
                .filter(pc -> pc.isAnnotationPresent(Aspect.class))
                .forEach(pc -> {
                    Aspect aspect = pc.getAnnotation(Aspect.class);
                    Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                    proxyMap.put(pc, targetClassSet);
                });
    }

    /**
     * 构建<事务代理类-目标类集合(所有Service注解类)>映射关系
     * @param proxyMap
     * @throws Exception
     */
    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);

        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }

    /**
     * 构建<目标类-代理对象列表>映射关系
     * @param proxyMap
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception{
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();

        proxyMap.entrySet().stream().forEach(en -> {
            Class<?> proxyClass = en.getKey();
            Set<Class<?>> targetClassList = en.getValue();
            targetClassList.stream().forEach(targetClass -> {
                Proxy proxy;
                try {
                    proxy = (Proxy)proxyClass.newInstance();
                } catch (Exception e){
                    throw new RuntimeException(e);
                }
                if(targetMap.containsKey(targetClass)){
                    targetMap.get(targetClass).add(proxy);
                }else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass,proxyList);
                }
            });

        });
        return targetMap;
    }
}
