package com.mylicense.service.machine;

import com.mylicense.common.ResMsg;

public interface IMachineInfoService {

    /**
     * 获取服务器硬件信息
     */
    ResMsg getMachineCode() throws Exception;
}
