package liuyuyang.net.common.listener;

import liuyuyang.net.common.properties.BaiduProperties;
import liuyuyang.net.web.service.impl.BaiduServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class BaiduTokenStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    @Resource
    private BaiduServiceImpl baiduService;
    @Resource
    private BaiduProperties baiduProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 如果是关闭状态，则不检查
        if(!baiduProperties.getIsOpen()) return;

        log.info("应用启动完成，开始检查百度统计 Token 状态");

        try {
            // 检查是否需要初始化token
            baiduService.initializeToken();
            log.info("百度统计初始化检查完成");
        } catch (Exception e) {
            log.error("百度统计初始化失败", e);
        }
    }
} 