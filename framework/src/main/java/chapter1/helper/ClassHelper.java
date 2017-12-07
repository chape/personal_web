package chapter1.helper;

import chapter1.annotation.Controller;
import chapter1.annotation.Service;
import chapter1.util.ClassUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class ClassHelper {

    private static final Set<Class<?>> CLASS_SET;

    static{
        String basePackage = ConfigHelper.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
    }

    public static Set<Class<?>> getClassSet(){
        return CLASS_SET;
    }

    public static Set<Class<?>> getServiceCLassSet(){
        return CLASS_SET.stream()
                    .filter(c -> c.isAnnotationPresent(Service.class))
                    .collect(Collectors.toSet());
    }

    public static Set<Class<?>> getControllerCLassSet(){
        return CLASS_SET.stream()
                .filter(c -> c.isAnnotationPresent(Controller.class))
                .collect(Collectors.toSet());
    }

    public static Set<Class<?>> getBeanCLassSet(){
        Set<Class<?>> controllerCLassSet = getControllerCLassSet();
        Set<Class<?>> serviceCLassSet = getServiceCLassSet();
        Set<Class<?>> beanClassSet = new HashSet<>(controllerCLassSet.size()+serviceCLassSet.size());
        beanClassSet.addAll(controllerCLassSet);
        beanClassSet.addAll(serviceCLassSet);
        return beanClassSet;
    }
}
