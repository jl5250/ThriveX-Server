package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.model.ArticleCate;
import liuyuyang.net.web.mapper.ArticleCateMapper;
import liuyuyang.net.web.mapper.CateMapper;
import liuyuyang.net.model.Cate;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.web.service.CateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CateServiceImpl extends ServiceImpl<CateMapper, Cate> implements CateService {
    @Resource
    private CateMapper cateMapper;
    @Resource
    private ArticleCateMapper articleCateMapper;

    // 判断是否存在二级分类
    @Override
    public Boolean isExistTwoCate(Integer id) {
        LambdaQueryWrapper<Cate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Cate::getLevel, id);

        List<Cate> data = cateMapper.selectList(lambdaQueryWrapper);

        if (!data.isEmpty())
            throw new CustomException(400, "ID为：" + id + "的分类中有 " + data.size() + " 个二级分类，请解绑后重试");

        return true;
    }

    // 判断该分类中是否有文章
    @Override
    public Boolean isCateArticleCount(Integer id) {
        LambdaQueryWrapper<ArticleCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ArticleCate::getCateId, id);

        List<ArticleCate> data = articleCateMapper.selectList(lambdaQueryWrapper);

        if (!data.isEmpty())
            throw new CustomException(400, "ID为：" + id + "的分类中有 " + data.size() + " 篇文章，请删除后重试");

        return true;
    }

    @Override
    public void del(Integer id) {
        Cate data = cateMapper.selectById(id);

        if (data == null) throw new CustomException("该分类不存在");

        if (isExistTwoCate(id) && isCateArticleCount(id)) {
            cateMapper.deleteById(id);
        }
    }

    @Override
    public void batchDel(List<Integer> ids) {
        for (Integer id : ids) del(id);
    }

    @Override
    public Cate get(Integer id) {
        Cate data = cateMapper.selectById(id);

        if (data == null) throw new CustomException(400, "该分类不存在");

        // 获取当前分类下的所有子分类
        List<Cate> cates = cateMapper.selectList(null);
        data.setChildren(buildCateTree(cates, id));

        return data;
    }

    @Override
    public List<Cate> list(String pattern) {
        // 分类排序
        LambdaQueryWrapper<Cate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Cate::getOrder);

        // 查询所有分类
        List<Cate> list = cateMapper.selectList(lambdaQueryWrapper);

        // 分类排序
        // list.sort(Comparator.comparingInt(Cate::getOrder));

        // 如果参数是list则返回列表，否则处理成树形结构
        if (Objects.equals(pattern, "list")) return list;

        // 构建分类树
        List<Cate> result = buildCateTree(list, 0);
        return result;
    }

    @Override
    public Page<Cate> paging(Integer page, Integer size) {
        // 分类排序
        LambdaQueryWrapper<Cate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Cate::getOrder);

        // 查询所有分类
        List<Cate> list = cateMapper.selectList(lambdaQueryWrapper);

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