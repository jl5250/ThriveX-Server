package liuyuyang.net.web.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.model.Permission;
import liuyuyang.net.web.service.PermissionService;
import liuyuyang.net.common.utils.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "权限管理")
@RestController
@RequestMapping("/permission")
@Transactional
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @PremName("permission:add")
    @PostMapping
    @ApiOperation("新增权限")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody Permission permission) {
        boolean res = permissionService.save(permission);
        return res ? Result.success() : Result.error();
    }

    @PremName("permission:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除权限")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        Permission data = permissionService.getById(id);
        if (data == null) return Result.error("该数据不存在");

        Boolean res = permissionService.removeById(id);
        return res ? Result.success() : Result.error();
    }

    @PremName("permission:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除权限")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        Boolean res = permissionService.removeByIds(ids);
        return res ? Result.success() : Result.error();
    }

    @PremName("permission:edit")
    @PatchMapping
    @ApiOperation("编辑权限")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody Permission permission) {
        boolean res = permissionService.updateById(permission);
        return res ? Result.success() : Result.error();
    }

    @PremName("permission:info")
    @GetMapping("/{id}")
    @ApiOperation("获取权限")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Permission> get(@PathVariable Integer id) {
        Permission data = permissionService.getById(id);
        return Result.success(data);
    }

    @PremName("permission:list")
    @GetMapping
    @ApiOperation("获取权限列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Permission>> list() {
        List<Permission> data = permissionService.list();
        return Result.success(data);
    }
}
