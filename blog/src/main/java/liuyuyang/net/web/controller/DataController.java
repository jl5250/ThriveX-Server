package liuyuyang.net.web.controller;

import io.swagger.annotations.Api;
import liuyuyang.net.common.annotation.CheckRole;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.web.service.DataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "数据管理")
@RestController
@RequestMapping("/data")
@CheckRole
public class DataController {
    @Resource
    private DataService dataService;

    @PostMapping
    public Result<String> Backup() {
        dataService.Backup();
        return Result.success();
    }
}
