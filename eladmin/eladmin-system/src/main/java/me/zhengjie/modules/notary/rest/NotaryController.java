/*
 *  Copyright 2019-2020 Zheng Jie
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
import me.zhengjie.modules.notary.domain.IdentityInfo;
import me.zhengjie.modules.notary.domain.dto.NotaryDTO;
import me.zhengjie.modules.notary.domain.vo.IdentityInfoQueryCriteria;
import me.zhengjie.modules.notary.service.IdentityInfoService;
import me.zhengjie.modules.system.domain.Role;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.domain.vo.UserQueryCriteria;
import me.zhengjie.modules.system.service.RoleService;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.utils.PageResult;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tianyi
 * @date 2024-06-01
 */
@Tag(name = "公证员管理")
@RestController
@RequestMapping("/api/notary")
@RequiredArgsConstructor
public class NotaryController {

    private final long NOTARY_ROLE_ID = 2;

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;
    private final IdentityInfoService identityInfoService;

    @Operation(summary = "查询用户")
    @GetMapping
    public ResponseEntity<PageResult<NotaryDTO>> queryUser(@ParameterObject UserQueryCriteria criteria) {
        criteria.setRoleId(NOTARY_ROLE_ID);
        PageResult<User> userPageResult = userService.queryAll(criteria, criteria.buildPage());
        List<Long> userIdList = userPageResult.list().stream().map(User::getId).toList();
        IdentityInfoQueryCriteria identityInfoQueryCriteria = new IdentityInfoQueryCriteria();
        identityInfoQueryCriteria.setUserIds(userIdList);
        Map<Long, IdentityInfo> identityInfoMap = identityInfoService.queryAll(identityInfoQueryCriteria)
                .stream().collect(Collectors.toMap(IdentityInfo::getUserId, v -> v, (v1, v2) -> v1));

        PageResult<NotaryDTO> pageResult = new PageResult<>(userPageResult.list().stream().map(item -> {
            NotaryDTO notaryDTO = new NotaryDTO();
            notaryDTO.setId(item.getId());
            notaryDTO.setUsername(item.getUsername());
            notaryDTO.setPhone(item.getPhone());
            Optional.ofNullable(identityInfoMap.get(item.getId())).ifPresent(info -> {
                notaryDTO.setIdName(info.getName());
                notaryDTO.setIdCode(info.getCode());
                notaryDTO.setSealUrl(info.getSealUrl());
            });
            return notaryDTO;
        }).toList(), userPageResult.total());

        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    @Log("新增用户")
    @Operation(summary = "新增用户")
    @PostMapping
    public ResponseEntity<Object> createUser(@Validated @RequestBody NotaryDTO notaryDTO) {
        Role role = roleService.findById(NOTARY_ROLE_ID);
        User user = new User();
        user.setId(notaryDTO.getId());
        user.setRoles(Collections.singleton(role));
        user.setUsername(notaryDTO.getUsername());
        user.setPhone(notaryDTO.getPhone());
        user.setEnabled(Boolean.TRUE);
        user.setPassword(passwordEncoder.encode(StringUtils.hasText(notaryDTO.getPassword()) ? notaryDTO.getPassword() : notaryDTO.getUsername()));
        userService.create(user);

        IdentityInfo identityInfo = new IdentityInfo();
        identityInfo.setUserId(user.getId());
        identityInfo.setCode(notaryDTO.getIdCode());
        identityInfo.setName(notaryDTO.getIdName());
        identityInfo.setSealUrl(notaryDTO.getSealUrl());
        identityInfo.setStatus(1);
        identityInfo.setPhone(notaryDTO.getPhone());
        identityInfoService.save(identityInfo);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Log("修改用户")
    @Operation(summary = "修改用户")
    @PutMapping
    public ResponseEntity<Object> updateUser(@Validated(User.Update.class) @RequestBody User resources) throws Exception {
        userService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除用户")
    @Operation(summary = "删除用户")
    @DeleteMapping
    public ResponseEntity<Object> deleteUser(@RequestBody Long id) {
        userService.delete(Collections.singleton(id));
        identityInfoService.deleteByUserId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
