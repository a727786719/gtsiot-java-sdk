package io.github.a727786719.driver.license;


/**
 * 授权验证结果
 */
public class LicenseVerifyResult {

    private boolean ok;
    private int tagCount;
    private LicenseInfo license;
    private String error;

    public boolean isOk() {
        return ok;
    }

    public int getTagCount() {
        return tagCount;
    }

    public LicenseInfo getLicense() {
        return license;
    }

    public String getError() {
        return error;
    }
}
