package liuyuyang.net.web.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.dto.cate.CateFormDTO;
import liuyuyang.net.model.Cate;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.result.cate.CateArticleCount;
import liuyuyang.net.web.service.CateService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "分类管理")
@RestController
@RequestMapping("/cate")
@Transactional
public class CateController {
    @Resource
    private CateService cateService;

    @PremName("cate:add")
    @PostMapping
    @ApiOperation("新增分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.save(cate);
        return Result.success();
    }

    @PremName("cate:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        cateService.del(id);
        return Result.success();
    }

    @PremName("cate:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            boolean e = cateService.isExistTwoCate(id);
            if (!e) return Result.error();
            cateService.removeById(id);
        }

        return Result.success();
    }

    @PremName("cate:edit")
    @PatchMapping
    @ApiOperation("编辑分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody CateFormDTO cateFormDTO) {
        Cate cate = BeanUtil.copyProperties(cateFormDTO, Cate.class);
        cateService.updateById(cate);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取分类")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<Cate> get(@PathVariable Integer id) {
        Cate data = cateService.get(id);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取分类列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Cate>> list(@ApiParam(value = "默认为tree树性结构，设置为list表示列表结构") @RequestParam(defaultValue = "recursion") String pattern) {
        List<Cate> data = cateService.list(pattern);
        return Result.success(data);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询分类列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer size) {
        Page<Cate> data = cateService.paging(page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @GetMapping("/article/count")
    @ApiOperation("获取每个分类中的文章数量")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result<List<CateArticleCount>> cateArticleCount() {
        List<CateArticleCount> list = cateService.cateArticleCount();
        return Result.success(list);
    }
}
