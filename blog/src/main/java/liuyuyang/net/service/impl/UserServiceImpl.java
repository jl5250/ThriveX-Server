package liuyuyang.net.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import liuyuyang.net.dto.user.EditPassDTO;
import liuyuyang.net.execption.YuYangException;
import liuyuyang.net.mapper.UserMapper;
import liuyuyang.net.model.User;
import liuyuyang.net.service.UserService;
import liuyuyang.net.utils.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public Page<User> paging(Integer page, Integer size) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 分页查询
        Page<User> result = new Page<>(page, size);
        userMapper.selectPage(result, queryWrapper);

        for (User user : result.getRecords()) {
            user.setPassword("只有聪明的人才能看到密码");
        }

        return result;
    }

    @Override
    public void editPass(EditPassDTO data) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", data.getUsername());
        queryWrapper.eq("password", DigestUtils.md5DigestAsHex(data.getOldPassword().getBytes()));

        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new YuYangException(400, "用户名或旧密码错误");
        }

        user.setPassword(DigestUtils.md5DigestAsHex(data.getNewPassword().getBytes()));
        userMapper.updateById(user);
    }

    @Override
    public void register(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());

        User data = userMapper.selectOne(queryWrapper);

        // 判断用户名是否存在
        if (data != null) {
            throw new YuYangException(400, "该用户已存在：" + user.getUsername());
        }

        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        userMapper.insert(user);
    }

    @Override
    public User login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        User data = userMapper.selectOne(queryWrapper);

        if (data == null) {
            throw new YuYangException(400, "用户名或密码错误");
        }

        return data;
    }
}