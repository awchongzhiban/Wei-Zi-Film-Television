package com.weizi.auth.controller;

import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.service.IUmsAdminService;
import com.weizi.common.response.WeiZiResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/ums")
public class UmsAdminController {

    private final IUmsAdminService adminService;

    public UmsAdminController(IUmsAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     *  新增用户接口
     */
    @PostMapping("add")
    public WeiZiResult addAdmin(@RequestBody UmsAdminEntity admin) {
        boolean isSuccess = adminService.save(admin);
        if (isSuccess) {
            return WeiZiResult.success();
        }
        return WeiZiResult.error();
    }

    @GetMapping("list")
    public WeiZiResult searchAdminList() {
        List<UmsAdminEntity> adminEntityList = adminService.list();
        adminEntityList.forEach(System.out::println);
        return WeiZiResult.success(adminEntityList);
    }
}
