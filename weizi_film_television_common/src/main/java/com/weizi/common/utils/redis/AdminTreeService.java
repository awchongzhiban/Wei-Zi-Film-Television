package com.weizi.common.utils.redis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.weizi.common.domain.po.AdminTreeNode;
import com.weizi.common.mapper.AdminMapper;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AdminTreeService {
    @Resource
    private AdminMapper adminMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 构建管理员树形结构并缓存到 Redis
    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void refreshAdminTree() {
        List<AdminTreeNode> admins = adminMapper.findAllAdminIdAndParentId();
        // 构建树形结构
        Map<Long, Map<String, Object>> adminTree = buildAdminTree(admins);
        // 缓存到 Redis
        redisTemplate.opsForValue().set("admin_tree", JSON.toJSONString(adminTree));
    }

    public boolean isInMyBranch(Long currentAdminId, Long targetAdminId) {
        // 如果是超级管理员就有权限
        if (WeiZiSecurityUtil.isSuperAdmin()) return true;

        String adminTree = redisTemplate.opsForValue().get("admin_tree");

        // 先判断是否为空，如果为空则刷新树
        if (adminTree == null) {
            refreshAdminTree();
            adminTree = redisTemplate.opsForValue().get("admin_tree");
        }

        // 再次检查是否为空，如果仍为空，则返回false
        if (adminTree == null)
            return false;

        // 将JSON字符串转换为Map<Long, AdminTreeNode>
        Map<Long, AdminTreeNode> adminMap = JSON.parseObject(adminTree, new TypeReference<>() {
        });

        // 转换后判空
        if (adminMap == null) return false;

        // 获取目标管理员节点
        AdminTreeNode targetAdminNode = adminMap.get(targetAdminId);

        if (targetAdminNode == null) {
            return false; // 如果目标管理员不存在于树形结构中，则返回 false
        }

        // 向上查找目标管理员的父节点，直到找到根节点或者当前管理员节点
        while (targetAdminNode != null && !targetAdminNode.getAdminId().equals(currentAdminId)) {
            targetAdminNode = adminMap.get(targetAdminNode.getParentAdminId());
        }

        // 如果找到了当前管理员节点，则说明目标管理员在当前管理员的分支内
        return targetAdminNode != null;
    }

    // 构建管理员树形结构的Map
    public Map<Long, Map<String, Object>> buildAdminTree(List<AdminTreeNode> admins) {
        Map<Long, Map<String, Object>> adminMap = new HashMap<>();

        // 遍历管理员列表，根据 parentAdminId 构建树形结构
        for (AdminTreeNode admin : admins) {
            Long adminId = admin.getAdminId();
            Long parentAdminId = admin.getParentAdminId();

            // 构建包含 adminId 和 parentAdminId 的 Map
            Map<String, Object> adminInfo = new HashMap<>();
            adminInfo.put("adminId", adminId);
            adminInfo.put("parentAdminId", parentAdminId);

            adminMap.put(adminId, adminInfo);
        }

        return adminMap;
    }


    // 新增管理员
    public void addAdmin(Long parentAdminId, Long adminId) {
        // 获取当前管理员树信息
        String adminTreeJson = redisTemplate.opsForValue().get("admin_tree");

        if (adminTreeJson == null) {
            // 如果管理员树信息为空，则刷新管理员树
            refreshAdminTree();
            adminTreeJson = redisTemplate.opsForValue().get("admin_tree");
        }

        // 将JSON字符串转换为Map<Long, Map<String, Object>>
        Map<Long, Map<String, Object>> adminMap = JSON.parseObject(adminTreeJson, new TypeReference<Map<Long, Map<String, Object>>>() {});

        if (adminMap == null) {
            // 如果管理员Map为空，则创建一个新的管理员Map
            adminMap = new HashMap<>();
        }

        // 将新管理员信息添加到管理员Map中
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("adminId", adminId);
        adminInfo.put("parentAdminId", parentAdminId);
        adminMap.put(adminId, adminInfo);

        // 更新Redis中的管理员树信息
        redisTemplate.opsForValue().set("admin_tree", JSON.toJSONString(adminMap));
    }

    // 删除管理员
    public void deleteAdmin(Long adminId) {
        // 获取当前管理员树信息
        String adminTreeJson = redisTemplate.opsForValue().get("admin_tree");

        if (adminTreeJson != null) {
            // 将JSON字符串转换为Map<Long, Map<String, Object>>
            Map<Long, Map<String, Object>> adminMap = JSON.parseObject(adminTreeJson, new TypeReference<Map<Long, Map<String, Object>>>() {});

            if (adminMap != null) {
                // 从管理员Map中移除指定管理员ID
                adminMap.remove(adminId);

                // 更新Redis中的管理员树信息
                redisTemplate.opsForValue().set("admin_tree", JSON.toJSONString(adminMap));
            }
        }
    }

}
