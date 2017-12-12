package core.bean;

import core.util.CastUtil;

import java.util.Map;

/**
 * Created by ChaoChao on 07/12/2017.
 */
public class Param {

    Map<String,Object> paramMap;

    public Param(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }

    public Map<String, Object> getMap() {
        return paramMap;
    }
}
