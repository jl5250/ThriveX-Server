package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.model.*;
import liuyuyang.net.web.mapper.*;
import liuyuyang.net.web.service.ArticleCateService;
import liuyuyang.net.web.service.ArticleService;
import liuyuyang.net.web.service.ArticleTagService;
import liuyuyang.net.web.service.CateService;
import liuyuyang.net.common.utils.YuYangUtils;
import liuyuyang.net.vo.PageVo;
import liuyuyang.net.vo.article.ArticleFillterVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private ArticleTagService articleTagService;
    @Resource
    private ArticleCateMapper articleCateMapper;
    @Resource
    private ArticleCateService articleCateService;
    @Resource
    private ArticleConfigMapper articleConfigMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private CateMapper cateMapper;
    @Resource
    private CateService cateService;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private YuYangUtils yuYangUtils;

    @Override
    public void add(Article article) {
        articleMapper.insert(article);

        // 新增分类
        List<Integer> cateIdList = article.getCateIds();
        if (!cateIdList.isEmpty()) {
            ArrayList<ArticleCate> cateArrayList = new ArrayList<>(cateIdList.size());
            for (Integer id : cateIdList) {
                ArticleCate articleCate = new ArticleCate();
                articleCate.setArticleId(article.getId());
                articleCate.setCateId(id);
                cateArrayList.add(articleCate);
            }
            articleCateService.saveBatch(cateArrayList);
        }

        // 新增标签
        List<Integer> tagIdList = article.getTagIds();
        if (!tagIdList.isEmpty()) {
            ArrayList<ArticleTag> tagArrayList = new ArrayList<>(tagIdList.size());
            for (Integer id : tagIdList) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(id);
                tagArrayList.add(articleTag);
            }
            articleTagService.saveBatch(tagArrayList);
        }

        // 新增文章配置
        ArticleConfig config = article.getConfig();
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(article.getId());
        articleConfig.setStatus(config.getStatus());
        articleConfig.setPassword(config.getPassword());

        articleConfigMapper.insert(articleConfig);
    }

    @Override
    public void del(Integer id, Integer is_del) {
        Article article = articleMapper.selectById(id);

        // 严格删除：直接从数据库删除
        if (is_del == 0) {
            // // 删除绑定的分类
            // QueryWrapper<ArticleCate> queryWrapperCate = new QueryWrapper<>();
            // queryWrapperCate.in("article_id", id);
            // articleCateMapper.delete(queryWrapperCate);
            //
            // // 删除绑定的标签
            // QueryWrapper<ArticleTag> queryWrapperTag = new QueryWrapper<>();
            // queryWrapperTag.in("article_id", id);
            // articleTagMapper.delete(queryWrapperTag);
            //
            // // 删除文章配置
            // QueryWrapper<ArticleConfig> queryWrapperArticleConfig = new QueryWrapper<>();
            // queryWrapperArticleConfig.in("article_id", article.getId());
            // articleConfigMapper.delete(queryWrapperArticleConfig);

            // 删除文章关联的数据
            delArticleCorrelationData(id);

            // 删除当前文章
            articleMapper.deleteById(id);
        }

        // 普通删除：更改 is_del 字段，到时候可以通过更改字段恢复
        if (is_del == 1) {
            article.setIsDel(1);
            articleMapper.updateById(article);
        }

        if (is_del != 0 && is_del != 1) {
            throw new CustomException(400, "参数有误：请选择是否严格删除");
        }
    }

    @Override
    public void reduction(Integer id) {
        Article article = articleMapper.selectById(id);
        article.setIsDel(0);
        articleMapper.updateById(article);
    }

    @Override
    public void delBatch(List<Integer> ids) {
        for (Integer id : ids) {
            // 删除文章关联的数据
            delArticleCorrelationData(id);
        }

        // 批量删除文章
        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", ids);
        articleMapper.delete(queryWrapperArticle);
    }

    @Override
    public void edit(Article article) {
        if (article.getCateIds() == null) throw new CustomException(400, "编辑失败：请绑定分类");

        // 删除文章关联的数据
        delArticleCorrelationData(article.getId());

        // 重新绑定分类
        for (Integer id : article.getCateIds()) {
            ArticleCate articleCate = new ArticleCate();
            articleCate.setArticleId(article.getId());
            articleCate.setCateId(id);
            articleCateMapper.insert(articleCate);
        }

        // 重新绑定标签
        for (Integer id : article.getTagIds()) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(article.getId());
            articleTag.setTagId(id);
            articleTagMapper.insert(articleTag);
        }

        // 重新绑定文章配置
        ArticleConfig config = article.getConfig();
        ArticleConfig articleConfig = new ArticleConfig();
        articleConfig.setArticleId(article.getId());
        articleConfig.setStatus(config.getStatus());
        articleConfig.setPassword(config.getPassword());
        articleConfigMapper.insert(articleConfig);

        // 修改文章
        articleMapper.updateById(article);
    }

    @Override
    public Article get(Integer id, String password, String token) {
        Article data = bindingData(id);

        String description = data.getDescription();
        String content = data.getContent();

        boolean isAdmin = !"".equals(token) && yuYangUtils.isAdmin(token);

        ArticleConfig config = data.getConfig();

        if (data.getIsEncrypt() == 0 && !password.isEmpty()) {
            throw new CustomException(610, "该文章不需要访问密码");
        }

        // 管理员可以查看任何权限的文章
        if (!isAdmin) {
            if (data.getIsDel() == 1) {
                throw new CustomException(404, "该文章已被删除");
            }

            if ("hide".equals(config.getStatus())) {
                throw new CustomException(611, "该文章已被隐藏");
            }

            // 如果有密码就必须通过密码才能查看
            if (data.getIsEncrypt() == 1) {
                // 如果需要访问密码且没有传递密码参数
                if (password.isEmpty()) {
                    throw new CustomException(612, "请输入文章访问密码");
                }

                data.setDescription("该文章需要密码才能查看");
                data.setContent("该文章需要密码才能查看");

                // 验证密码是否正确
                // if (config.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
                if (config.getPassword().equals(password)) {
                    data.setDescription(description);
                    data.setContent(content);
                } else {
                    throw new CustomException(613, "文章访问密码错误");
                }
            }
        }

        // 获取当前文章的创建时间
        String createTime = data.getCreateTime();

        // 查询上一篇文章
        QueryWrapper<Article> prevQueryWrapper = new QueryWrapper<>();
        prevQueryWrapper.lt("create_time", createTime).eq("is_del", 0).orderByDesc("create_time").last("LIMIT 1");
        Article prevArticle = articleMapper.selectOne(prevQueryWrapper);
        if (prevArticle != null) {
            Map<String, Object> resultPrev = new HashMap<>();
            resultPrev.put("id", prevArticle.getId());
            resultPrev.put("title", prevArticle.getTitle());
            data.setPrev(resultPrev);
        }

        // 查询下一篇文章
        QueryWrapper<Article> nextQueryWrapper = new QueryWrapper<>();
        nextQueryWrapper.gt("create_time", createTime).eq("is_del", 0).orderByAsc("create_time").last("LIMIT 1");
        Article nextArticle = articleMapper.selectOne(nextQueryWrapper);
        if (nextArticle != null) {
            Map<String, Object> resultNext = new HashMap<>();
            resultNext.put("id", nextArticle.getId());
            resultNext.put("title", nextArticle.getTitle());
            data.setNext(resultNext);
        }

        return data;
    }

    @Override
    public List<Article> list(ArticleFillterVo filterVo, String token) {
        QueryWrapper<Article> queryWrapper = queryWrapperArticle(filterVo);
        queryWrapper.eq("is_draft", filterVo.getIsDraft());
        queryWrapper.eq("is_del", filterVo.getIsDel());
        List<Article> list = articleMapper.selectList(queryWrapper);

        boolean isAdmin = yuYangUtils.isAdmin(token);
        list = list.stream()
                .map(article -> bindingData(article.getId()))
                // 如果是普通用户则不显示隐藏的文章，如果是管理员则显示
                .filter(article -> isAdmin || !Objects.equals(article.getConfig().getStatus(), "hide"))
                .collect(Collectors.toList());

        for (Article article : list) {
            // 如果有密码就必须通过密码才能查看
            if (article.getIsEncrypt() == 1) {
                article.setDescription("该文章是加密的");
                article.setContent("该文章是加密的");
            }
        }

        return list;
    }

    @Override
    public Page<Article> paging(ArticleFillterVo filterVo, PageVo pageVo, String token) {
        List<Article> list = list(filterVo, token);
        boolean isAdmin = yuYangUtils.isAdmin(token);
        if (!isAdmin) {
            list = list.stream().filter(k -> !Objects.equals(k.getConfig().getStatus(), "no_home")).collect(Collectors.toList());
        }
        Page<Article> result = yuYangUtils.getPageData(pageVo, list);
        return result;
    }

    @Override
    public Page<Article> getCateArticleList(Integer id, PageVo pageVo) {
        // 通过分类 id 查询出所有文章id
        QueryWrapper<ArticleCate> queryWrapperArticleCate = new QueryWrapper<>();
        queryWrapperArticleCate.in("cate_id", id);
        List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleCate).stream()
                .map(ArticleCate::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return new Page<>(pageVo.getPage(), pageVo.getSize(), 0);
        }

        // 构建文章查询条件
        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", articleIds)
                .eq("is_draft", 0)
                .eq("is_del", 0)
                .orderByDesc("create_time");

        // 查询文章列表
        Page<Article> page = new Page<>(pageVo.getPage(), pageVo.getSize());
        articleMapper.selectPage(page, queryWrapperArticle);

        // 绑定数据并处理加密文章
        page.setRecords(page.getRecords().stream().map(article -> {
            Article boundArticle = bindingData(article.getId());
            // 如果有密码，设置描述和内容为提示信息
            if (boundArticle.getIsEncrypt() == 1) {
                boundArticle.setDescription("该文章是加密的");
                boundArticle.setContent("该文章是加密的");
            }
            return boundArticle;
        }).collect(Collectors.toList()));

        return page;
    }

    @Override
    public Page<Article> getTagArticleList(Integer id, PageVo pageVo) {
        // 通过标签 id 查询出所有文章 id
        QueryWrapper<ArticleTag> queryWrapperArticleTag = new QueryWrapper<>();
        queryWrapperArticleTag.in("tag_id", id);
        List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleTag).stream()
                .map(ArticleTag::getArticleId)
                .collect(Collectors.toList());

        // 有数据就查询，没有就返回空数组
        if (articleIds.isEmpty()) {
            return new Page<>(pageVo.getPage(), pageVo.getSize(), 0);
        }

        // 构建文章查询条件
        QueryWrapper<Article> queryWrapperArticle = new QueryWrapper<>();
        queryWrapperArticle.in("id", articleIds)
                .eq("is_draft", 0)
                .eq("is_del", 0)
                .orderByDesc("create_time");

        // 查询文章列表
        Page<Article> page = new Page<>(pageVo.getPage(), pageVo.getSize());
        articleMapper.selectPage(page, queryWrapperArticle);

        // 绑定数据并处理加密文章
        page.setRecords(page.getRecords().stream().map(article -> {
            Article boundArticle = bindingData(article.getId());
            // 如果有密码，设置描述和内容为提示信息
            if (boundArticle.getIsEncrypt() == 1) {
                boundArticle.setDescription("该文章是加密的");
                boundArticle.setContent("该文章是加密的");
            }
            return boundArticle;
        }).collect(Collectors.toList()));

        return page;
    }

    @Override
    public List<Article> getRandomArticles(Integer count) {
        List<Integer> ids = articleMapper.selectList(null).stream()
                // 不能是加密文章，且能够正常显示
                .filter(k -> {
                    QueryWrapper<ArticleConfig> articleConfigQueryWrapper = new QueryWrapper<>();
                    articleConfigQueryWrapper.eq("article_id", k.getId());
                    ArticleConfig config = articleConfigMapper.selectOne(articleConfigQueryWrapper);
                    return "".equals(config.getPassword()) && Objects.equals(config.getStatus(), "default");
                })
                // 不能是已删除或草稿
                .filter(k -> k.getIsDel() == 0 && k.getIsDraft() == 0)
                .map(Article::getId)
                .collect(Collectors.toList());

        if (ids.size() <= count) {
            // 如果文章数量少于或等于需要的数量，直接返回所有文章
            return ids.stream()
                    .map(id -> get(id, "", ""))
                    .collect(Collectors.toList());
        }

        // 随机打乱文章ID列表
        Collections.shuffle(ids, new Random());

        // 选择前 count 个文章ID
        List<Integer> randomArticleIds = ids.subList(0, count);

        // 根据随机选择的文章ID获取文章
        return randomArticleIds.stream()
                .map(this::bindingData)
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> getRecommendedArticles(Integer count) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("view").last("LIMIT " + count);
        return list(queryWrapper);
    }

    @Override
    public void recordView(Integer id) {
        Article data = articleMapper.selectById(id);
        if (data == null) throw new CustomException(400, "获取失败：该文章不存在");
        data.setView(data.getView() + 1);
        articleMapper.updateById(data);
    }

    // 关联文章数据
    @Override
    public Article bindingData(Integer id) {
        Article data = articleMapper.selectById(id);

        if (data == null) throw new CustomException(400, "获取文章失败：该文章不存在");

        // 查询当前文章的分类ID
        QueryWrapper<ArticleCate> queryWrapperCateIds = new QueryWrapper<>();
        queryWrapperCateIds.eq("article_id", id);
        List<Integer> cate_ids = articleCateMapper.selectList(queryWrapperCateIds).stream().map(ArticleCate::getCateId).collect(Collectors.toList());

        // 如果有分类，则绑定分类信息
        if (!cate_ids.isEmpty()) {
            QueryWrapper<Cate> queryWrapperCateList = new QueryWrapper<>();
            queryWrapperCateList.in("id", cate_ids);
            List<Cate> cates = cateService.buildCateTree(cateMapper.selectList(queryWrapperCateList), 0);
            data.setCateList(cates);
        }

        // 查询当前文章的标签ID
        QueryWrapper<ArticleTag> queryWrapperTagIds = new QueryWrapper<>();
        queryWrapperTagIds.eq("article_id", id);
        List<Integer> tag_ids = articleTagMapper.selectList(queryWrapperTagIds).stream().map(ArticleTag::getTagId).collect(Collectors.toList());

        if (!tag_ids.isEmpty()) {
            QueryWrapper<Tag> queryWrapperTagList = new QueryWrapper<>();
            queryWrapperTagList.in("id", tag_ids);
            List<Tag> tags = tagMapper.selectList(queryWrapperTagList);
            data.setTagList(tags);
        }

        data.setComment(commentMapper.getCommentList(id).size());

        // 查找文章配置
        QueryWrapper<ArticleConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        ArticleConfig articleConfig = articleConfigMapper.selectOne(queryWrapper);
        data.setConfig(articleConfig);

        return data;
    }

    // 过滤文章数据
    @Override
    public QueryWrapper<Article> queryWrapperArticle(ArticleFillterVo filterVo) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        // 根据关键字通过标题过滤出对应文章数据
        if (filterVo.getKey() != null && !filterVo.getKey().isEmpty()) {
            queryWrapper.like("title", "%" + filterVo.getKey() + "%");
        }

        // 根据开始与结束时间过滤
        if (filterVo.getStartDate() != null && filterVo.getEndDate() != null) {
            queryWrapper.between("create_time", filterVo.getStartDate(), filterVo.getEndDate());
        } else if (filterVo.getStartDate() != null) {
            queryWrapper.ge("create_time", filterVo.getStartDate());
        } else if (filterVo.getEndDate() != null) {
            queryWrapper.le("create_time", filterVo.getEndDate());
        }

        // 根据分类id过滤
        if (filterVo.getCateId() != null) {
            QueryWrapper<ArticleCate> queryWrapperArticleIds = new QueryWrapper<>();
            queryWrapperArticleIds.in("cate_id", filterVo.getCateId());
            List<Integer> articleIds = articleCateMapper.selectList(queryWrapperArticleIds).stream().map(ArticleCate::getArticleId).collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in("id", articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in("id", -1); // -1 假设为不存在的ID
            }
        }

        // 根据标签id过滤
        if (filterVo.getTagId() != null) {
            QueryWrapper<ArticleTag> queryWrapperArticleIds = new QueryWrapper<>();
            queryWrapperArticleIds.in("tag_id", filterVo.getTagId());
            List<Integer> articleIds = articleTagMapper.selectList(queryWrapperArticleIds).stream().map(ArticleTag::getArticleId).collect(Collectors.toList());

            if (!articleIds.isEmpty()) {
                queryWrapper.in("id", articleIds);
            } else {
                // 添加一个始终为假的条件
                queryWrapper.in("id", -1); // -1 假设为不存在的ID
            }
        }

        return queryWrapper;
    }

    // 删除文章关联的数据
    public void delArticleCorrelationData(Integer id) {
        QueryWrapper<ArticleCate> queryWrapperCate = new QueryWrapper<>();
        queryWrapperCate.in("article_id", id);
        articleCateMapper.delete(queryWrapperCate);

        // 删除绑定的标签
        QueryWrapper<ArticleTag> queryWrapperTag = new QueryWrapper<>();
        queryWrapperTag.in("article_id", id);
        articleTagMapper.delete(queryWrapperTag);

        // 删除文章配置
        QueryWrapper<ArticleConfig> queryWrapperArticleConfig = new QueryWrapper<>();
        queryWrapperArticleConfig.in("article_id", id);
        articleConfigMapper.delete(queryWrapperArticleConfig);
    }
}