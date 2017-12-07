package chapter1.helper;


import chapter1.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class BeanHelper {
    private static final Map<Class<?>,Object> BEAN_MAP = new HashMap<>();

    static{
        Set<Class<?>> beanClassSet = ClassHelper.getBeanCLassSet();
        beanClassSet.stream().forEach(bc -> {
            Object obj = ReflectionUtil.newInstance(bc);
            BEAN_MAP.put(bc,obj);
        });
    }

    public static Map<Class<?>, Object> getBeanMap(){
        return BEAN_MAP;
    }

    public static <T> T getBean(Class<T> cls){
        if(!BEAN_MAP.containsKey(cls)){
            throw new RuntimeException("can not get bean by class: " + cls);
        }
        return (T)BEAN_MAP.get(cls);
    }
}
