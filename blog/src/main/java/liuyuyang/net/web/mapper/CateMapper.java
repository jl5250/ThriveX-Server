package liuyuyang.net.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CateMapper extends BaseMapper<Cate> {
    @Select("select MIN(c.name) AS name, count(*) as count from article a join article_cate ac on a.id = ac.article_id join cate c on c.id = ac.cate_id group by c.id")
    public List<CateArticleCount> cateArticleCount();
}
