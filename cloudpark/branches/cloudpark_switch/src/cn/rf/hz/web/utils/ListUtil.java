package cn.rf.hz.web.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ListUtil {

    // 取列表的前面指定记录
    public static List<Object> getTopList(List<Object> list, int top) {
        if (list == null || list.size() <= top) {
            return list;
        } else {
            return getSubList(list, 0, top);
        }
    }

    public static List<Object> getSubList(List<Object> list, int start, int end) {

        List<Object> resultList = new ArrayList<Object>();

        int i = 0;
        for (Object obj : list) {
            if (start <= i && i < end) {
                resultList.add(obj);
            }
            i++;
        }

        return resultList;
    }

    /**
     * 比较两个list是否相等
     * 
     * @param list1
     * @param list2
     * @return
     */
    public static boolean isEquals(List<Long> list1, List<Long> list2) {
        if (list1 == null && list2 == null) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        for (Long object : list1) {
            if (!list2.contains(object)) {
                return false;
            }
        }
        return true;
    }

    public static <T, K> List<T> mapValueToList(Map<K, T> map) {
        List<T> list = new ArrayList<T>();
        for (T value : map.values()) {
            list.add(value);
        }
        return list;
    }

    /**
     * 根据当前页和页大小参数从list获取当前页所对应的页
     * 
     * @param list 原始的列表
     * @param pageSize 页大小
     * @param currentPage 当前页,下标从0开始
     * @return 当前页所包含的id的List
     */
    public static <T> List<T> getNextPage(List<T> list, int pageSize, int currentPage) {
        if (list != null && list.size() > 0) {
            int fromIndex = currentPage * pageSize;
            int toIndex = fromIndex + pageSize;
            if (fromIndex < list.size()) {// 保证数组index不越界
                if (toIndex > list.size()) {// 不足一页大小的最后一页
                    toIndex = list.size();
                }
                // 实现序列化接口的arraylist
                return new ArrayList<T>(list.subList(fromIndex, toIndex));
            }
        }
        return new ArrayList<T>();
    }




}
