package core;


import core.bean.Data;
import core.bean.Handler;
import core.bean.Param;
import core.bean.View;
import core.helper.*;
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

        UploadHelper.init(servletContext);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        ServletHelper.init(req, res);

        try {

            String requestMethod = req.getMethod();
            String requestPath = req.getPathInfo();

            if("/favicon.ico".equals(requestPath)) {
                return;
            }

            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if(null == handler){
                return;
            }
            Class<?> controllerClass = handler.getControllerClass();
            Object controllerBean = BeanHelper.getBean(controllerClass);

            Param param;

            if(UploadHelper.isMultipart(req)) {
                param = UploadHelper.createParam(req);
            } else {
                param = RequestHelper.createParam(req);
            }

            Method actionMethod = handler.getActionMethod();

            Object result;
            if(param.isEmpty()){
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
            } else {
                result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
            }

            if(result instanceof View){
                handleViewResult((View)result, req, res);

            }else if(result instanceof Data){
                handleDataResult((Data)result, req, res);
            }
        } finally {
            ServletHelper.destory();
        }
    }

    private static void handleViewResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = view.getPath();
        if(StringUtil.isNotEmpty(path)){
            if(path.startsWith("/")){
                response.sendRedirect(request.getContextPath() + path);
            }else{
                Map<String,Object> model = view.getModel();
                model.entrySet().stream()
                        .forEach(en -> request.setAttribute(en.getKey(), en.getValue()));
                request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
            }
        }
    }

    private static void handleDataResult(Data data, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Object model = data.getModel();
        if(null != model){
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
