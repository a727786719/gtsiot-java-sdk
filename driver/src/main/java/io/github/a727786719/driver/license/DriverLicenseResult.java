package io.github.a727786719.driver.license;

/**
 * 驱动 License JSON 验证结果
 */
public class DriverLicenseResult {

    /**
     * 返回码，0 表示成功
     */
    private int code;

    /**
     * Payload JSON 字符串
     */
    private String payloadJson;

    /**
     * Result JSON 字符串
     */
    private String resultJson;

    /**
     * 错误信息
     */
    private String errorMsg;

    public DriverLicenseResult() {
    }

    /**
     * 判断验证是否成功
     *
     * @return 如果 code 为 0 则返回 true
     */
    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "DriverLicenseResult{" +
                "code=" + code +
                ", payloadJson='" + payloadJson + '\'' +
                ", resultJson='" + resultJson + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
