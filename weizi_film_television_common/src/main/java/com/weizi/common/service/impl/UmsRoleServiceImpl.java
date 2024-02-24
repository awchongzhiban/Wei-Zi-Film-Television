package com.weizi.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.entity.UmsRoleEntity;
import com.weizi.common.mapper.UmsRoleMapper;
import com.weizi.common.service.IUmsRoleService;
import org.springframework.stereotype.Service;

@Service
public class UmsRoleServiceImpl extends ServiceImpl<UmsRoleMapper, UmsRoleEntity> implements IUmsRoleService {
}
