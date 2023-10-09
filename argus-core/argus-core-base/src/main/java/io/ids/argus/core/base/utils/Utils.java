package io.ids.argus.core.base.utils;

public class Utils {
    public static String pack(String url) {
        url = url.toLowerCase();
        if (!url.startsWith(Constants.URL_SEPARATOR)) {
            url = Constants.URL_SEPARATOR + url;
        }
        if (!url.endsWith(Constants.URL_SEPARATOR)) {
            url = url + Constants.URL_SEPARATOR;
        }
        return url;
    }
}
