package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.WebConfig;

import java.util.Map;

public interface WebConfigService extends IService<WebConfig> {
    Object get(String name);
    Map list(String group);
    void edit(String group, Map<String, String> config);
    Map getSystemInfo();
}
