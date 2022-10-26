package com.sk.signet.onm.common.db;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sk.signet.onm.common.utils.CommonUtil;

@SuppressWarnings("rawtypes")
public class ResultMap extends LinkedHashMap {

	public Object get(Object key) {
        if (super.get(key) == null) {
            return "";
        } else {
            return super.get(key);
        }
    }
	
	@SuppressWarnings("unchecked")
    public Object put(Object key, Object value) {
        return super.put(CommonUtil.convert2CamelCase((String) key), value);
    }
	
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
        if(o == null) return true;

        if(o instanceof String) {
            if(((String)o).length() == 0){
                return true;
        }

        } else if(o instanceof Collection) {
            if(((Collection)o).isEmpty()){
                return true;
            }
        } else if(o.getClass().isArray()) {
            if(Array.getLength(o) == 0){
                return true;
            }
        } else if(o instanceof Map) {
            if(((Map)o).isEmpty()){
                return true;
            }
        }else {
            return false;
        }

        return false;
    }

    /**
    * test for Map,Collection,String,Array isNotEmpty
    * @param c
    * @return
    */ 
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }
}
