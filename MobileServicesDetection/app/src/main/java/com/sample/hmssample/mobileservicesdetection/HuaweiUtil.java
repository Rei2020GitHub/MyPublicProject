package com.sample.hmssample.mobileservicesdetection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HuaweiUtil {

    private static final String HARMONY = "harmony";

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

    public static boolean isHarmonyOS() {
        try {
            Class classType = Class.forName("com.huawei.system.BuildEx");
            Method method = classType.getMethod("getOsBrand");
            ClassLoader classLoader = classType.getClassLoader();
            if (classLoader != null && classLoader.getParent() == null) {
                return HARMONY.equals(method.invoke(classType));
            }
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        }
        return false;
    }
}
