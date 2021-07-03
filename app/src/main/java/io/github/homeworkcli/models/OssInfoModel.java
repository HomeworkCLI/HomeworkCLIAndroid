package io.github.homeworkcli.models;

public class OssInfoModel {
    private String accessKeyId;
    private String accessKeySecret;
    private String expiration;
    private String securityToken;

    public String getSecurityToken() {
        return this.securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getExpiration() {
        return this.expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getAccessKeyId() {
        return this.accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return this.accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String toString() {
        return "OSSInfo{securityToken='" + this.securityToken + "', expiration='" + this.expiration + "', accessKeyId='" + this.accessKeyId + "', accessKeySecret='" + this.accessKeySecret + "'}";
    }
}
