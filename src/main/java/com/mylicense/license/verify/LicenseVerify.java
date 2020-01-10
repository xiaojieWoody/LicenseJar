package com.mylicense.license.verify;

import com.mylicense.common.SpringContextUtils;
import com.mylicense.config.LicenseConfig;
import com.mylicense.license.manager.LicenseManagerHolder;
import com.mylicense.license.param.CustomKeyStoreParam;
import com.mylicense.license.param.LicenseVerifyParam;
import de.schlichtherle.license.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

/**
 * License校验
 */
@Slf4j
@Component
public class LicenseVerify {

    /**
     * 安装License证书
     */
//    public synchronized LicenseContent install(LicenseVerifyParam param) {
//        LicenseContent result = null;
//        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        //安装证书
//        try{
//            LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
//            licenseManager.uninstall();
//
//            result = licenseManager.install(new File(param.getLicensePath()));
//            log.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}",format.format(result.getNotBefore()),format.format(result.getNotAfter())));
//        }catch (Exception e){
//            log.error("证书安装失败！",e);
//        }
//        return result;
//    }

    /**
     * 校验License证书
     */
    public boolean verify(){
        // 单例模式，应用启动安装证书时已经初始化过
        LicenseVerifyParam param = new LicenseVerifyParam();
        LicenseConfig licenseConfig = SpringContextUtils.getBeanByClass(LicenseConfig.class);
        BeanUtils.copyProperties(licenseConfig, param);
        URL publickeyResource = LicenseVerify.class.getClassLoader().getResource(licenseConfig.getPublicKeysStorePath());
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
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class
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
