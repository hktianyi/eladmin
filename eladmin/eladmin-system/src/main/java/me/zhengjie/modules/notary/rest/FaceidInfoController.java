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

import io.swagger.v3.oas.annotations.Hidden;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.notary.domain.FaceidInfo;
import me.zhengjie.modules.notary.service.FaceidInfoService;
import me.zhengjie.modules.notary.domain.vo.FaceidInfoQueryCriteria;
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
* @author rk
* @date 2024-06-07
**/
@Hidden
@RestController
@RequiredArgsConstructor
@Tag(name = "FaceInfo管理")
@RequestMapping("/api/faceidInfo")
public class FaceidInfoController {

    private final FaceidInfoService faceidInfoService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('faceidInfo:list')")
    public void exportFaceidInfo(HttpServletResponse response, FaceidInfoQueryCriteria criteria) throws IOException {
        faceidInfoService.download(faceidInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询FaceInfo")
    @Operation(summary = "查询FaceInfo")
    @PreAuthorize("@el.check('faceidInfo:list')")
    public ResponseEntity<PageResult<FaceidInfo>> queryFaceidInfo(FaceidInfoQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(faceidInfoService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增FaceInfo")
    @Operation(summary = "新增FaceInfo")
    @PreAuthorize("@el.check('faceidInfo:add')")
    public ResponseEntity<Object> createFaceidInfo(@Validated @RequestBody FaceidInfo resources){
        faceidInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改FaceInfo")
    @Operation(summary = "修改FaceInfo")
    @PreAuthorize("@el.check('faceidInfo:edit')")
    public ResponseEntity<Object> updateFaceidInfo(@Validated @RequestBody FaceidInfo resources){
        faceidInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除FaceInfo")
    @Operation(summary = "删除FaceInfo")
    @PreAuthorize("@el.check('faceidInfo:del')")
    public ResponseEntity<Object> deleteFaceidInfo(@RequestBody List<Integer> ids) {
        faceidInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
