package com.jun.plugin.rbac.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.jun.plugin.rbac.security.common.ApiResponse;

/**
 * <p>
 * 测试Controller
 * </p>
 *
 * @package: com.xkcoding.rbac.security.controller
 * @description: 测试Controller
 * @author: yangkai.shen
 * @date: Created in 2018-12-10 15:44
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public ApiResponse list() {
        log.info("测试列表查询");
        return ApiResponse.ofMessage("测试列表查询");
    }

    @PostMapping
    public ApiResponse add() {
        log.info("测试列表添加");
        return ApiResponse.ofMessage("测试列表添加");
    }

    @PutMapping("/{id}")
    public ApiResponse update(@PathVariable Long id) {
        log.info("测试列表修改");
        return ApiResponse.ofSuccess("测试列表修改");
    }
}
