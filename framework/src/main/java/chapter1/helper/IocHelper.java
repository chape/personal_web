package chapter1.helper;

import chapter1.annotation.Inject;
import chapter1.util.ArrayUtil;
import chapter1.util.CollectionUtil;
import chapter1.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class IocHelper {

    static {
        Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
        if(CollectionUtil.isNotEmpty(beanMap)){
            beanMap.entrySet().forEach(ben -> {
                Class<?> beanClass = ben.getKey();
                Object beanInstance = ben.getValue();
                Field[] fields = beanClass.getDeclaredFields();
                if(ArrayUtil.isNotEmpty(fields)){
                    Arrays.stream(fields)
                            .filter(f -> f.isAnnotationPresent(Inject.class))
                            .forEach(f -> {
                                Class<?> beanFieldCLass = f.getType();
                                Object beanFieldInstance = beanMap.get(beanFieldCLass);
                                if(null != beanFieldInstance){
                                    ReflectionUtil.setField(beanInstance, f, beanFieldInstance);
                                }
                    });
                }
            });
        }
    }
}
