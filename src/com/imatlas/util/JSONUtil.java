/**
 * @author azrael
 * @date 2013-2-6
 */
package com.imatlas.util;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * @author azrael
 *
 */
public class JSONUtil {
    /**
     * 把 bundle 转换成 json 对象, 只取用 String, Boolean, Integer, Long, Double
     * @param bundle
     * @return
     * @throws JSONException 
     */
    public static JSONObject bundleToJSON(Bundle bundle) throws JSONException{
        JSONObject json = new JSONObject();
        if(bundle == null || bundle.isEmpty()){
            return json;
        }
        Set<String> keySet = bundle.keySet();
        for (String key : keySet) {
            Object object = bundle.get(key);
            if (object instanceof String || object instanceof Boolean || object instanceof Integer
                    || object instanceof Long || object instanceof Double){
                json.put(key, object);
            }
        }
        return json;
    }
    
    /**
    * 把 bundle 转换成 json 字符串, 只取用 String, Boolean, Integer, Long, Double
    * @param bundle
    * @return
    * @throws JSONException 
    */
   public static String bundleToJSONString(Bundle bundle) throws JSONException{
       JSONObject json = bundleToJSON(bundle);
       return json.toString();
   }
    
}
