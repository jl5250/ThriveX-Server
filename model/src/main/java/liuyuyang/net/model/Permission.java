package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "权限id")
    private Integer id;

    @ApiModelProperty(value = "权限名称", example = "user:add", required = true)
    private String name;

    @ApiModelProperty(value = "权限描述", example = "新增用户", required = true)
    private String description;

    @TableField("`group`")
    @ApiModelProperty(value = "权限分组", example = "user", required = true)
    private String group;

    // @ApiModelProperty(value = "角色ID", example = "1", required = true)
    // private Integer roleId;
}
