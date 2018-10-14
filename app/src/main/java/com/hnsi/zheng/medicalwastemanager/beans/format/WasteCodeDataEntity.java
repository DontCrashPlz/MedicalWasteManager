package com.hnsi.zheng.medicalwastemanager.beans.format;

import com.hnsi.zheng.medicalwastemanager.exception.InvalidWasteCodeDataException;

/**
 * Author: Create by Zheng on 2018/10/9 22:32
 * E-mail: zhengCH12138@163.com
 * <p>
 * 医废袋二维码数据实体
 */
public class WasteCodeDataEntity {

    private String orgName;//0 医院名称
    private String createBy;//1 收集人员ID
    private String createName;//2 收集人员姓名
    private String departmentName;//3 科室名称
    private String departmentUserId;//4 科室人员ID
    private String departmentUserName;//5 科室人员姓名
    private String wasteTypeDictId;//6 医废类型
    private String weight;//7 医废重量
    private String createTime;//8 收集时间
    private String guid;//9 医废编号

    public WasteCodeDataEntity(){}

    public WasteCodeDataEntity(String formatStr) throws InvalidWasteCodeDataException {
        if (formatStr == null){
            throw new InvalidWasteCodeDataException("医废二维码数据字符串无效");
        }
        String[] strArrays = formatStr.split("_");
        if (strArrays.length != 10){
            throw new InvalidWasteCodeDataException("医废二维码数据数组无效");
        }
        this.orgName = strArrays[0];
        this.createBy = strArrays[1];
        this.createName = strArrays[2];
        this.departmentName = strArrays[3];
        this.departmentUserId = strArrays[4];
        this.departmentUserName = strArrays[5];
        this.wasteTypeDictId = strArrays[6];
        this.weight = strArrays[7];
        this.createTime = strArrays[8];
        this.guid = strArrays[9];
    }

    /**
     * 获取医废二维码数据对象的格式化字符串
     * @return
     */
    public String toFormatStr() throws InvalidWasteCodeDataException {
        if (!isValidStr(orgName)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:医院名称无效");
        }
        if (!isValidStr(createBy)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:收集人员ID无效");
        }
        if (!isValidStr(createName)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:收集人员姓名无效");
        }
        if (!isValidStr(departmentName)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:科室名称无效");
        }
        if (!isValidStr(departmentUserId)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:科室人员ID无效");
        }
        if (!isValidStr(departmentUserName)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:科室人员姓名无效");
        }
        if (!isValidStr(wasteTypeDictId)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:医废类型无效");
        }
        if (!isValidStr(weight)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:医废重量无效");
        }
        if (!isValidStr(createTime)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:收集时间无效");
        }
        if (!isValidStr(guid)){
            throw new InvalidWasteCodeDataException("医废二维码数据无效:医废编号无效");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(orgName).append("_")
                .append(createBy).append("_")
                .append(createName).append("_")
                .append(departmentName).append("_")
                .append(departmentUserId).append("_")
                .append(departmentUserName).append("_")
                .append(wasteTypeDictId).append("_")
                .append(weight).append("_")
                .append(createTime).append("_")
                .append(guid);
        return builder.toString();
    }

    private boolean isValidStr(String str){
        str = str.trim();
        if (str == null) return false;
        if ("".equals(str)) return false;
        if ("null".equals(str)) return false;
        return true;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentUserId() {
        return departmentUserId;
    }

    public void setDepartmentUserId(String departmentUserId) {
        this.departmentUserId = departmentUserId;
    }

    public String getDepartmentUserName() {
        return departmentUserName;
    }

    public void setDepartmentUserName(String departmentUserName) {
        this.departmentUserName = departmentUserName;
    }

    public String getWasteTypeDictId() {
        return wasteTypeDictId;
    }

    public void setWasteTypeDictId(String wasteTypeDictId) {
        this.wasteTypeDictId = wasteTypeDictId;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "WasteCodeDataEntity{" +
                "orgName='" + orgName + '\'' +
                ", createBy='" + createBy + '\'' +
                ", createName='" + createName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentUserId='" + departmentUserId + '\'' +
                ", departmentUserName='" + departmentUserName + '\'' +
                ", wasteTypeDictId='" + wasteTypeDictId + '\'' +
                ", weight='" + weight + '\'' +
                ", createTime='" + createTime + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }
}
