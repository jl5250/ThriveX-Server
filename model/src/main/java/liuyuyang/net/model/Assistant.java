package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("assistant")
public class Assistant {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "助手名称", example = "DeepSeek", required = true)
    private String name;
    @ApiModelProperty(value = "API 地址", example = "https://api.deepseek.com")
    private String url;
    @TableField("`key`")
    @ApiModelProperty(value = "API 密钥", example = "xxxxxxxxxxxxxxxxxxxxxxxxxx")
    private String key;
    @ApiModelProperty(value = "API 模型", example = "deepseek-chat")
    private String model;
}