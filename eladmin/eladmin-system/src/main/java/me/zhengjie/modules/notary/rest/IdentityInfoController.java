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

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.notary.domain.IdentityInfo;
import me.zhengjie.modules.notary.service.IdentityInfoService;
import me.zhengjie.modules.notary.domain.vo.IdentityInfoQueryCriteria;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhengjie.utils.PageResult;

/**
* @author rdbao
* @date 2024-06-06
**/
@RestController
@RequiredArgsConstructor
@Tag(name = "IdentityInfo管理")
@RequestMapping("/api/identityInfo")
public class IdentityInfoController {

    private final IdentityInfoService identityInfoService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('identityInfo:list')")
    public void exportIdentityInfo(HttpServletResponse response, IdentityInfoQueryCriteria criteria) throws IOException {
        identityInfoService.download(identityInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询IdentityInfo")
    @Operation(summary = "查询IdentityInfo")
    @PreAuthorize("@el.check('identityInfo:list')")
    public ResponseEntity<PageResult<IdentityInfo>> queryIdentityInfo(IdentityInfoQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(identityInfoService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增IdentityInfo")
    @Operation(summary = "新增IdentityInfo")
    @PreAuthorize("@el.check('identityInfo:add')")
    public ResponseEntity<Object> createIdentityInfo(@Validated @RequestBody IdentityInfo resources){
        identityInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改IdentityInfo")
    @Operation(summary = "修改IdentityInfo")
    @PreAuthorize("@el.check('identityInfo:edit')")
    public ResponseEntity<Object> updateIdentityInfo(@Validated @RequestBody IdentityInfo resources){
        identityInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除IdentityInfo")
    @Operation(summary = "删除IdentityInfo")
    @PreAuthorize("@el.check('identityInfo:del')")
    public ResponseEntity<Object> deleteIdentityInfo(@RequestBody List<Integer> ids) {
        identityInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
