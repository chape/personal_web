package core.helper;


import core.annotation.Action;
import core.bean.Handler;
import core.bean.Request;
import core.util.ArrayUtil;
import core.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class ControllerHelper {

    private static final Map<Request, Handler> ACTION_MAP = new HashMap<>();

    static {
        Set<Class<?>> controllerSet = ClassHelper.getControllerCLassSet();
        if(CollectionUtil.isNotEmpty(controllerSet)){
            controllerSet.stream().forEach(c -> {
                Method[] methods = c.getDeclaredMethods();
                if(ArrayUtil.isNotEmpty(methods)){
                    Arrays.stream(methods).filter(m -> m.isAnnotationPresent(Action.class)).forEach(m -> {
                        Action action = m.getAnnotation(Action.class);
                        String mapping = action.value();
                        if(mapping.matches("\\w+:/\\w*")){
                            String[] array = mapping.split(":");
                            if(ArrayUtil.isNotEmpty(array) && 2 == array.length){
                                String requestMethod = array[0];
                                String requestPath = array[1];
                                Request request = new Request(requestMethod,requestPath);
                                Handler handler = new Handler(c, m);
                                ACTION_MAP.put(request,handler);
                            }
                        }
                    });
                }
            });
        }
    }

    public static Handler getHandler(String requestMethod, String requestPath){
        requestMethod = requestMethod.toLowerCase();
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
