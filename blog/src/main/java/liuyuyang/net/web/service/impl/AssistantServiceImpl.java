package liuyuyang.net.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.model.Assistant;
import liuyuyang.net.web.mapper.AssistantMapper;
import liuyuyang.net.web.service.AssistantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AssistantServiceImpl extends ServiceImpl<AssistantMapper, Assistant> implements AssistantService {

}