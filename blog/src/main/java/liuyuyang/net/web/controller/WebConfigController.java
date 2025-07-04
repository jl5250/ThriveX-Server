package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.WebConfigService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "网站配置")
@RestController
@RequestMapping("/web_config")
@Transactional
public class WebConfigController {
    @Resource
    private WebConfigService webConfigService;

    @GetMapping("/{name}")
    @ApiOperation("获取指定项目配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result get(@PathVariable("name") String name) {
        return Result.success(webConfigService.get(name));
    }

    @GetMapping("/list/{group}")
    @ApiOperation("获取项目配置列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result list(@PathVariable("group") String group) {
        return Result.success(webConfigService.list(group));
    }

    @PremName("config")
    @PatchMapping("/{group}")
    @ApiOperation("修改项目配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result edit(@PathVariable("group") String group, @RequestBody Map<String, String> config) {
        webConfigService.edit(group, config);
        return Result.success();
    }
}