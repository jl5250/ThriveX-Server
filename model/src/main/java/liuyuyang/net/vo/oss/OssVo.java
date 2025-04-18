package liuyuyang.net.vo.oss;

import liuyuyang.net.model.Oss;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * oss vo
 * @author laifeng
 * @version 1.0
 * @date 2025/1/7 9:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OssVo extends Oss {
    /**
     * 项目路径
     */
    private String projectPath;
}
