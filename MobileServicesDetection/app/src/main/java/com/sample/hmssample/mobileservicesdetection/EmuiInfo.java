package com.sample.hmssample.mobileservicesdetection;

import java.lang.reflect.Method;

public class EmuiInfo {
    public static EmuiInfo getEMUI() {
        EmuiInfo.Builder builder = new EmuiInfo.Builder();
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            String buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
            int startIndex = buildVersion.indexOf("_");
            if (startIndex != 1) {
                String emuiVersion = buildVersion.substring(startIndex + 1);
                String[] versionArray = emuiVersion.split("\\.");
                builder.major(versionArray[0]);
                builder.minor(versionArray[1]);
                builder.patch(versionArray[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return builder.build();
    }

    private String major = "";
    private String minor = "";
    private String patch = "";

    private EmuiInfo(Builder builder) {
        this.major = builder.major;
        this.minor = builder.minor;
        this.patch = builder.patch;
    }

    public String getMajor() {
        return this.major;
    }

    public String getMinor() {
        return this.minor;
    }

    public String getPatch() {
        return this.patch;
    }

    public String toString() {
        return this.major + "." + this.minor + "." + this.patch;
    }

    static class Builder {
        private String major = "";
        private String minor = "";
        private String patch = "";

        public Builder() {
        }

        public Builder major(String major) {
            this.major = major;
            return this;
        }

        public Builder minor(String minor) {
            this.minor = minor;
            return this;
        }

        public Builder patch(String patch) {
            this.patch = patch;
            return this;
        }

        public EmuiInfo build() {
            return new EmuiInfo(this);
        }
    }
}
