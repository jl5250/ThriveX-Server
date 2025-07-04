package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.web.mapper.WebConfigMapper;
import liuyuyang.net.model.WebConfig;
import liuyuyang.net.web.service.WebConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WebConfigServiceImpl extends ServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {
    @Resource
    private WebConfigMapper webConfigMapper;

    @Override
    public Object get(String name){
        return webConfigMapper.selectOne(new LambdaQueryWrapper<WebConfig>().eq(WebConfig::getName, name)).getValue();
    }

    @Override
    public Map<String, Object> list(String group) {
        List<WebConfig> list;
        Map<String, Object> result;
        LambdaQueryWrapper<WebConfig> wrapper = new LambdaQueryWrapper<>();

        switch (group) {
            case "all":
                list = webConfigMapper.selectList(null);
                result = list.stream().collect(Collectors.toMap(WebConfig::getName, WebConfig::getValue));
                return result;
            case "web":
                wrapper.eq(WebConfig::getGroup, "web");
                break;
            case "layout":
                wrapper.eq(WebConfig::getGroup, "layout");
                break;
            case "system":
                return getSystemInfo();
            default:
                throw new CustomException(400, "未知的分组");
        }

        // 不等于 "all" or "system" 触发
        list = webConfigMapper.selectList(wrapper);
        result = list.stream().collect(Collectors.toMap(WebConfig::getName, WebConfig::getValue));
        return result;
    }

    @Override
    public void edit(String group, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            LambdaQueryWrapper<WebConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WebConfig::getName, name);
            WebConfig config = new WebConfig();
            config.setValue(value);
            webConfigMapper.update(config, wrapper);
        }
    }

    @Override
    public Map getSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();

        // 获取操作系统信息
        OperatingSystem os = systemInfo.getOperatingSystem();
        // 获取系统名称
        String osName = os.getFamily();
        // 获取系统版本
        String osVersion = os.getVersionInfo().getVersion();

        // 获取操作系统内存信息
        GlobalMemory memory = systemInfo.getHardware().getMemory();
        // 总内存量
        long totalMemory = memory.getTotal();
        // 可用内存量
        long availableMemory = memory.getAvailable();
        // 内存使用率
        double memoryUsage = (double) (totalMemory - availableMemory) / totalMemory * 100;

        Map<String, Object> result = new HashMap<>();
        result.put("osName", osName);
        result.put("osVersion", osVersion);
        result.put("totalMemory", (int) (totalMemory / (1024.0 * 1024 * 1024)));
        result.put("availableMemory", (int) (availableMemory / (1024.0 * 1024 * 1024)));
        result.put("memoryUsage", Float.valueOf(String.format("%.2f", memoryUsage)));

        return result;
    }
}