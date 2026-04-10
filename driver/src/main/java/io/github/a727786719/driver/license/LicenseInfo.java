package io.github.a727786719.driver.license;

import java.util.List;

/**
 * 基本授权信息
 */
public class LicenseInfo {

    private String path;
    private String licenseName;
    private String licenseType;
    private List<String> macAddr;
    private String createTime;
    private String validityPeriod;
    private String deadline;

    public String getPath() {
        return path;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public List<String> getMacAddr() {
        return macAddr;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getValidityPeriod() {
        return validityPeriod;
    }

    public String getDeadline() {
        return deadline;
    }
}
