package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("record")
public class Record extends BaseModel {
    @ApiModelProperty(value = "内容", example = "大前端永远滴神！", required = true)
    private String content;
    @ApiModelProperty(value = "图片", example = "[]")
    private String images;
}
