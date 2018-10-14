package com.hnsi.zheng.medicalwastemanager.exception;

/**
 * Author: Create by Zheng on 2018/10/9 22:20
 * E-mail: zhengCH12138@163.com
 *
 * 无效IC卡信息异常
 */
public class InvalidCardDataException extends Exception {

    public InvalidCardDataException(String message) {
        super(message);
    }

}
