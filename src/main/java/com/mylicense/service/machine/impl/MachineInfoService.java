package com.mylicense.service.machine.impl;

import com.alibaba.fastjson.JSON;
import com.mylicense.common.ResMsg;
import com.mylicense.license.machine.AbstractMachineInfo;
import com.mylicense.license.machine.LinuxMachineInfo;
import com.mylicense.license.machine.WindowsMachineInfo;
import com.mylicense.license.model.LicenseCheckModel;
import com.mylicense.service.machine.IMachineInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
public class MachineInfoService implements IMachineInfoService {

    /**
     * 获取服务器硬件信息
     */
    @Override
    public ResMsg getMachineCode() {

        String osName = System.getProperty("os.name").toLowerCase();

        AbstractMachineInfo abstractMachineInfo = null;
        if (osName.startsWith("windows")) {
            abstractMachineInfo = new WindowsMachineInfo();
        } else {
            abstractMachineInfo = new LinuxMachineInfo();
        }
        LicenseCheckModel machineInfo = abstractMachineInfo.getMachineInfo();

        // 转Base64
        String encoderMsg = Base64.getUrlEncoder().encodeToString(JSON.toJSONBytes(machineInfo));
        return new ResMsg(200, "success", "", encoderMsg);
    }
}
