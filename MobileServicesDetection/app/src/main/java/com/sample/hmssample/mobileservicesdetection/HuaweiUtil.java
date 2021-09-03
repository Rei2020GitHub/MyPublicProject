package com.sample.hmssample.mobileservicesdetection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HuaweiUtil {

    public static String getManufacturer() {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            return (String) getMethod.invoke(classType, new Object[] {"ro.product.manufacturer"});
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isHuaweiDevice() {
        String manufacturer = getManufacturer();
        if (null == manufacturer) {
            return false;
        }

        return manufacturer.equalsIgnoreCase("HUAWEI");
    }
}
