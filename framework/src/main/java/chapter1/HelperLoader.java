package chapter1;


import chapter1.helper.BeanHelper;
import chapter1.helper.ClassHelper;
import chapter1.helper.ControllerHelper;
import chapter1.helper.IocHelper;
import chapter1.util.ClassUtil;

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
