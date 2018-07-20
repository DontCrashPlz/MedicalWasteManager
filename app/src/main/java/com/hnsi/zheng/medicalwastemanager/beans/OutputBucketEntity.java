package com.hnsi.zheng.medicalwastemanager.beans;

/**
 * 出库页面出库垃圾桶列表信息实体
 * Created by Zheng on 2018/7/16.
 */

public class OutputBucketEntity {
    private String guid;
    private float inputWeigh;
    private float outputWeigh;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public float getInputWeigh() {
        return inputWeigh;
    }

    public void setInputWeigh(float inputWeigh) {
        this.inputWeigh = inputWeigh;
    }

    public float getOutputWeigh() {
        return outputWeigh;
    }

    public void setOutputWeigh(float outputWeigh) {
        this.outputWeigh = outputWeigh;
    }

    @Override
    public String toString() {
        return "OutputBucketEntity{" +
                "guid='" + guid + '\'' +
                ", inputWeigh=" + inputWeigh +
                ", outputWeigh=" + outputWeigh +
                '}';
    }
}
