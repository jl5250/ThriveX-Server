package liuyuyang.net.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lyy.baidu")
public class BaiduProperties {
    private Boolean isOpen;
    private String clientKey;
    private String clientSecret;
    private String refreshToken;
    private String siteId;
}
