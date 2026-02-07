package com.job.job_studio.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.job.job_studio.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class LoginUser implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private Long studentId; // 扩展字段：关联的学生ID
    private String role;    // 扩展字段：角色

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public LoginUser(SysUser user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.studentId = user.getStudentId();
        this.role = user.getRole();
        // 将数据库角色转换为 Spring Security 权限对象
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // 将 SysUser 转换为 UserDetails
    public static LoginUser build(SysUser user) {
        return new LoginUser(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    // 以下方法全部返回 true 即可（账号不过期、不锁定等）
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}