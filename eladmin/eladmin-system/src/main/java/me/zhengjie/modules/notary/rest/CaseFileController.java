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
import me.zhengjie.modules.notary.domain.CaseFile;
import me.zhengjie.modules.notary.service.CaseFileService;
import me.zhengjie.modules.notary.domain.vo.CaseFileQueryCriteria;
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
@Tag(name = "CaseFile管理")
@RequestMapping("/api/caseFile")
public class CaseFileController {

    private final CaseFileService caseFileService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('caseFile:list')")
    public void exportCaseFile(HttpServletResponse response, CaseFileQueryCriteria criteria) throws IOException {
        caseFileService.download(caseFileService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询CaseFile")
    @Operation(summary = "查询CaseFile")
    @PreAuthorize("@el.check('caseFile:list')")
    public ResponseEntity<PageResult<CaseFile>> queryCaseFile(CaseFileQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(caseFileService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增CaseFile")
    @Operation(summary = "新增CaseFile")
    @PreAuthorize("@el.check('caseFile:add')")
    public ResponseEntity<Object> createCaseFile(@Validated @RequestBody CaseFile resources){
        caseFileService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改CaseFile")
    @Operation(summary = "修改CaseFile")
    @PreAuthorize("@el.check('caseFile:edit')")
    public ResponseEntity<Object> updateCaseFile(@Validated @RequestBody CaseFile resources){
        caseFileService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除CaseFile")
    @Operation(summary = "删除CaseFile")
    @PreAuthorize("@el.check('caseFile:del')")
    public ResponseEntity<Object> deleteCaseFile(@RequestBody List<Integer> ids) {
        caseFileService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
