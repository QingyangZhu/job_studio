package com.job.job_studio.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.SysUser;
import com.job.job_studio.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // 转换为 Spring Security 需要的 UserDetails 对象
        return LoginUser.build(user);
    }
}