package com.hnsi.zheng.medicalwastemanager.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zheng on 2018/7/8.
 * 已入库的医废桶实体
 */

public class InputedBucketEntity implements Serializable {

    /**
     * businessStatus : 1
     * checkStatus : 0
     * createBy : 2
     * createByName : 张三
     * createTime : 2018-07-09 11:14:37
     * dataStatus : 0
     * fileId :
     * garbageBinTypeId : 0
     * guid : 0011807081927210020
     * id:1,
     * inputDataType : 0
     * orgId : 1
     * orgName : 第一人民医院
     * outputBy : 0
     * outputByName :
     * outputDataType : 0
     * outputOverdueStatus : 0
     * outputTime : null
     * outputTotalWeight : 0
     * remindStatus : 0
     * requireOutputTime : null
     * updateBy : 0
     * updateByName :
     * updateTime : null
     * wasteAmount : 3
     * wasteList : []
     * wasteWeight : 0.62
     */

    private int businessStatus;
    private int checkStatus;
    private int createBy;
    private String createByName;
    private String createTime;
    private int dataStatus;
    private String fileId;
    private int garbageBinTypeId;
    private String guid;
    private int id;
    private int inputDataType;
    private int orgId;
    private String orgName;
    private int outputBy;
    private String outputByName;
    private int outputDataType;
    private int outputOverdueStatus;
    private String outputTime;
    private int outputTotalWeight;
    private int remindStatus;
    private String requireOutputTime;
    private int updateBy;
    private String updateByName;
    private String updateTime;
    private int wasteAmount;
    private ArrayList<CollectedWasteEntity> wasteList;
    private float wasteWeight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<CollectedWasteEntity> getWasteList() {
        return wasteList;
    }

    public void setWasteList(ArrayList<CollectedWasteEntity> wasteList) {
        this.wasteList = wasteList;
    }

    public int getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(int businessStatus) {
        this.businessStatus = businessStatus;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getGarbageBinTypeId() {
        return garbageBinTypeId;
    }

    public void setGarbageBinTypeId(int garbageBinTypeId) {
        this.garbageBinTypeId = garbageBinTypeId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getInputDataType() {
        return inputDataType;
    }

    public void setInputDataType(int inputDataType) {
        this.inputDataType = inputDataType;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getOutputBy() {
        return outputBy;
    }

    public void setOutputBy(int outputBy) {
        this.outputBy = outputBy;
    }

    public String getOutputByName() {
        return outputByName;
    }

    public void setOutputByName(String outputByName) {
        this.outputByName = outputByName;
    }

    public int getOutputDataType() {
        return outputDataType;
    }

    public void setOutputDataType(int outputDataType) {
        this.outputDataType = outputDataType;
    }

    public int getOutputOverdueStatus() {
        return outputOverdueStatus;
    }

    public void setOutputOverdueStatus(int outputOverdueStatus) {
        this.outputOverdueStatus = outputOverdueStatus;
    }

    public String getOutputTime() {
        return outputTime;
    }

    public void setOutputTime(String outputTime) {
        this.outputTime = outputTime;
    }

    public int getOutputTotalWeight() {
        return outputTotalWeight;
    }

    public void setOutputTotalWeight(int outputTotalWeight) {
        this.outputTotalWeight = outputTotalWeight;
    }

    public int getRemindStatus() {
        return remindStatus;
    }

    public void setRemindStatus(int remindStatus) {
        this.remindStatus = remindStatus;
    }

    public String getRequireOutputTime() {
        return requireOutputTime;
    }

    public void setRequireOutputTime(String requireOutputTime) {
        this.requireOutputTime = requireOutputTime;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateByName() {
        return updateByName;
    }

    public void setUpdateByName(String updateByName) {
        this.updateByName = updateByName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getWasteAmount() {
        return wasteAmount;
    }

    public void setWasteAmount(int wasteAmount) {
        this.wasteAmount = wasteAmount;
    }

    public float getWasteWeight() {
        return wasteWeight;
    }

    public void setWasteWeight(float wasteWeight) {
        this.wasteWeight = wasteWeight;
    }

    @Override
    public String toString() {
        return "InputedBucketEntity{" +
                "businessStatus=" + businessStatus +
                ", checkStatus=" + checkStatus +
                ", createBy=" + createBy +
                ", createByName='" + createByName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", dataStatus=" + dataStatus +
                ", fileId='" + fileId + '\'' +
                ", garbageBinTypeId=" + garbageBinTypeId +
                ", guid='" + guid + '\'' +
                ", id=" + id +
                ", inputDataType=" + inputDataType +
                ", orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                ", outputBy=" + outputBy +
                ", outputByName='" + outputByName + '\'' +
                ", outputDataType=" + outputDataType +
                ", outputOverdueStatus=" + outputOverdueStatus +
                ", outputTime='" + outputTime + '\'' +
                ", outputTotalWeight=" + outputTotalWeight +
                ", remindStatus=" + remindStatus +
                ", requireOutputTime='" + requireOutputTime + '\'' +
                ", updateBy=" + updateBy +
                ", updateByName='" + updateByName + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", wasteAmount=" + wasteAmount +
                ", wasteList=" + wasteList +
                ", wasteWeight=" + wasteWeight +
                '}';
    }
}
