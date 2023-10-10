package io.ids.argus.core.base.json;

import com.alibaba.fastjson2.JSONArray;

public class ArgusJsonArray {
    final JSONArray array;
    public ArgusJsonArray() {
        array = new JSONArray();
    }
    ArgusJsonArray(JSONArray jsonArray) {
        this.array = jsonArray;
    }

    public String getString(int index) {
        return array.getString(index);
    }

    public Float getFloat(int index) {
        return array.getFloat(index);
    }

    public Double getDouble(int index) {
        return array.getDouble(index);
    }

    public Integer getInteger(int index) {
        return array.getInteger(index);
    }

    public Boolean getBoolean(int index) {
        return array.getBoolean(index);
    }

    public ArgusJson get(int index) {
        var element = array.getJSONObject(index);
        if (element == null) {
            return null;
        }
        return new ArgusJson(element);
    }

    public void add(int index, boolean bool) {
        array.add(index, bool);
    }

    public void add(int index, Character character) {
        array.add(index, character);
    }

    public void add(int index, float number) {
        array.add(index, number);
    }

    public void add(int index, int number) {
        array.add(index, number);
    }

    public void add(int index, double number) {
        array.add(index, number);
    }

    public void add(int index, long number) {
        array.add(index, number);
    }

    public void add(int index, String string) {
        array.add(index, string);
    }

    public void add(boolean bool) {
        array.add(bool);
    }

    public void add(Character character) {
        array.add(character);
    }

    public void add(float number) {
        array.add(number);
    }

    public void add(int number) {
        array.add(number);
    }

    public void add(double number) {
        array.add(number);
    }

    public void add(long number) {
        array.add(number);
    }

    public void add(String string) {
        array.add(string);
    }

    public void add(ArgusJson data) {
        array.add(data.json);
    }

    public String toJSONString() {
        return array.toJSONString();
    }

    public int size() {
        return array.size();
    }
}
