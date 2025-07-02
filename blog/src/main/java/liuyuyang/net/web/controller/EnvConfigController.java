package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.common.annotation.CheckRole;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.model.EnvConfig;
import liuyuyang.net.web.service.EnvConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "环境配置管理")
@RestController
@RequestMapping("/env-config")
@CheckRole
public class EnvConfigController {
    @Resource
    private EnvConfigService envConfigService;

    @ApiOperation("获取全部环境配置列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    @GetMapping("/list")
    public Result<List<EnvConfig>> list() {
        List<EnvConfig> data = envConfigService.list();
        return Result.success("获取成功", data);
    }

    @ApiOperation("根据ID获取环境配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    @GetMapping("/{id}")
    public Result<EnvConfig> getById(@ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id) {
        EnvConfig envConfig = envConfigService.getById(id);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @ApiOperation("根据名称获取环境配置")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    @GetMapping("/name/{name}")
    public Result<EnvConfig> getByName(@ApiParam(value = "配置名称", required = true, example = "database_config") @PathVariable String name) {
        EnvConfig envConfig = envConfigService.getByName(name);
        return envConfig != null ? Result.success("获取成功", envConfig) : Result.error("配置不存在");
    }

    @ApiOperation("更新JSON配置值")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    @PatchMapping("/{id}/json")
    public Result<String> updateJsonValue(@ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id, 
                                          @ApiParam(value = "JSON配置值", required = true) @RequestBody Map<String, Object> jsonValue) {
        boolean success = envConfigService.updateJsonValue(id, jsonValue);
        return success ? Result.success("JSON配置更新成功") : Result.error("更新失败");
    }

    @ApiOperation("获取JSON配置中的特定字段值")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    @GetMapping("/{id}/field/{fieldName}")
    public Result<Object> getJsonFieldValue(@ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id, 
                                            @ApiParam(value = "字段名称", required = true, example = "host") @PathVariable String fieldName) {
        Object value = envConfigService.getJsonFieldValue(id, fieldName);
        return value != null ? Result.success("获取成功", value) : Result.error("字段不存在或配置为空");
    }

    @ApiOperation("更新JSON配置中的特定字段值")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    @PatchMapping("/{id}/field/{fieldName}")
    public Result<String> updateJsonFieldValue(@ApiParam(value = "环境配置ID", required = true, example = "1") @PathVariable Integer id, 
                                               @ApiParam(value = "字段名称", required = true, example = "host") @PathVariable String fieldName, 
                                               @ApiParam(value = "字段值", required = true) @RequestBody Object value) {
        boolean success = envConfigService.updateJsonFieldValue(id, fieldName, value);
        return success ? Result.success("字段更新成功") : Result.error("更新失败");
    }
} 