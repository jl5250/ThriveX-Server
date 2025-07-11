package liuyuyang.net.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.NoTokenRequired;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.dto.album.AlbumCateAddFormDTO;
import liuyuyang.net.model.AlbumCate;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.model.AlbumImage;
import liuyuyang.net.web.service.AlbumCateService;
import liuyuyang.net.common.utils.Paging;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "相册管理")
@RestController
@RequestMapping("/album/cate")
@Transactional
public class AlbumCateController {
    @Resource
    private AlbumCateService albumCateService;

    @PremName("album_cate:add")
    @PostMapping
    @ApiOperation("新增相册")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<String> add(@RequestBody AlbumCateAddFormDTO albumCateAddFormDTO) {
        albumCateService.add(albumCateAddFormDTO);
        return Result.success();
    }

    @PremName("album_cate:del")
    @DeleteMapping("/{id}")
    @ApiOperation("删除相册")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@PathVariable Integer id) {
        albumCateService.del(id);
        return Result.success();
    }

    @PremName("album_cate:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除相册")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result<String> batchDel(@RequestBody List<Integer> ids) {
        albumCateService.batchDel(ids);
        return Result.success();
    }

    @PremName("album_cate:edit")
    @PatchMapping
    @ApiOperation("编辑相册")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<String> edit(@RequestBody AlbumCate albumCate) {
        albumCateService.edit(albumCate);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取相册")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<AlbumCate> get(@PathVariable Integer id) {
        AlbumCate albumCate = albumCateService.get(id);
        if (albumCate == null) return Result.error("该相册不存在");
        return Result.success(albumCate);
    }

    @NoTokenRequired
    @PostMapping("/list")
    @ApiOperation("获取相册列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<AlbumCate>> list() {
        List<AlbumCate> albumCates = albumCateService.list();
        return Result.success(albumCates);
    }

    @NoTokenRequired
    @PostMapping("/paging")
    @ApiOperation("分页查询相册列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 7)
    public Result paging(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<AlbumCate> data = albumCateService.paging(page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }

    @NoTokenRequired
    @GetMapping("/{id}/images")
    @ApiOperation("获取指定相册中的所有照片")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 8)
    public Result getImagesByAlbumId(@PathVariable Integer id, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        Page<AlbumImage> data = albumCateService.getImagesByAlbumId(id, page, size);
        Map<String, Object> result = Paging.filter(data);
        return Result.success(result);
    }
}