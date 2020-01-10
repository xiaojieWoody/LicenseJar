package com.mylicense.license.manager;

import com.alibaba.fastjson.JSON;
import com.mylicense.common.SpringContextUtils;
import com.mylicense.config.LicenseConfig;
import com.mylicense.license.machine.AbstractMachineInfo;
import com.mylicense.license.machine.LinuxMachineInfo;
import com.mylicense.license.machine.WindowsMachineInfo;
import com.mylicense.license.model.LicenseCheckModel;
import de.schlichtherle.license.*;
import de.schlichtherle.xml.GenericCertificate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * 自定义LicenseManager
 * 增加额外的服务器硬件信息校验
 */
@Slf4j
public class CustomLicenseManager extends LicenseManager {

    public CustomLicenseManager(){}

    public CustomLicenseManager(LicenseParam param) {
        super(param);
    }

    /**
     * 重写install方法
     * 其中validate方法调用本类中的validate方法，校验Mac地址等其他信息
     * @param key
     * @param notary
     * @return
     * @throws Exception
     */
    @Override
    protected synchronized LicenseContent install(final byte[] key, final LicenseNotary notary) throws Exception {
        final GenericCertificate certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) certificate.getContent();
        this.validate(content);
        setLicenseKey(key);
        setCertificate(certificate);
        return content;
    }

    /**
     * 重写verify方法
     * 调用本类中的validate方法，校验Mac地址等其他信息
     * @param notary
     * @return
     * @throws Exception
     */
    @Override
    protected synchronized LicenseContent verify(final LicenseNotary notary) throws Exception {
        GenericCertificate certificate = getCertificate();
        // Load license key from preferences,
        // 从安装中获取License文件
//        final byte[] key = getLicenseKey();

        // 从指定路径获取License证书
        LicenseConfig licenseConfig = SpringContextUtils.getBeanByClass(LicenseConfig.class);
        File keyLicense = new File(licenseConfig.getLicensePath());
        if(!keyLicense.exists()) {
            throw new NoLicenseInstalledException(getLicenseParam().getSubject() + "-证书不存在，请先上传证书！");
        }
        byte[] key = FileUtils.readFileToByteArray(keyLicense);

        if (null == key){
            throw new NoLicenseInstalledException(getLicenseParam().getSubject() + "-License获取失败！");
        }
        certificate = getPrivacyGuard().key2cert(key);
        notary.verify(certificate);
        final LicenseContent content = (LicenseContent) certificate.getContent();
        this.validate(content);
        setCertificate(certificate);
        return content;
    }

    /**
     * 重写validate方法
     * 增加Mac地址等其他信息校验
     * @param content
     * @throws LicenseContentException
     */
    @Override
    protected synchronized void validate(final LicenseContent content) throws LicenseContentException {
        //调用父类的validate方法
        super.validate(content);
        //校验自定义的License参数
        String LicenseCheckModelStr = (String)content.getExtra();
        LicenseCheckModel expectedCheckModel = JSON.parseObject(LicenseCheckModelStr, LicenseCheckModel.class);

        //客户服务器真实的参数信息（解密参数中获取）
        LicenseCheckModel licenseCheckModel = getServerInfos();

        if(expectedCheckModel != null && licenseCheckModel != null){
            //校验IP地址
//            if(!checkIpAddress(expectedCheckModel.getIpAddress(),serverCheckModel.getIpAddress())){
//                throw new LicenseContentException("当前服务器的IP没在授权范围内");
//            }

            //校验Mac地址
            if(!checkIpAddress(expectedCheckModel.getMacAddress(),licenseCheckModel.getMacAddress())){
                throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
            }

            //校验主板序列号
            if(!checkSerial(expectedCheckModel.getMainBoardSerial(),licenseCheckModel.getMainBoardSerial())){
                throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
            }

            //校验CPU序列号
            if(!checkSerial(expectedCheckModel.getCpuSerial(),licenseCheckModel.getCpuSerial())){
                throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
            }
        }else{
            throw new LicenseContentException("不能获取服务器硬件信息");
        }
    }

    /**
     * 客户服务器真实的参数信息（解密参数中获取）
     * @return
     */
    private LicenseCheckModel getServerInfos() {
        //操作系统类型
        String osName = System.getProperty("os.name").toLowerCase();
        AbstractMachineInfo abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsMachineInfo();
        }else{
            abstractServerInfos = new LinuxMachineInfo();
        }

        return abstractServerInfos.getMachineInfo();
    }

    /**
     * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内
     * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
     * @param expectedList
     * @param serverList
     * @return
     */
    private boolean checkIpAddress(List<String> expectedList, List<String> serverList){
        if(expectedList != null && expectedList.size() > 0){
            if(serverList != null && serverList.size() > 0){
                for(String expected : expectedList){
                    if(serverList.contains(expected.trim())){
                        return true;
                    }
                }
            }
            return false;
        }else {
            return true;
        }
    }

    /**
     * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
     * @param expectedSerial
     * @param serverSerial
     * @return
     */
    private boolean checkSerial(String expectedSerial,String serverSerial){
        if(StringUtils.isNotBlank(expectedSerial)){
            if(StringUtils.isNotBlank(serverSerial)){
                if(expectedSerial.equals(serverSerial)){
                    return true;
                }
            }
            return false;
        }else{
            return true;
        }
    }
}
