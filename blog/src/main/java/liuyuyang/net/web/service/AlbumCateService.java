package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.dto.album.AlbumCateAddFormDTO;
import liuyuyang.net.model.AlbumCate;
import liuyuyang.net.model.AlbumImage;

import java.util.List;

public interface AlbumCateService extends IService<AlbumCate> {
    void add(AlbumCateAddFormDTO albumCateAddFormDTO);
    
    void del(Integer id);
    
    void batchDel(List<Integer> ids);
    
    void edit(AlbumCate albumCate);
    
    AlbumCate get(Integer id);
    
    List<AlbumCate> list();
    
    Page<AlbumCate> paging(Integer page, Integer size);

    Page<AlbumImage> getImagesByAlbumId(Integer id, Integer page, Integer size);
}
