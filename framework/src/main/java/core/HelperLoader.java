package core;


import core.helper.*;
import core.util.ClassUtil;

import java.util.Arrays;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class HelperLoader {

    /**
     * AopHelper一定要在IocHelper之前,不然IocHelper注入不了代理对象
     */
    public static void init(){
        Class<?>[] classes = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class,
        };
        Arrays.stream(classes).forEach(cls -> ClassUtil.loadClass(cls.getName()));
    }
}
