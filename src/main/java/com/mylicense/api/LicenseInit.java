package com.mylicense.api;

import com.mylicense.common.ResMsg;
import com.mylicense.license.LicenseRunnable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * License对外接口
 */
@Slf4j
public class LicenseInit {

    public static ResMsg init() {

        // 将结果存入一个对象的变量中，然后另一个方法去取
        ResMsg resMsg = new ResMsg(-1, "fail", "证书验证失败!", null);
        // 周期性验证License有效性
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("license-verify-schedule-pool-%d").daemon(true).build());
        // 单个线程周期执行License验证
        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(new LicenseRunnable(), 0, 1, TimeUnit.SECONDS);
        try {
            // 阻塞等待结果
            Object o = scheduledFuture.get();
            log.info("+++++++++++License Verify End++++++++++++");
        } catch (InterruptedException e) {
            log.error("",e);
        } catch (ExecutionException e) {
            log.error("",e);
        }
        return resMsg;
    }
}
