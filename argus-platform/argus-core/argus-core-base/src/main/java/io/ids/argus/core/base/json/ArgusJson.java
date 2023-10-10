package io.ids.argus.core.base.json;

import com.alibaba.fastjson2.JSONObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

public class ArgusJson implements Serializable {
    final JSONObject json;

    public ArgusJson() {
        json = new JSONObject();
    }

    ArgusJson(JSONObject jsonObject) {
        this.json = jsonObject;
    }

    public String getString(String key) {
        return json.getString(key);
    }

    public Integer getInt(String key) {
        return json.getInteger(key);
    }

    public Boolean getBoolean(String key) {
        return json.getBoolean(key);
    }

    public Float getFloat(String key) {
        return json.getFloat(key);
    }

    public Double getDouble(String key) {
        return json.getDouble(key);
    }

    public ArgusJson getAJsonData(String key) {
        var jsonElement = json.getJSONObject(key);
        if (jsonElement == null) {
            return new ArgusJson();
        }
        return new ArgusJson(jsonElement);
    }

    public ArgusJsonArray getAJsonArray(String key) {
        var jsonElement = json.getJSONArray(key);
        if (jsonElement == null) {
            return null;
        }
        return new ArgusJsonArray(jsonElement);
    }

    public void add(String key, String value) {
        json.put(key, value);
    }

    public void add(ArgusJson value) {
        value.keys().forEach(item -> json.put(item, value.getElement(item)));
    }

    private Object getElement(String key) {
        return json.get(key);
    }

    public Object getObject(String key) {
        return json.get(key);
    }

    public void add(String key, int value) {
        json.put(key, value);
    }

    public void add(String key, long value) {
        json.put(key, value);
    }

    public void add(String key, double value) {
        json.put(key, value);
    }

    public void add(String key, float value) {
        json.put(key, value);
    }

    public void add(String key, boolean value) {
        json.put(key, value);
    }

    public void add(String key, Object value) {
        if (value == null) {
            json.put(key, new JSONObject());
            return;
        }
        json.put(key, value);
    }

    public void add(String key, ArgusJson value) {
        if (value == null) {
            json.put(key, new JSONObject());
            return;
        }
        if (value.json == null) {
            json.put(key, new JSONObject());
        }
        json.put(key, value.json);
    }

    public Set<String> keys() {
        return json.keySet();
    }

    public String toJsonString() {
        return Transformer.toJsonString(json);
    }

    public Object toJsonObject() {
        return json;
    }


    public static boolean isNull(ArgusJson json) {
        return Objects.isNull(json) || Objects.isNull(json.json);
    }
}
