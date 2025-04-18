package liuyuyang.net.dto.article;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.model.Article;
import lombok.Data;

import java.util.List;

@Data
public class ArticleDTO extends Article {
    // @TableField(exist = false)
    // @ApiModelProperty(value = "该文章所绑定的分类ID", example = "1,2,3", required = true)
    // private List<Integer> cateIds;
    //
    // @ApiModelProperty(value = "该文章所绑定的标签ID", example = "1,2,3")
    // private String tagIds;
}
