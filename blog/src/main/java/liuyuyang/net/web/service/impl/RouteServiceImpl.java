package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.web.mapper.RouteMapper;
import liuyuyang.net.model.Route;
import liuyuyang.net.web.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {

}