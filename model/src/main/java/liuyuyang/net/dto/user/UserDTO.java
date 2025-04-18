package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.BaseModel;
import lombok.Data;

@Data
public class UserDTO extends BaseModel {
    @ApiModelProperty(value = "用户账号", example = "liuyuyang", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", required = true)
    private String password;

    @ApiModelProperty(value = "用户名称", example = "宇阳", required = true)
    private String name;

    @ApiModelProperty(value = "用户角色ID", example = "1", required = true)
    private String roleId;
}
