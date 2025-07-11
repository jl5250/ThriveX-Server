package liuyuyang.net.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import liuyuyang.net.dto.cate.CateFormDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cate")
public class Cate extends CateFormDTO {
    @TableField(exist = false)
    private List<Cate> children = new ArrayList<>();
}