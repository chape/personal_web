package core.bean;

import core.util.CastUtil;
import core.util.CollectionUtil;
import core.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class Param {

    private List<FormParam> formParamList;
    private List<FileParam> fileParamList;

    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    public Map<String,Object> getFieldMap(){
        if(null == formParamList || formParamList.isEmpty()) {
            return Collections.emptyMap();
        }
        return formParamList.stream()
                         .collect(Collectors.toMap(f -> f.getFieldName(),
                                                    f -> f.getFieldValue(),
                                                    (v1,v2) -> v1 + StringUtil.SEPARATOR + v2));
    }

    public Map<String,List<FileParam>> getFileMap(){
        if(null == fileParamList || fileParamList.isEmpty()) {
            return Collections.emptyMap();
        }
        return fileParamList.stream()
                            .collect(Collectors.toMap(f -> f.getFieldName(),
                                                      f -> Arrays.asList(f),
                                                      (v1,v2) -> {
                                                          v1.addAll(v2);
                                                          return v1;}));
    }

    public List<FileParam> getFileList(String fieldName) {
        return getFileMap().get(fieldName);
    }

    public FileParam getFile(String fieldName) {

        List<FileParam> fileParamList = getFileList(fieldName);
        if(CollectionUtil.isEmpty(fileParamList) && 1 == fileParamList.size()) {
            return fileParamList.get(0);
        }
        return null;
    }

    public boolean isEmpty(){
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isEmpty(fileParamList);
    }

    public String getString(String name){
        return CastUtil.castString(getFieldMap().get(name));
    }

    public long getLong(String name){
        return CastUtil.castLong(getFieldMap().get(name));
    }

    public int getInt(String name){
        return CastUtil.castInt(getFieldMap().get(name));
    }

    public double getDouble(String name){
        return CastUtil.castDouble(getFieldMap().get(name));
    }

    public boolean getBoolean(String name){
        return CastUtil.castBoolean(getFieldMap().get(name));
    }

}
