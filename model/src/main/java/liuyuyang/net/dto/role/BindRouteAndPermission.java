package liuyuyang.net.dto.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BindRouteAndPermission {
    @ApiModelProperty(value = "路由id列表", example = "[1,2,3]", required = true)
    List<Integer> route_ids;
    @ApiModelProperty(value = "权限id列表", example = "[3,2,1]", required = true)
    List<Integer> permission_ids;
}
