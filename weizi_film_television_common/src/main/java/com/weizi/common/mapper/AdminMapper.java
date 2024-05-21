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
    AdminPO selectAdminByUserName(@Param("account") String account, @Param("accountType") int accountType);

    // 获取管理员分页
    List<AdminVO> selectAdminPage(@Param("adminId") Long adminId, @Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    int countTotal(@Param("adminId") Long adminId, @Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile);

    int updateAdminAvatar(@Param("imageFileName") String imageFileName, @Param("adminId") Long adminId);

    AdminVO searchAdminById(@Param("adminId") Long adminId);

    List<AdminTreeNode> findAllAdminIdAndParentId();

    List<RoleTagVO> getRoleTagList(@Param("roleIds") List<Long> roleIds);
}
