package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("article_config")
public class ArticleConfig {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "文章状态", example = "默认（default） 不在首页显示（no_home） 全站隐藏（hide）")
    private String status;

    @ApiModelProperty(value = "文章密码", example = "默认为空表示不加密")
    private String password;

    @ApiModelProperty(value = "文章ID", example = "1", required = true)
    private Integer articleId;
}
