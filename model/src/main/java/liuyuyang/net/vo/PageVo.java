package liuyuyang.net.vo;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class PageVo {
    @ApiParam(defaultValue = "1", value = "默认为第1页")
    private Integer page = 1;
    @ApiParam(defaultValue = "5", value = "默认为每页显示5个数据")
    private Integer size = 5;
}
