package com.std.dev;

import androidx.annotation.Nullable;

import com.tpms.utils.Log;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BaseWrap {
    private static final String TAG = "BaseWrap";

    @Nullable
    protected static <T> T runRelMethod(String str, Object obj, Class[] clsArr, Object... objArr) {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        try {
            Class<?> cls = Class.forName(str);
            Log.d(TAG, "class name is : " + cls.getName());
            if (cls == null) {
                return null;
            }
            new HashMap();
            Method method = cls.getMethod(methodName, clsArr);
            if (method != null) {
                return (T) method.invoke(obj, objArr);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
