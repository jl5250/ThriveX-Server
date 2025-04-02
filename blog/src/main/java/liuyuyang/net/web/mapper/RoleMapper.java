package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Permission;
import liuyuyang.net.model.Role;
import liuyuyang.net.model.Route;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    // 查询指定角色的所有菜单
    @Select("select a.* from route a, role b, route_role c where c.route_id = a.id and c.role_id = b.id and b.id = #{id}")
    public List<Route> getRouteList(Integer id);

    // 查询指定角色的所有权限
    @Select("select p.* from role r, permission p, role_permission rp where  r.id = rp.role_id and p.id = rp.permission_id and r.id = #{id}")
    public List<Permission> getPermissionList(Integer id);
}
