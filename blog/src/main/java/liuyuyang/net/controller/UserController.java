package liuyuyang.net.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.annotation.CheckRole;
import liuyuyang.net.dto.user.EditPassDTO;
import liuyuyang.net.dto.user.UserDTO;
import liuyuyang.net.dto.user.UserInfoDTO;
import liuyuyang.net.dto.user.UserLoginDTO;
import liuyuyang.net.model.Role;
import liuyuyang.net.model.User;
import liuyuyang.net.properties.JwtProperties;
import liuyuyang.net.result.Result;
import liuyuyang.net.service.RoleService;
import liuyuyang.net.service.UserService;
import liuyuyang.net.utils.JwtUtils;
import liuyuyang.net.utils.Paging;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.user.UserFilterVo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@Transactional
public class UserController {
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    @CheckRole
    @PostMapping
    @ApiOperation("新增用户")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody UserDTO user) {
        userService.add(user);
        return Result.success();
    }

    @CheckRole
    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        userService.del(id);
        return Result.success();
    }

    @CheckRole
    @DeleteMapping("/batch")
    @ApiOperation("批量删除用户")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        userService.delBatch(ids);
        return Result.success();
    }

    @CheckRole
    @PatchMapping
    @ApiOperation("编辑用户")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody UserInfoDTO user) {
        userService.edit(user);
        return Result.success();
    }

    @CheckRole
    @GetMapping("/{id}")
    @ApiOperation("获取用户")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<User> get(@PathVariable Integer id) {
        User data = userService.get(id);
        return Result.success(data);
    }

    @CheckRole
    @PostMapping("/list")
    @ApiOperation("获取用户列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<User>> list(@RequestBody UserFilterVo filterVo) {
        List<User> list = userService.list(filterVo);
        return Result.success(list);
    }

    @CheckRole
    @PostMapping("/paging")
    @ApiOperation("分页查询用户列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(UserFilterVo filterVo, PageVo pageVo) {
        Page<User> data = userService.paging(filterVo, pageVo);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result login(@RequestBody UserLoginDTO user) {
        User data = userService.login(user);

        Role role = roleService.getById(data.getRoleId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("user", data);
        claims.put("role", role);
        String token = JwtUtils.createJWT(jwtProperties.getSecretKey(), jwtProperties.getTtl(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", data);
        result.put("role", role);

        return Result.success("登录成功", result);
    }

    @PatchMapping("/pass")
    @ApiOperation("修改用户密码")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 9)
    public Result<String> editPass(@RequestBody EditPassDTO data) {
        userService.editPass(data);
        return Result.success("密码修改成功");
    }
}
