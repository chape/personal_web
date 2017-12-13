package core.helper;

import core.annotation.Aspect;
import core.proxy.AspectProxy;
import core.proxy.Proxy;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ChaoChao on 13/12/2017.
 */
public final class AopHelper {

    //TODO
    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();

        } catch(Exception e){

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
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);

        return proxyClassSet.stream()
                     .filter(pc -> pc.isAnnotationPresent(Aspect.class))
                     .collect(Collectors.toMap(pc -> pc, pc -> createTargetClassSet(pc.getAnnotation(Aspect.class))));
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
