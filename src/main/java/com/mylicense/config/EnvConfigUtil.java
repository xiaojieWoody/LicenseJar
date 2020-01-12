package com.mylicense.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Component
public class EnvConfigUtil {

    @Autowired
    private Environment environment;

    /**
     * 获取端口
     * @return
     */
    public String getPort(){
        return environment.getProperty("local.server.port");
    }

    /**
     * 获取ip
     * @return
     */
    public String getIp() {
        InetAddress localHost = null;
        try {
            localHost = Inet4Address.getLocalHost();
        } catch (UnknownHostException e) {
            log.error(e.getMessage(),e);
        }
        return localHost.getHostAddress();  // 返回格式为：xxx.xxx.xxx
        // localHost.getHostName() 一般是返回电脑用户名
    }

    /**
     * 获取请求IP和端口
     * @return
     */
    public String getIpPort() {
        return "http://" + getIp() + ":" + getPort() + "/";
    }
}
