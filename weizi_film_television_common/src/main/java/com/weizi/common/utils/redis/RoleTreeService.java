package com.weizi.common.utils.redis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.weizi.common.domain.po.RoleTreeNode;
import com.weizi.common.mapper.RoleMapper;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoleTreeService {
    @Resource
    private RoleMapper roleMapper; // 假设有一个 RoleMapper

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 构建角色树形结构并缓存到 Redis
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void refreshRoleTree() {
        List<RoleTreeNode> roles = roleMapper.findAllRoleIdAndParentId();
        // 构建树形结构
        Map<Long, Map<String, Object>> roleTree = buildRoleTree(roles);
        // 缓存到 Redis
        redisTemplate.opsForValue().set("role_tree", JSON.toJSONString(roleTree));
    }

    public boolean isInMyBranch(Long targetRoleId) {
        // 如果是超级管理员就有权限
        if (WeiZiSecurityUtil.isSuperAdmin()) return true;
        List<Long> currentRoleIds = WeiZiSecurityUtil.getRoleIdList();
        String roleTree = redisTemplate.opsForValue().get("role_tree");

        // 先判断是否为空，如果为空则刷新树
        if (roleTree == null) {
            refreshRoleTree();
            roleTree = redisTemplate.opsForValue().get("role_tree");
        }

        // 再次检查是否为空，如果仍为空，则返回false
        if (roleTree == null)
            return false;

        // 将JSON字符串转换为Map<Long, RoleTreeNode>
        Map<Long, RoleTreeNode> roleMap = JSON.parseObject(roleTree, new TypeReference<Map<Long, RoleTreeNode>>() {});

        // 转换后判空
        if (roleMap == null) return false;

        // 遍历当前角色ID数组
        for (Long currentRoleId : currentRoleIds) {
            // 获取目标角色节点
            RoleTreeNode targetRoleTreeNode = roleMap.get(targetRoleId);

            if (targetRoleTreeNode == null) {
                return false; // 如果目标角色不存在于树形结构中，则返回 false
            }

            // 向上查找目标角色的父节点，直到找到根节点或者当前角色节点
            while (targetRoleTreeNode != null && !targetRoleTreeNode.getRoleId().equals(currentRoleId)) {
                targetRoleTreeNode = roleMap.get(targetRoleTreeNode.getParentRoleId());
            }

            // 如果找到了当前角色节点，则说明目标角色在当前角色的分支内
            if (targetRoleTreeNode != null) {
                return true;
            }
        }

        // 如果所有角色都没有权限，则返回 false
        return false;
    }


    // 构建角色树形结构的Map
    public Map<Long, Map<String, Object>> buildRoleTree(List<RoleTreeNode> roles) {
        Map<Long, Map<String, Object>> roleMap = new HashMap<>();

        // 遍历角色列表，根据 parentRoleId 构建树形结构
        for (RoleTreeNode role : roles) {
            Long roleId = role.getRoleId();
            String roleName = role.getRoleName();
            Long parentRoleId = role.getParentRoleId();

            // 构建包含 roleId 和 parentRoleId 的 Map
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("roleId", roleId);
            roleInfo.put("roleName", roleName);
            roleInfo.put("parentRoleId", parentRoleId);

            roleMap.put(roleId, roleInfo);
        }

        return roleMap;
    }

    // 新增角色
    public void addRole(Long parentRoleId, String roleName, Long roleId) {
        // 获取当前角色树信息
        String roleTreeJson = redisTemplate.opsForValue().get("role_tree");

        if (roleTreeJson == null) {
            // 如果角色树信息为空，则刷新角色树
            refreshRoleTree();
            roleTreeJson = redisTemplate.opsForValue().get("role_tree");
        }

        // 将JSON字符串转换为Map<Long, Map<String, Object>>
        Map<Long, Map<String, Object>> roleMap = JSON.parseObject(roleTreeJson, new TypeReference<Map<Long, Map<String, Object>>>() {});

        if (roleMap == null) {
            // 如果角色Map为空，则创建一个新的角色Map
            roleMap = new HashMap<>();
        }

        // 将新角色信息添加到角色Map中
        Map<String, Object> roleInfo = new HashMap<>();
        roleInfo.put("roleId", roleId);
        roleInfo.put("roleName", roleName);
        roleInfo.put("parentRoleId", parentRoleId);
        roleMap.put(roleId, roleInfo);

        // 更新Redis中的角色树信息
        redisTemplate.opsForValue().set("role_tree", JSON.toJSONString(roleMap));
    }

    // 删除角色
    public void deleteRole(Long roleId) {
        // 获取当前角色树信息
        String roleTreeJson = redisTemplate.opsForValue().get("role_tree");

        if (roleTreeJson != null) {
            // 将JSON字符串转换为Map<Long, Map<String, Object>>
            Map<Long, Map<String, Object>> roleMap = JSON.parseObject(roleTreeJson, new TypeReference<Map<Long, Map<String, Object>>>() {});

            if (roleMap != null) {
                // 从角色Map中移除指定角色ID
                roleMap.remove(roleId);

                // 更新Redis中的角色树信息
                redisTemplate.opsForValue().set("role_tree", JSON.toJSONString(roleMap));
            }
        }
    }
}
