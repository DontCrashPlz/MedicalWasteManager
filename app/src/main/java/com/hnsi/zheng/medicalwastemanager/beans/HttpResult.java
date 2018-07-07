package com.hnsi.zheng.medicalwastemanager.beans;

/**
 * Created by Zheng on 2018/7/6.
 */

public class HttpResult<T> {
    private String msg;
    private T obj;
    private String status;
    private boolean success;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "msg='" + msg + '\'' +
                ", obj=" + obj +
                ", status='" + status + '\'' +
                ", success=" + success +
                '}';
    }
}
