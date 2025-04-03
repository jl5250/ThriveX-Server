package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.web.mapper.RecordMapper;
import liuyuyang.net.model.Record;
import liuyuyang.net.web.service.RecordService;
import liuyuyang.net.common.utils.YuYangUtils;
import liuyuyang.net.vo.FilterVo;
import liuyuyang.net.vo.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
    @Resource
    private RecordMapper recordMapper;
    @Resource
    private YuYangUtils yuYangUtils;

    @Override
    public List<Record> list(FilterVo filterVo) {
        QueryWrapper<Record> queryWrapper = yuYangUtils.queryWrapperFilter(filterVo, "content");
        List<Record> list = recordMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public Page<Record> paging(FilterVo filterVo, PageVo pageVo) {
        List<Record> list = list(filterVo);
        return yuYangUtils.getPageData(pageVo, list);
    }
}