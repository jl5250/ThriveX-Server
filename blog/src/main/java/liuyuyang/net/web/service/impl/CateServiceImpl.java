package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.execption.CustomException;
import liuyuyang.net.web.mapper.CateMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.web.service.CateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CateServiceImpl extends ServiceImpl<CateMapper, Cate> implements CateService {
    @Resource
    private CateMapper cateMapper;

    @Override
    public Boolean exist(Integer id) {
        QueryWrapper<Cate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("level", id);

        List<Cate> data = cateMapper.selectList(queryWrapper);

        if (!data.isEmpty()) {
            throw new CustomException(400, "ID为：" + id + "的分类中绑定了 " + data.size() + " 个二级分类，请解绑后重试");
        }

        return true;
    }

    @Override
    public Cate get(Integer id) {
        Cate data = cateMapper.selectById(id);

        if (data == null) {
            throw new CustomException(400, "该分类不存在");
        }

        // 获取当前分类下的所有子分类
        List<Cate> cates = cateMapper.selectList(null);
        data.setChildren(buildCateTree(cates, id));

        return data;
    }

    @Override
    public List<Cate> list(String pattern) {
        // 查询所有分类
        List<Cate> list = cateMapper.selectList(null);

        // 分类排序
        list.sort(Comparator.comparingInt(Cate::getOrder));

        // 如果参数是list则直接不进行递归处理
        if (Objects.equals(pattern, "list")) return list;

        // 构建分类树
        List<Cate> result = buildCateTree(list, 0);
        return result;
    }

    @Override
    public Page<Cate> paging(Integer page, Integer size) {
        // 查询所有分类
        List<Cate> list = cateMapper.selectList(null);
        // 分类排序
        list.sort(Comparator.comparingInt(Cate::getOrder));

        // 构建分类树
        List<Cate> cates = buildCateTree(list, 0);

        // 分页处理
        int start = (page - 1) * size;
        int end = Math.min(start + size, cates.size());
        List<Cate> pagedCates = cates.subList(start, end);

        // 返回分页结果
        Page<Cate> result = new Page<>(page, size);
        result.setRecords(pagedCates);
        result.setTotal(cates.size());

        return result;
    }

    @Override
    public List<CateArticleCount> cateArticleCount() {
        return cateMapper.cateArticleCount();
    }

    @Override
    // 无限级递归
    public List<Cate> buildCateTree(List<Cate> list, Integer lever) {
        List<Cate> children = new ArrayList<>();
        for (Cate cate : list) {
            if (cate.getLevel().equals(lever)) {
                cate.setChildren(buildCateTree(list, cate.getId()));
                children.add(cate);
            }
        }
        return children;
    }
}