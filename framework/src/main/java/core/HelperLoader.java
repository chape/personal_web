package core;


import core.helper.BeanHelper;
import core.helper.ClassHelper;
import core.helper.ControllerHelper;
import core.helper.IocHelper;
import core.util.ClassUtil;

import java.util.Arrays;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class HelperLoader {

    public static void init(){
        Class<?>[] classes = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class,
        };
        Arrays.stream(classes).forEach(cls -> ClassUtil.loadClass(cls.getName()));
    }
}
