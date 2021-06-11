package io.github.homeworkcli.models;

import java.io.Serializable;

public class BaseModel implements Serializable {
    private int SUCCESS_CODE = 1;
    private int code;
    private String msg;
    private long responseTime;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSuccessCode() {
        return SUCCESS_CODE;
    }

    public void setSuccessCode(int successCode) {
        this.SUCCESS_CODE = successCode;
    }

    public boolean isSuccess() {
        return this.code == this.SUCCESS_CODE;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}