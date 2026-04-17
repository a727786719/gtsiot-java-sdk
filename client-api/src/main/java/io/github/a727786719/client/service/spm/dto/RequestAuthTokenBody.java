package io.github.a727786719.client.service.spm.dto;

/**
 * 使用 ak, sk 请求 SPM token 请求参数
 */
public class RequestAuthTokenBody {
    private String ak;
    private String sk;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
}
