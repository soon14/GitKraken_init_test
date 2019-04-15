package cn.rf.hz.web.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.reformer.cloud.utils.OSSConfigure;

public class ThreadLocalDateFormatMap {

    protected static ThreadLocalDateFormatMap INSTANCE = null;

    public static ThreadLocalDateFormatMap getInstance() {
        if (null == INSTANCE)
            return new ThreadLocalDateFormatMap();
        else
            return INSTANCE;
    }

    protected static ThreadLocal<Map<String, DateFormat>> localDateFormatMap = new ThreadLocal<Map<String, DateFormat>>() {

        protected Map<String, DateFormat> initialValue() {
            return new HashMap();
        }

    };

    protected DateFormat createSimpleDateFormat(String pattern) {
        DateFormat result = new SimpleDateFormat(pattern);
        putDateFormat(pattern, result);
        return result;
    }

    public DateFormat putDateFormat(String pattern, DateFormat format) {
        return localDateFormatMap.get().put(pattern, format);
    }

    public DateFormat getDateFormat(String pattern) {
        DateFormat format = localDateFormatMap.get().get(pattern);
        if (format == null) {
            format = createSimpleDateFormat(pattern);
        }
        return format;
    }

}
