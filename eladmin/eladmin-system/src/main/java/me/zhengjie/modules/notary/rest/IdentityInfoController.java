/*
 *  Copyright 2019-2023 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.notary.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.base.ResponseWrap;
import me.zhengjie.modules.notary.domain.IdentityInfo;
import me.zhengjie.modules.notary.service.IdentityInfoService;
import me.zhengjie.modules.security.service.dto.JwtUserDto;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author rdbao
 * @date 2024-06-06
 **/
@RestController
@RequiredArgsConstructor
@Tag(name = "用户管理")
@RequestMapping("/api/identityInfo")
public class IdentityInfoController {

    private final IdentityInfoService identityInfoService;

    @GetMapping
    @Log("查询我的实名信息")
    @Operation(summary = "查询我的实名信息")
    public ResponseWrap<IdentityInfo> queryIdentityInfo() {
        Long userId;
        if (SecurityUtils.getCurrentUser() instanceof JwtUserDto jwtUserDto) {
            userId = Optional.ofNullable(jwtUserDto.getUser().getId()).orElseThrow(() -> new RuntimeException("获取用户信息失败"));
        } else {
            return ResponseWrap.error("获取用户信息失败");
        }
        return ResponseWrap.ok(identityInfoService.queryByUserId(userId));
    }

    @PostMapping
    @Log("保存我的实名信息")
    @Operation(summary = "新增我的实名信息")
    public ResponseWrap<Object> createIdentityInfo(@Validated @RequestBody IdentityInfo resources) {
        Long userId;
        if (SecurityUtils.getCurrentUser() instanceof JwtUserDto jwtUserDto) {
            userId = Optional.ofNullable(jwtUserDto.getUser().getId()).orElseThrow(() -> new RuntimeException("获取用户信息失败"));
        } else {
            return ResponseWrap.error("获取用户信息失败");
        }
        resources.setUserId(userId);
        IdentityInfo identityInfo = identityInfoService.queryByUserId(userId);
        if (identityInfo != null) {
            resources.setId(identityInfo.getId());
            identityInfoService.update(resources);
        } else {
            identityInfoService.save(resources);
        }
        return ResponseWrap.ok();
    }

}
