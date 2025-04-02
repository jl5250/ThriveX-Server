package liuyuyang.net.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import liuyuyang.net.annotation.PremName;
import liuyuyang.net.execption.CustomException;
import liuyuyang.net.web.mapper.PermissionMapper;
import liuyuyang.net.web.mapper.RolePermissionMapper;
import liuyuyang.net.model.Permission;
import liuyuyang.net.model.RolePermission;
import liuyuyang.net.properties.JwtProperties;
import liuyuyang.net.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class PremNameAspect {
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    // 定义切点，支持类和方法上的注解
    @Pointcut("@within(liuyuyang.net.annotation.PremName) || @annotation(liuyuyang.net.annotation.PremName)")
    private void cut() {
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        // 获取方法上的 @PremName 注解
        Optional<PremName> nameOpt = Optional.of(getMethodAnnotation(joinPoint).get());

        // 如果注解存在，进行权限验证
        nameOpt.ifPresent(name -> {
            String prem = name.value();
            log.info("权限名称：{}", prem);

            // 获取当前请求的上下文
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse response = attributes.getResponse();

                // 获取请求头中的 token
                String token = request.getHeader("Authorization");
                log.debug("Authorization Header: {}", token);

                // 如果 token 为 null，跳过权限校验
                if (token == null) {
                    log.info("Token为空，跳过权限校验");
                    throw new CustomException("Token 不能为空");
                }

                // 去掉 Bearer 前缀
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                Map<String, Object> role;

                // 解析 token 并获取角色信息
                try {
                    Claims claims = JwtUtils.parseJWT(jwtProperties.getSecretKey(), token);
                    role = (Map<String, Object>) claims.get("role");

                    // 查询指定角色的权限
                    LambdaQueryWrapper<RolePermission> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    roleLambdaQueryWrapper.eq(RolePermission::getRoleId, role.get("id"));
                    List<RolePermission> rolePermission = rolePermissionMapper.selectList(roleLambdaQueryWrapper);

                    // 收集所有权限ID
                    List<Integer> permissionIds = rolePermission.stream()
                            .map(RolePermission::getPermissionId)
                            .collect(Collectors.toList());

                    // 如果没有权限ID，抛出异常
                    if (permissionIds.isEmpty()) {
                        throw new CustomException("暂无权限，请联系管理员");
                    }

                    // 查询所有权限
                    LambdaQueryWrapper<Permission> permissionLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    permissionLambdaQueryWrapper.in(Permission::getId, permissionIds);
                    List<Permission> permissions = permissionMapper.selectList(permissionLambdaQueryWrapper);

                    // 如果权限列表为空，抛出异常
                    if (permissions == null || permissions.isEmpty()) {
                        throw new CustomException("暂无权限，请联系管理员");
                    }
                } catch (Exception e) {
                    // 记录错误日志并抛出自定义异常
                    log.error("Token解析或权限查询出错", e);
                    response.setStatus(401);
                    throw new CustomException(401, e.getMessage());
                }

                log.info("角色ID：{}", role.get("id"));
            }
        });
    }

    // 获取当前方法上的 @PremName 注解
    private Optional<PremName> getMethodAnnotation(JoinPoint joinPoint) {
        return Optional.ofNullable(getCurrentMethod(joinPoint))
                .map(method -> method.getAnnotation(PremName.class));
    }

    // 获取当前执行的方法对象
    private Method getCurrentMethod(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = joinPoint.getTarget().getClass();
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (Exception e) {
            log.error("获取方法时出错", e);
        }
        return null;
    }
}
