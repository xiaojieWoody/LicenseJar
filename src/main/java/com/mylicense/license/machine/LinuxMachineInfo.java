package com.mylicense.license.machine;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

public class LinuxMachineInfo extends AbstractMachineInfo {

    /**
     * 获取mac地址
     *
     * @return
     * @throws Exception
     */
    @Override
    protected List<String> getMacAddress() throws Exception {
        List<String> result = null;
        List<InetAddress> localAllInetAddress = getLocalAllInetAddress();
        if (localAllInetAddress != null && localAllInetAddress.size() > 0) {
            result = localAllInetAddress.stream().map(this::getMacByInetAddress).distinct().collect(Collectors.toList());
        }
        return result;
    }

    @Override
    protected String getCPUSerial() throws Exception {
        // CPU序列号
        String serialNumber = "";

        //使用dmidecode命令获取CPU序列号
        String[] shell = {"/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if (StringUtils.isNotBlank(line)) {
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }

    @Override
    protected String getMainBoardSerial() throws Exception {
        //主板序列号
        String serialNumber = "";

        //使用dmidecode命令获取主板序列号
        String[] shell = {"/bin/bash", "-c", "dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = reader.readLine().trim();
        if (StringUtils.isNotBlank(line)) {
            serialNumber = line;
        }

        reader.close();
        return serialNumber;
    }
}
