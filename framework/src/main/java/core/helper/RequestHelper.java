package core.helper;

import core.bean.FormParam;
import core.bean.Param;
import core.util.ArrayUtil;
import core.util.CodecUtil;
import core.util.StreamUtil;
import core.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by ChaoChao on 14/12/2017.
 */
public class RequestHelper {

    /**
     * 创建请求参数
     * @param request
     * @return
     * @throws IOException
     */
    public static Param createParam(HttpServletRequest request) throws IOException {

        List<FormParam> formParamList = new ArrayList<>();
        formParamList.addAll(parseParameterNames(request));
        formParamList.addAll(parseInputStream(request));

        return new Param(formParamList);
    }

    private static List<FormParam> parseParameterNames(HttpServletRequest request) {

        List<FormParam> formParamList = new ArrayList<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String fieldName = parameterNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)) {
                String fieldValue;
                if (1 == fieldValues.length) {
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fieldValues.length; i++) {
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    private static List<FormParam> parseInputStream(HttpServletRequest request) throws IOException {

        List<FormParam> formParamList = new ArrayList<>();

        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if(StringUtil.isNotEmpty(body)){
            String[] params = StringUtil.splitString(body, "&");
            if(ArrayUtil.isNotEmpty(params)){
                Arrays.stream(params).forEach(param -> {
                    String[] array = StringUtil.splitString(param, "=");
                    if(ArrayUtil.isNotEmpty(array) && 2 == array.length){
                        String paramName = array[0];
                        String paramValue = array[1];
                        formParamList.add(new FormParam(paramName, paramValue));
                    }
                });
            }
        }
        return formParamList;
    }

}
