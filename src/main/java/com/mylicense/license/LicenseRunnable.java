package com.mylicense.license;

import com.mylicense.common.ResMsg;
import com.mylicense.common.SpringContextUtils;
import com.mylicense.config.LicenseInfoConfig;
import com.mylicense.service.license.ILicenseVerifyService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LicenseRunnable implements Runnable{

    @Override
    public void run() {
        ResMsg verify = null;
        ILicenseVerifyService licenseVerifyService = SpringContextUtils.getBeanByClass(ILicenseVerifyService.class);
        LicenseInfoConfig licenseConfig = SpringContextUtils.getBeanByClass(LicenseInfoConfig.class);
        // 事先约定License文件存储在服务器的目录
        try {
            log.info("+++++++++++License Verify Begin++++++++++++");
            verify = licenseVerifyService.verify(licenseConfig.getLicensePath());
            log.warn("----" + licenseConfig.getRemainTime() + "------");
        } catch (Exception e) {
            log.error("License文件验证异常.....", e);
        }
    }
}
