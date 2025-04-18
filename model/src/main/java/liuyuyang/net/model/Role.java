package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "角色ID")
    private Integer id;

    @ApiModelProperty(value = "角色名称", example = "管理员", required = true)
    private String name;

    @ApiModelProperty(value = "角色标识", example = "admin", required = true)
    private String mark;

    @ApiModelProperty(value = "角色描述", example = "具有所有权限的管理员角色", required = true)
    private String description;
}
