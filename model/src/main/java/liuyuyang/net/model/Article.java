package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@TableName("article")
public class Article extends BaseModel {
    @ApiModelProperty(value = "文章标题", example = "示例文章标题", required = true)
    private String title;

    @ApiModelProperty(value = "文章介绍", example = "示例文章介绍")
    private String description;

    @ApiModelProperty(value = "文章主要内容", example = "示例文章内容", required = true)
    private String content;

    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;

    @ApiModelProperty(value = "文章浏览量", example = "100")
    private Integer view;

    @ApiModelProperty(value = "文章评论数量", example = "20")
    private Integer comment;

    // @ApiModelProperty(value = "是否为文章草稿", example = "默认：0，草稿：1")
    // private Integer isDraft;
    //
    // @ApiModelProperty(value = "是否为加密文章", example = "默认：0，加密：1")
    // private Integer isEncrypt;
    //
    // @ApiModelProperty(value = "是否严格删除", example = "默认：0，严格删除：1")
    // private Integer isDel;

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的分类ID", example = "1,2,3")
    private List<Integer> cateIds;
    @TableField(exist = false)
    @ApiModelProperty(value = "分类列表")
    private List<Cate> cateList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "该文章所绑定的标签ID", example = "1,2,3")
    private List<Integer> tagIds;
    @TableField(exist = false)
    @ApiModelProperty(value = "标签列表")
    private List<Tag> tagList = new ArrayList<>();

    @TableField(exist = false)
    @ApiModelProperty(value = "文章配置项")
    private ArticleConfig config;

    @TableField(exist = false)
    private Map prev;
    @TableField(exist = false)
    private Map next;
}
