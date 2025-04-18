package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("route")
public class Route {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "路由ID")
    private Integer id;

    @ApiModelProperty(value = "路由路径", example = "/home", required = true)
    private String path;

    @ApiModelProperty(value = "路由描述", example = "首页路由", required = true)
    private String description;
}
