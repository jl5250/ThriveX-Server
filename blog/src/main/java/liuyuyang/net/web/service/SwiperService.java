package liuyuyang.net.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import liuyuyang.net.model.Swiper;

public interface SwiperService extends IService<Swiper> {
    Page<Swiper> list(Integer page, Integer size);
}
