package com.mylicense.service.license;

public interface ILicenseVerifyService {

    /**
     * 传入License路径
     * 校验License证书
     */
    boolean verify(String licensePath) throws Exception;
}
