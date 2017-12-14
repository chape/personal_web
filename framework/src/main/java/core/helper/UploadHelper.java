package core.helper;

import core.bean.FileParam;
import core.bean.FormParam;
import core.bean.Param;
import core.util.CollectionUtil;
import core.util.FileUtil;
import core.util.StreamUtil;
import core.util.StringUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ChaoChao on 14/12/2017.
 */
public class UploadHelper {

    private static Logger LOGGER = LoggerFactory.getLogger(UploadHelper.class);

    private static ServletFileUpload servletFileUpload;

    public static void init(ServletContext servletContext) {
        File repository = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
        servletFileUpload = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository));
        int uploadLimit = ConfigHelper.getAppUploadLimit();
        if(uploadLimit > 0) {
            servletFileUpload.setFileSizeMax(uploadLimit * 1024 * 1024);
        }
    }

    public static boolean isMultipart(HttpServletRequest request) {

        return ServletFileUpload.isMultipartContent(request);
    }

    public static Param createParam(HttpServletRequest request) {
        List<FormParam> formParamList = new ArrayList<>();
        List<FileParam> fileParamList = new ArrayList<>();

        try {
            Map<String, List<FileItem>> fileItemListMap =  servletFileUpload.parseParameterMap(request);
            if(CollectionUtil.isNotEmpty(fileItemListMap)) {
                fileItemListMap.entrySet().stream().forEach(entry -> {
                    String fieldName = entry.getKey();
                    List<FileItem> fileItemList = entry.getValue();
                    if(CollectionUtil.isNotEmpty(fileItemList)) {
                        fileItemList.stream().forEach(fileItem -> {
                            if(fileItem.isFormField()) {
                                try{
                                    String fieldValue = fileItem.getString("UTF-8");
                                    formParamList.add(new FormParam(fieldName, fieldValue));
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                try {
                                    String fileName = new String(fileItem.getName().getBytes(), "UTF-8");
                                    String fileRealName = FileUtil.getRealFileName(fileName);

                                    if(StringUtil.isNotEmpty(fileRealName)) {
                                        String contentType = fileItem.getContentType();
                                        long fileSize = fileItem.getSize();
                                        InputStream fileInputStream = fileItem.getInputStream();
                                        String fileFieldName = fileItem.getFieldName();
                                        fileParamList.add(new FileParam(fileFieldName, fileRealName, fileSize, contentType, fileInputStream));
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                    }
                });
            }

        } catch (Exception e) {
            LOGGER.error("create param failure");
            throw new RuntimeException(e);
        }
        return new Param(formParamList, fileParamList);
    }

    /**
     * 上传单个文件
     * @param basePath
     * @param fileParam
     */
    public static void uploadFile(String basePath, FileParam fileParam) {

        if(null == fileParam) {
            return;
        }
        String filePath = basePath + fileParam.getFileName();
        FileUtil.createFile(filePath);
        InputStream inputStream = new BufferedInputStream(fileParam.getInputStream());
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
            StreamUtil.copyStream(inputStream, outputStream);
        } catch (Exception e) {
            LOGGER.error("upload file error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量上传文件
     * @param basePath
     * @param fileParamList
     */
    public static void uploadFile(String basePath, List<FileParam> fileParamList) {
        if(CollectionUtil.isEmpty(fileParamList)) {
            return;
        }
        fileParamList.stream().forEach(fp -> uploadFile(basePath, fp));
    }
}
