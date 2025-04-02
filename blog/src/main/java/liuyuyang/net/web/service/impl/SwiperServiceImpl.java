package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.web.mapper.SwiperMapper;
import liuyuyang.net.model.Swiper;
import liuyuyang.net.web.service.SwiperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class SwiperServiceImpl extends ServiceImpl<SwiperMapper, Swiper> implements SwiperService {
    @Resource
    private SwiperMapper SwiperMapper;

    @Override
    public Page<Swiper> list(Integer page, Integer size) {
        QueryWrapper<Swiper> queryWrapper = new QueryWrapper<>();

        // 分页查询
        Page<Swiper> result = new Page<>(page, size);
        SwiperMapper.selectPage(result, queryWrapper);

        return result;
    }
}