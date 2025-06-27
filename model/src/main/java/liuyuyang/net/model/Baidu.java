package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("baidu")
public class Baidu {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresTime;
    private LocalDateTime createTime;
}
