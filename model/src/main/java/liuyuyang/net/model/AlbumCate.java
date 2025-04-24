package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName(value = "album_cate")
public class AlbumCate {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "相册名称", example = "旅行", required = true)
    private String name;

    @ApiModelProperty(value = "文章封面链接", example = "http://123.com/images/example.jpg")
    private String cover;
}
