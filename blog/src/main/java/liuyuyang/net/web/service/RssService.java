package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import liuyuyang.net.model.Rss;
import liuyuyang.net.vo.PageVo;

import java.util.List;

public interface RssService {
    List<Rss> list();
    
    Page<Rss> paging(PageVo pageVo);
}
