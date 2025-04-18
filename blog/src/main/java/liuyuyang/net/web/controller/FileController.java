package liuyuyang.net.web.controller;

import cn.hutool.core.lang.Dict;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.qiniu.common.QiniuException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import liuyuyang.net.common.annotation.PremName;
import liuyuyang.net.common.execption.CustomException;
import liuyuyang.net.common.utils.Result;
import liuyuyang.net.common.utils.OssUtils;
import liuyuyang.net.web.service.impl.FileDetailService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.get.ListFilesResult;
import org.dromara.x.file.storage.core.get.RemoteDirInfo;
import org.dromara.x.file.storage.core.get.RemoteFileInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * 统一文件上传
 *
 * @author laifeng
 * @date 2024/12/14
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
@Transactional
public class FileController {
    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private FileDetailService fileDetailService;

    @PremName("file:add")
    @PostMapping
    @ApiOperation("文件上传")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 1)
    public Result<Object> add(@RequestParam(defaultValue = "") String dir,@RequestParam String platform, @RequestParam MultipartFile[] files) throws IOException {
        if (dir == null || dir.trim().isEmpty()) throw new CustomException(400, "请指定一个目录");

        List<String> urls = new ArrayList<>();

        String newPlatform=!Objects.equals(platform, "")? platform:OssUtils.getPlatform();
        for (MultipartFile file : files) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            int width = image.getWidth(); // 通过图片流获取图片宽度
            int height = image.getHeight(); // 通过图片流获取图片高度
            // 创建一个Dict来存储长和宽
            Dict dict = Dict.create();
            dict.set("width", width);
            dict.set("height", height);
            FileInfo result = fileStorageService.of(file)
                    .setAttr(dict)
                    .setPlatform(newPlatform)
                    .setPath(dir + '/')
                    .upload();

            if (result == null) throw new CustomException("上传文件失败");

            String url = result.getUrl();
            urls.add(url.startsWith("https://") ? url : "https://" + url);
        }

        return Result.success("文件上传成功：", urls);
    }

    @PremName("file:del")
    @DeleteMapping
    @ApiOperation("删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 2)
    public Result<String> del(@RequestParam String filePath) {
        String url = filePath.startsWith("https://") ? filePath : "https://" + filePath;
        boolean delete = fileStorageService.delete(url);
        return Result.status(delete);
    }

    @PremName("file:del")
    @DeleteMapping("/batch")
    @ApiOperation("批量删除文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 3)
    public Result batchDel(@RequestBody String[] pathList) throws QiniuException {
        for (String url : pathList) {
            boolean delete = fileStorageService.delete(url.startsWith("https://") ? url : "https://" + url);
            if (!delete) throw new CustomException("删除文件失败");
        }
        return Result.success();
    }

    @PremName("file:info")
    @GetMapping("/info")
    @ApiOperation("获取文件信息")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 4)
    public Result<FileInfo> get(@RequestParam String filePath) throws QiniuException {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(filePath);
        return Result.success(fileInfo);
    }

    @PremName("file:dir")
    @GetMapping("/dir")
    @ApiOperation("获取目录列表")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 5)
    public Result<List<Map>> getDirList() {
        ListFilesResult result = fileStorageService.listFiles()
                .setPlatform(OssUtils.getPlatform())
                .listFiles();

        // 获取文件列表
        List<Map> list = new ArrayList<>();
        List<RemoteDirInfo> fileList = result.getDirList();

        for (RemoteDirInfo item : fileList) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", item.getName());
            data.put("path", item.getOriginal());
            list.add(data);
        }

        return Result.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("获取指定目录中的文件")
    @ApiOperationSupport(author = "刘宇阳 | liuyuyang1024@yeah.net", order = 6)
    public Result<List<Map>> getFileList(@RequestParam String dir,@RequestParam(defaultValue = "defaultPlatform") String platform) {
        if (dir == null || dir.trim().isEmpty()) throw new CustomException(400, "请指定一个目录");
        String newPlatform=!Objects.equals(platform, "defaultPlatform")? platform:OssUtils.getPlatform();
        ListFilesResult result = fileStorageService.listFiles()
                .setPlatform(newPlatform)
                .setPath(dir + '/')
                .listFiles();

        // 获取文件列表
        List<Map> list = new ArrayList<>();
        List<RemoteFileInfo> fileList = result.getFileList();

        for (RemoteFileInfo item : fileList) {
            // 如果是目录就略过
            if (Objects.equals(item.getExt(), "")) continue;

            Map<String, Object> data = new HashMap<>();

            data.put("basePath", item.getBasePath());
            data.put("dir", dir);
            data.put("path", item.getBasePath() + item.getPath() + item.getFilename());
            data.put("name", item.getFilename());
            data.put("size", item.getSize());
            data.put("type", item.getExt());

            String url = item.getUrl();
            if (!url.startsWith("https://")) url = "https://" + url;
            Dict dict=fileDetailService.getByUrl(url).getAttr();
            if(!dict.isEmpty())
                data.put("arrt",fileDetailService.getByUrl(url).getAttr());
            data.put("url", url);

            list.add(data);
        }

        return Result.success(list);
    }

    @GetMapping("/dir/local")
    @ApiOperation("获取本地指定目录列表")
    @ApiOperationSupport(author = "王俊龙 | 1662528926@qq.com", order = 7)
    public Result<List<Map>> getLocalDirList(@RequestParam(defaultValue = "album/") String dir,@RequestParam(defaultValue = "local") String platform) {
        // 使用传入的platform，如果没有传入则使用OssUtils.getPlatform()
        ListFilesResult result = fileStorageService.listFiles()
                .setPath(dir)
                .setPlatform(platform)
                .listFiles();

        // 获取文件列表
        List<Map> list = new ArrayList<>();
        List<RemoteDirInfo> fileList = result.getDirList();

        for (RemoteDirInfo item : fileList) {
            Map<String, Object> data = new HashMap<>();
            data.put("name", item.getName());
            data.put("path", item.getOriginal());
            list.add(data);
        }

        return Result.success(list);
    }
}
