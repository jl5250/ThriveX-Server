package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;

import java.util.List;

public interface CateService extends IService<Cate> {
    Boolean exist(Integer cid);

    Cate get(Integer cid);

    List<Cate> list(String pattern);

    Page<Cate> paging(Integer page, Integer size);

    List<CateArticleCount> cateArticleCount();

    List<Cate> buildCateTree(List<Cate> list, Integer lever);
}
