package liuyuyang.net.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoDTO {
    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户账号", example = "liuyuyang", required = true)
    private String username;

    @ApiModelProperty(value = "用户名称", example = "宇阳", required = true)
    private String name;

    @ApiModelProperty(value = "用户介绍", example = "再渺小的星光，也有属于他的光芒!")
    private String info;

    @ApiModelProperty(value = "用户邮箱", example = "liuyuyang1024@yeah.net")
    private String email;

    @ApiModelProperty(value = "用户头像", example = "yuyang.jpg")
    private String avatar;

    @ApiModelProperty(value = "用户角色ID", example = "1", required = true)
    private String roleId;
}