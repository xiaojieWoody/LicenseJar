package com.mylicense.license.machine;

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

    /**
     * CPU序列号
     * @return
     * @throws Exception
     */
    @Override
    protected String getCPUSerial() throws Exception {

        String serialNumber = "";

        //使用dmidecode命令获取CPU序列号
        String[] shell = {"/bin/bash", "-c", "dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        if(process.getInputStream() != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if(reader.readLine() != null) {
                serialNumber = reader.readLine().trim();
            }
            reader.close();
        }

        return serialNumber;
    }

    /**
     * 主板序列号
     * @return
     * @throws Exception
     */
    @Override
    protected String getMainBoardSerial() throws Exception {
        String serialNumber = "";

        //使用dmidecode命令获取主板序列号
        String[] shell = {"/bin/bash", "-c", "dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1"};
        Process process = Runtime.getRuntime().exec(shell);
        process.getOutputStream().close();

        if(null != process.getInputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            if(reader.readLine() != null) {
                serialNumber = reader.readLine().trim();
            }

            reader.close();
        }

        return serialNumber;
    }
}
