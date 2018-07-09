package com.hnsi.zheng.medicalwastemanager.beans;

import java.io.Serializable;

/**
 * Created by Zheng on 2018/7/8.
 * 已收集的医废数据实体
 */

public class CollectedWasteEntity implements Serializable {

    /**
     * businessStatus : 0
     * createBy : 2
     * createByName : wyl
     * createTime : 2018-07-07 20:10:36
     * dataStatus : 0
     * departmentId : 0
     * departmentUserId : 3
     * garbageGuid :
     * guid : 0010017001051805091
     * inputDataType : 0
     * inputOverdueStatus : 0
     * orgId : 1
     * orgName : 人民医院
     * remindStatus : 0
     * requireInputTime : null
     * updateBy : 0
     * updateByName :
     * updateTime : null
     * wasteTypeDictId : 1
     * wasteTypeDictName : 感染性医废
     * weight : 0.14
     */

    private int businessStatus;
    private int createBy;
    private String createByName;
    private String createTime;
    private int dataStatus;
    private int departmentId;
    private String departmentName;
    private int departmentUserId;
    private String departmentUserName;
    private String garbageGuid;
    private String guid;
    private int inputDataType;
    private int inputOverdueStatus;
    private int orgId;
    private String orgName;
    private int remindStatus;
    private String requireInputTime;
    private int updateBy;
    private String updateByName;
    private String updateTime;
    private int wasteTypeDictId;
    private String wasteTypeDictName;
    private float weight;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentUserName() {
        return departmentUserName;
    }

    public void setDepartmentUserName(String departmentUserName) {
        this.departmentUserName = departmentUserName;
    }

    public int getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(int businessStatus) {
        this.businessStatus = businessStatus;
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

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public int getDepartmentUserId() {
        return departmentUserId;
    }

    public void setDepartmentUserId(int departmentUserId) {
        this.departmentUserId = departmentUserId;
    }

    public String getGarbageGuid() {
        return garbageGuid;
    }

    public void setGarbageGuid(String garbageGuid) {
        this.garbageGuid = garbageGuid;
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

    public int getInputOverdueStatus() {
        return inputOverdueStatus;
    }

    public void setInputOverdueStatus(int inputOverdueStatus) {
        this.inputOverdueStatus = inputOverdueStatus;
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

    public int getRemindStatus() {
        return remindStatus;
    }

    public void setRemindStatus(int remindStatus) {
        this.remindStatus = remindStatus;
    }

    public String getRequireInputTime() {
        return requireInputTime;
    }

    public void setRequireInputTime(String requireInputTime) {
        this.requireInputTime = requireInputTime;
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

    public int getWasteTypeDictId() {
        return wasteTypeDictId;
    }

    public void setWasteTypeDictId(int wasteTypeDictId) {
        this.wasteTypeDictId = wasteTypeDictId;
    }

    public String getWasteTypeDictName() {
        return wasteTypeDictName;
    }

    public void setWasteTypeDictName(String wasteTypeDictName) {
        this.wasteTypeDictName = wasteTypeDictName;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "CollectedWasteEntity{" +
                "businessStatus=" + businessStatus +
                ", createBy=" + createBy +
                ", createByName='" + createByName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", dataStatus=" + dataStatus +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", departmentUserId=" + departmentUserId +
                ", departmentUserName='" + departmentUserName + '\'' +
                ", garbageGuid='" + garbageGuid + '\'' +
                ", guid='" + guid + '\'' +
                ", inputDataType=" + inputDataType +
                ", inputOverdueStatus=" + inputOverdueStatus +
                ", orgId=" + orgId +
                ", orgName='" + orgName + '\'' +
                ", remindStatus=" + remindStatus +
                ", requireInputTime='" + requireInputTime + '\'' +
                ", updateBy=" + updateBy +
                ", updateByName='" + updateByName + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", wasteTypeDictId=" + wasteTypeDictId +
                ", wasteTypeDictName='" + wasteTypeDictName + '\'' +
                ", weight=" + weight +
                '}';
    }
}
