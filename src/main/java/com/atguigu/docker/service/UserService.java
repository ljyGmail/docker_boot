package com.atguigu.docker.service;

import com.atguigu.docker.entities.User;
import com.atguigu.docker.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ClassName: UserService
 * Package: com.atguigu.docker.service
 * Description:
 *
 * @Author: ljy
 * @Create: 2025. 6. 3. 오전 12:15
 * @Version 1.0
 */
@Service
@Slf4j
public class UserService {
    public static final String CACHE_KEY_USER = "user:";
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    public void addUser(User user) {
        // 1 先插入mysql并成功
        int i = userMapper.insertSelective(user);
        if (i > 0) {
            // 2 需要再次查询一下mysql将数据捞回来并OK
            user = userMapper.selectByPrimaryKey(user.getId());
            // 3 将捞回来的user存进redis，完成新增功能的数据一致性。
            String key = CACHE_KEY_USER + user.getId();
            redisTemplate.opsForValue().set(key, user);
        }
    }

    public User findUserById(Integer id) {
        User user = null;
        String key = CACHE_KEY_USER + id;
        // 1 先从redis里面查询，如果有直接返回结果，如果没有再去查询mysql
        user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            // 2 redis里面没有，继续查询mysql
            user = userMapper.selectByPrimaryKey(id);
            if (user == null) {
                // 3.1 redis+mysql都无数据，记录下导致穿透的这个key回写redis
                return user;
            } else {
                // 3.2 mysql有，需要将数据写会redis，保证下一次的缓存命中
                redisTemplate.opsForValue().set(key, user);
            }
        }
        return user;
    }
}
