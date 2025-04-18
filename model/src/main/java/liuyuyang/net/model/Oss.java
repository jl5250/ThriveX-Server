package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("oss")
public class Oss {
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "存储平台")
    private String platform;

    @ApiModelProperty(value = "平台名称")
    @TableField(exist = false)
    private String platformName;

    @ApiModelProperty(value = "Access Key")
    private String accessKey;

    @ApiModelProperty(value = "Secret Key")
    private String secretKey;

    @ApiModelProperty(value = "地域")
    private String endPoint;

    @ApiModelProperty(value = "存储桶")
    private String bucketName;

    @ApiModelProperty(value = "域名")
    private String domain;

    @ApiModelProperty(value = "文件目录")
    private String basePath;

    /**
     * 是否启用 0:禁用 1：启用
     */
    @ApiModelProperty(value = "是否启用 0:禁用 1：启用")
    private Integer isEnable;
}
