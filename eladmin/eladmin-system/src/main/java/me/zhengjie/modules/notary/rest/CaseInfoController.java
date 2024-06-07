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
import me.zhengjie.modules.notary.domain.CaseInfo;
import me.zhengjie.modules.notary.service.CaseInfoService;
import me.zhengjie.modules.notary.domain.vo.CaseInfoQueryCriteria;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springdoc.api.annotations.ParameterObject;
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
@RestController
@RequiredArgsConstructor
@Tag(name = "订单管理")
@RequestMapping("/api/caseInfo")
public class CaseInfoController {

    private final CaseInfoService caseInfoService;

    @Log("导出数据")
    @Operation(summary = "导出数据", hidden = true)
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('caseInfo:list')")
    public void exportCaseInfo(HttpServletResponse response, CaseInfoQueryCriteria criteria) throws IOException {
        caseInfoService.download(caseInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询订单")
    @Operation(summary = "查询订单")
    @PreAuthorize("@el.check('caseInfo:list')")
    public ResponseEntity<PageResult<CaseInfo>> queryCaseInfo(@ParameterObject CaseInfoQueryCriteria criteria){
        return new ResponseEntity<>(caseInfoService.queryAll(criteria,criteria.buildPage()),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增订单")
    @Operation(summary = "新增订单")
    @PreAuthorize("@el.check('caseInfo:add')")
    public ResponseEntity<Object> createCaseInfo(@Validated @RequestBody CaseInfo resources){
        caseInfoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改订单")
    @Operation(summary = "修改订单")
    @PreAuthorize("@el.check('caseInfo:edit')")
    public ResponseEntity<Object> updateCaseInfo(@Validated @RequestBody CaseInfo resources){
        caseInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除订单")
    @Operation(summary = "删除订单")
    @PreAuthorize("@el.check('caseInfo:del')")
    public ResponseEntity<Object> deleteCaseInfo(@RequestBody List<Integer> ids) {
        caseInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
