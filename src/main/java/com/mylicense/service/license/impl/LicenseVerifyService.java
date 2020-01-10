package com.mylicense.service.license.impl;

import com.mylicense.common.SpringContextUtils;
import com.mylicense.config.LicenseConfig;
import com.mylicense.license.manager.LicenseManagerHolder;
import com.mylicense.license.param.CustomKeyStoreParam;
import com.mylicense.license.param.LicenseVerifyParam;
import com.mylicense.service.license.ILicenseVerifyService;
import de.schlichtherle.license.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

@Slf4j
@Service
public class LicenseVerifyService implements ILicenseVerifyService {

    /**
     * 传入License路径
     * 校验License证书
     */
    @Override
    public boolean verify(String licensePath){
        LicenseVerifyParam param = new LicenseVerifyParam();
        LicenseConfig licenseConfig = SpringContextUtils.getBeanByClass(LicenseConfig.class);
        if(!StringUtils.isEmpty(licensePath)) {
            licenseConfig.setLicensePath(licensePath);
        }
        BeanUtils.copyProperties(licenseConfig, param);
        // 获取公钥地址
        URL publickeyResource = LicenseVerifyService.class.getClassLoader().getResource(licenseConfig.getPublicKeysStorePath());
        if(publickeyResource != null) {
            param.setPublicKeysStorePath(publickeyResource.getPath());
        } else {
            log.error("请先添加授权公钥！");
            throw new RuntimeException("请先添加授权公钥！");
        }
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
            log.info(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}",format.format(licenseContent.getNotBefore()),format.format(licenseContent.getNotAfter())));
            return true;
        }catch (Exception e){
            log.error("证书校验失败！",e);
            return false;
        }
    }

    /**
     * 初始化证书生成参数
     */
    private LicenseParam initLicenseParam(LicenseVerifyParam param){
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerifyService.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerifyService.class
                ,param.getPublicKeysStorePath()
                ,param.getPublicAlias()
                ,param.getStorePass()
                ,null);

        return new DefaultLicenseParam(param.getSubject()
                ,preferences
                ,publicStoreParam
                ,cipherParam);
    }
}
