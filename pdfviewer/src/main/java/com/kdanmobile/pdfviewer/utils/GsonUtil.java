package com.kdanmobile.pdfviewer.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @param:
 * @author: luozhipeng
 * @Decreption: Gson对象封装类
 * @create at: 14/6/16 11:33
 **/
public class GsonUtil {
    private static Gson gson = null;

    static {
        if (gson == null) {
            /*gson = new Gson();*/
            gson = new GsonBuilder().setExclusionStrategies(new ParseExclusion()).create();
        }
    }

    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }


    public static String objectToJson(Object ts, Type type) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts, type);
        }
        return jsonStr;
    }


    public static String objectToJsonDateSerializer(Object ts, final String dateformat) {
        String jsonStr = null;
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class,
                        new JsonSerializer<Date>() {
                            @Override
                            public JsonElement serialize(Date src,
                                                         Type typeOfSrc,
                                                         JsonSerializationContext context) {
                                SimpleDateFormat format = new SimpleDateFormat(dateformat);
                                return new JsonPrimitive(format.format(src));
                            }
                        }).setDateFormat(dateformat).create();
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }


    public static List<?> jsonToList(String jsonStr) {
        List<?> objList = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<List<?>>() {
            }.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }


    public static List<?> jsonToList(String jsonStr, Type type) {
        List<?> objList = null;
        if (gson != null) {
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }


    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> cl) {
        try {
            if (gson != null) {
                return gson.fromJson(jsonStr, cl);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (IllegalStateException ee) {
            ee.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl, final String pattern) {
        Object obj = null;
        gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        try {
                            return format.parse(dateStr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).setDateFormat(pattern).create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return (T) obj;
    }

    public synchronized static String getObjectJson(LinkedTreeMap map, String key) {
        Object object = map.get(key);
        return gson.toJsonTree(object).getAsJsonObject().toString();
    }

    public synchronized static <T> T getObjectJson(LinkedTreeMap map, String key, Class<T> cls) {
        Object object = map.get(key);
        String json = gson.toJsonTree(object).getAsJsonObject().toString();
        return jsonToBean(json, cls);
    }

    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }

    private static class ParseExclusion implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getDeclaredClass() == Lock.class);
        }

    }
}
