package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import liuyuyang.net.dto.album.AlbumImageAddFormDTO;
import lombok.Data;

@Data
@TableName(value = "album_image")
public class AlbumImage extends AlbumImageAddFormDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;
}
