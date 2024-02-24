package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.domain.entity.UmsMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UmsAdminMapper extends BaseMapper<UmsAdminEntity> {
    UmsAdminEntity selectAdminByUserName(@Param("account") String account,@Param("accountType") int accountType);

    // 获取管理员分页
    List<UmsAdminEntity> selectAdminPage(@Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    int countTotal(@Param("username") String username, @Param("nickname") String nickname, @Param("email") String email, @Param("mobile") String mobile);

    int updateAdminAvatar(@Param("imageFileName") String imageFileName, @Param("adminId") Long adminId);

    UmsAdminEntity searchAdminById(@Param("adminId") Long adminId);
}
