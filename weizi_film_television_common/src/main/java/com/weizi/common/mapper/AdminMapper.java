package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weizi.common.domain.po.AdminTreeNode;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.vo.list.RoleTagVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMapper extends BaseMapper<AdminPO> {
    // 根据用户名查询管理员
    AdminPO selectAdminByUserName(@Param("account") String account, @Param("accountType") int accountType);
    // 获取管理员分页
    List<AdminVO> selectAdminPage(@Param("adminId") Long adminId, @Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);
    // 获取管理员总数
    int countTotal(@Param("adminId") Long adminId, @Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile);
    // 更新管理员头像
    int updateAdminAvatar(@Param("imageFileName") String imageFileName, @Param("adminId") Long adminId);
    // 管理员详情
    AdminVO searchAdminById(@Param("adminId") Long adminId);
    // 管理员树
    List<AdminTreeNode> findAllAdminIdAndParentId();
    // 角色tag列表
    List<RoleTagVO> getRoleTagList(@Param("roleIds") List<Long> roleIds);
    // 超级管理员可获取
    List<RoleTagVO> getAllRoleTagList();
}
