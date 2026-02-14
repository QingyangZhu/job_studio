package com.job.job_studio.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.dto.PasswordUpdateDTO;
import com.job.job_studio.dto.UserProfileUpdateDTO;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.entity.SysUser;
import com.job.job_studio.mapper.StudentInfoMapper;
import com.job.job_studio.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 核心辅助方法：通过用户名查找绑定的学号 (student_id)
     */
    private Long getStudentIdByUsername(String username) {
        // 1. 查询 sys_user 表
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", username);
        SysUser user = sysUserMapper.selectOne(query);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 返回该账号绑定的 student_id
        // 注意：如果是 admin，这个字段是 NULL，这是正常的
        return user.getStudentId();
    }

    /**
     * 获取当前用户的学生档案
     */
    public StudentInfo getStudentProfile(String username) {
        // 1. 先找到这个账号绑定的学号 (例如 'stu123' -> 250101)
        Long studentId = getStudentIdByUsername(username);

        // 2. 如果是管理员 (student_id 为 NULL)，直接返回 null，不报错
        if (studentId == null) {
            return null;
        }

        // 3. 用真正的学号去查档案
        return studentInfoMapper.selectById(studentId);
    }

    /**
     * 更新个人资料
     */
    @Transactional
    public void updateProfile(String username, UserProfileUpdateDTO dto) {
        // 1. 获取关联的学号
        Long studentId = getStudentIdByUsername(username);

        // 2. 如果是管理员 (NULL)，禁止操作并提示
        if (studentId == null) {
            throw new RuntimeException("当前账号 (" + username + ") 是管理员或未绑定学号，无法创建学生档案。");
        }

        // 3. 查询现有记录 (用 studentId 查，而不是 username)
        StudentInfo student = studentInfoMapper.selectById(studentId);

        if (student == null) {
            // 如果只有学号绑定关系，但 student_info 表里还没建档，新建一个
            student = new StudentInfo();
            student.setStudentId(studentId); // 绑定真正的数字学号
            // 给个默认名字
            student.setName(username);
        }

        // 4. 更新字段
        if (dto.getEmail() != null) student.setContactEmail(dto.getEmail());
        if (dto.getPhone() != null) student.setPhone(dto.getPhone());
        if (dto.getTargetJob() != null) student.setTargetJob(dto.getTargetJob());
        if (dto.getGithub() != null) student.setGithubLink(dto.getGithub());
        if (dto.getBio() != null) student.setBio(dto.getBio());

        // 5. 保存
        if (studentInfoMapper.selectById(studentId) == null) {
            studentInfoMapper.insert(student);
        } else {
            studentInfoMapper.updateById(student);
        }
    }

    /**
     * 修改密码 (逻辑不变，依然通过用户名查 sys_user)
     */
    public void updatePassword(String username, PasswordUpdateDTO dto) {
        QueryWrapper<SysUser> query = new QueryWrapper<>();
        query.eq("username", username);
        SysUser user = sysUserMapper.selectOne(query);

        if (user == null) throw new RuntimeException("用户不存在");

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(user);
    }
}