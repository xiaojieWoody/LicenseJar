package com.mylicense.license.machine;

import com.mylicense.license.model.LicenseCheckModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 获取客户服务器的基本信息，如：Mac地址、CPU序列号、主板序列号等
 */
public abstract class AbstractMachineInfo {

    private static final Logger logger = LogManager.getLogger(AbstractMachineInfo.class);

    /**
     * 服务器信息
     * @return
     */
    public LicenseCheckModel getMachineInfo() {
        LicenseCheckModel result = new LicenseCheckModel();

        try {
            result.setMacAddress(this.getMacAddress());
            result.setCpuSerial(this.getCPUSerial());
            result.setMainBoardSerial(this.getMainBoardSerial());
        }catch (Exception e){
            logger.error("获取服务器硬件信息失败",e);
        }

        return result;
    }

    /**
     * 获取mac地址
     * @return
     * @throws Exception
     */
    protected abstract List<String> getMacAddress() throws Exception;

    /**
     * 获取cpu序列号
     * @return
     * @throws Exception
     */
    protected abstract String getCPUSerial() throws Exception;

    /**
     * 获取主板序列号
     * @return
     * @throws Exception
     */
    protected abstract String getMainBoardSerial() throws Exception;

    /**
     * 获取符合条件的InetAddress
     * @return
     * @throws Exception
     */
    protected List<InetAddress> getLocalAllInetAddress() throws Exception {
        List<InetAddress> result = new ArrayList<>();

        // 遍历所有网络接口
        for (Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
            NetworkInterface networkI = (NetworkInterface)networkInterfaces.nextElement();
            // 遍历网络接口下的ip
            for(Enumeration inetAddresses = networkI.getInetAddresses(); inetAddresses.hasMoreElements();) {
                InetAddress inetAddress = (InetAddress)inetAddresses.nextElement();
                //排除LoopbackAddress、SiteLocalAddress、LinkLocalAddress、MulticastAddress类型的IP地址
                if(!inetAddress.isLoopbackAddress()
                        && !inetAddress.isLinkLocalAddress() && !inetAddress.isMulticastAddress()){
                    result.add(inetAddress);
                }
            }
        }
        return result;
    }

    /**
     * 获取某个网络接口的mac地址
     * @param inetAddress
     * @return
     */
    protected String getMacByInetAddress(InetAddress inetAddress) {
        try {
            byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < mac.length; i++) {
                if(i != 0) {
                    sb.append("-");
                }
                //将十六进制byte转化为字符串
                String temp = Integer.toHexString(mac[i] & 0xff);
                if(temp.length() == 1) {
                    sb.append("0" + temp);
                } else {
                    sb.append(temp);
                }
            }
            return sb.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }
}
