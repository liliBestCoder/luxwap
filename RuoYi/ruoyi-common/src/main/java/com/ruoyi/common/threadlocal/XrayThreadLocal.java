package com.ruoyi.common.threadlocal;

public class XrayThreadLocal {
    private static ThreadLocal<Long> uid = new ThreadLocal<>();

    public static void setUid(Long uid) {
        XrayThreadLocal.uid.set(uid);
    }

    public static Long getUid() {
        return XrayThreadLocal.uid.get();
    }

    public static void clear(){
        uid.remove();
    }
}
