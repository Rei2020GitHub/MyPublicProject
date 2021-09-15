package com.sample.hmssample.mobileservicesdetection;

import android.os.Build;

public class BuildInfo {
    public static boolean isOrientationCorrect() {
        if (HuaweiUtil.isHarmonyOS()) {
            return true;
        }

        BuildInfo buildInfo = getBuild();
        if (null == buildInfo) {
            return true;
        }

        long major = Long.parseLong(buildInfo.getMajorVersion());
        long minor = Long.parseLong(buildInfo.getMinorVersion());
        long build = Long.parseLong(buildInfo.getBuildVersion());
        long revision = Long.parseLong(buildInfo.getRevisionVersion());

        if (major > 11) {
            return true;
        } else if (major == 11) {
            if (minor > 0) {
                return true;
            } else if (minor == 0) {
                if (build > 0) {
                    return true;
                } else if (build == 0) {
                    if (revision >= 210) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static BuildInfo getBuild() {
        BuildInfo.Builder builder = new BuildInfo.Builder();
        try {
            int startIndex = Build.DISPLAY.indexOf(" ");
            int endIndex = Build.DISPLAY.indexOf("(");
            if (startIndex != 1 && endIndex != 1) {
                String buildData = Build.DISPLAY.substring(startIndex + 1, endIndex);
                String[] versionArray = buildData.split("\\.");
                builder.majorVersion(versionArray[0]);
                builder.minorVersion(versionArray[1]);
                builder.buildVersion(versionArray[2]);
                builder.revisionVersion(versionArray[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return builder.build();
    }

    private String majorVersion = "";
    private String minorVersion = "";
    private String buildVersion = "";
    private String revisionVersion = "";

    private BuildInfo(BuildInfo.Builder builder) {
        this.majorVersion = builder.majorVersion;
        this.minorVersion = builder.minorVersion;
        this.buildVersion = builder.buildVersion;
        this.revisionVersion = builder.revisionVersion;
    }

    public String getMajorVersion() {
        return this.majorVersion;
    }

    public String getMinorVersion() {
        return this.minorVersion;
    }

    public String getBuildVersion() {
        return this.buildVersion;
    }

    public String getRevisionVersion() {
        return this.revisionVersion;
    }

    public String toString() {
        return this.majorVersion + "." + this.minorVersion + "." + this.buildVersion + "." + this.revisionVersion;
    }

    static class Builder {
        private String majorVersion = "";
        private String minorVersion = "";
        private String buildVersion = "";
        private String revisionVersion = "";

        public Builder() {
        }

        public BuildInfo.Builder majorVersion(String majorVersion) {
            this.majorVersion = majorVersion;
            return this;
        }

        public BuildInfo.Builder minorVersion(String minorVersion) {
            this.minorVersion = minorVersion;
            return this;
        }

        public BuildInfo.Builder buildVersion(String buildVersion) {
            this.buildVersion = buildVersion;
            return this;
        }

        public BuildInfo.Builder revisionVersion(String revisionVersion) {
            this.revisionVersion = revisionVersion;
            return this;
        }

        public BuildInfo build() {
            return new BuildInfo(this);
        }
    }
}
