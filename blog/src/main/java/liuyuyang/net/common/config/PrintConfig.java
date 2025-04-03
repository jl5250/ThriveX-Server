package liuyuyang.net.common.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class PrintConfig {
    private static final Logger logger = LoggerFactory.getLogger(PrintConfig.class);
    private final ConfigurableEnvironment environment;
    private final AtomicBoolean printed = new AtomicBoolean(false);

    private static final Map<String, String> CONFIG_MAPPINGS = new LinkedHashMap<>();

    static {
        CONFIG_MAPPINGS.put("server.port", "PORT");
        CONFIG_MAPPINGS.put("spring.datasource.url", "DB_INFO");
        CONFIG_MAPPINGS.put("spring.datasource.username", "DB_USERNAME");
        CONFIG_MAPPINGS.put("spring.datasource.password", "DB_PASSWORD");
        CONFIG_MAPPINGS.put("lyy.email.host", "EMAIL_HOST");
        CONFIG_MAPPINGS.put("lyy.email.port", "EMAIL_PORT");
        CONFIG_MAPPINGS.put("lyy.email.username", "EMAIL_USERNAME");
        CONFIG_MAPPINGS.put("lyy.email.password", "EMAIL_PASSWORD");
    }

    public PrintConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @EventListener
    public void printConfigurations(ApplicationStartedEvent event) {
         log.info("\n----------------------------------------------------------\n\t\t" +
                        "服务已启动: 欢迎使用 ThriveX 博客管理系统 \n\t\t" +
                        "接口地址: \thttp://localhost:{}/api\n\t\t" +
                        "API文档: \thttp://localhost:{}/doc.html\n\t\t" +
                        "加入项目交流群: liuyuyang2023\n" +
                        "----------------------------------------------------------",
                environment.getProperty("server.port"), environment.getProperty("server.port"));

        if (printed.compareAndSet(false, true)) {
            logger.info("=== 配置信息 ===");

            CONFIG_MAPPINGS.forEach((configPath, envVar) -> {
                String value = environment.getProperty(configPath);
                logger.info("{} = {}", envVar, value);
            });

            logger.info("=== 配置信息打印完成 ===");
        }
    }
} 