package com.mylicense.service.license;

import com.mylicense.common.ResMsg;

public interface ILicenseVerifyService {

    /**
     * 传入License路径
     * 校验License证书
     */
    ResMsg verify(String licensePath) throws Exception;
}
