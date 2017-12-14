package core;


import core.bean.Data;
import core.bean.Handler;
import core.bean.Param;
import core.bean.View;
import core.helper.BeanHelper;
import core.helper.ConfigHelper;
import core.helper.ControllerHelper;
import core.util.*;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ChaoChao on 07/12/2017.
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet{

    @Override
    public void init(ServletConfig servletConfig) throws ServletException{
        HelperLoader.init();
        ServletContext servletContext = servletConfig.getServletContext();
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String requestMethod = req.getMethod();
        String requestPath = req.getPathInfo();
        Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
        if(null == handler){
            return;
        }
        Class<?> controllerClass = handler.getControllerClass();
        Object controllerBean = BeanHelper.getBean(controllerClass);

        Map<String,Object> paramMap = new HashMap<>();
        Enumeration<String> paramNames = req.getParameterNames();
        while(paramNames.hasMoreElements()){
            String paramName = paramNames.nextElement();
            String paramValue = req.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }
        String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            String[] params = StringUtil.splitString(body, "&");
            if(ArrayUtil.isNotEmpty(params)){
                Arrays.stream(params).forEach(param -> {
                    String[] array = StringUtil.splitString(param, "=");
                    if(ArrayUtil.isNotEmpty(array) && 2 == array.length){
                        String paramName = array[0];
                        String paramValue = array[1];
                        paramMap.put(paramName, paramValue);
                    }
                });
            }
        }
        Param param = new Param(paramMap);
        Method actionMethod = handler.getActionMethod();

        Object result;
        if(param.isEmpty()){
            result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
        } else {
            result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
        }

        if(result instanceof View){
            View view = (View)result;
            String path = view.getPath();
            if(StringUtil.isNotEmpty(path)){
                if(path.startsWith("/")){
                    res.sendRedirect(req.getContextPath() + path);
                }else{
                    Map<String,Object> model = view.getModel();
                    model.entrySet().stream()
                            .forEach(en -> req.setAttribute(en.getKey(), en.getValue()));
                    req.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(req, res);
                }
            }
        }else if(result instanceof Data){
            Data data = (Data)result;
            Object model = data.getModel();
            if(null != model){
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                PrintWriter writer = res.getWriter();
                String json = JsonUtil.toJson(model);
                writer.write(json);
                writer.flush();
                writer.close();
            }
        }
    }
}
